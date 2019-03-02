package com.multi.chlru.gifproject.manager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.multi.chlru.gifproject.GifItem;
import com.multi.chlru.gifproject.R;
import com.multi.chlru.gifproject.main.BigImageActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RecyclerManagerAdapter extends RecyclerView.Adapter<ViewManagerHolder> {
    Context context;
    private ArrayList<GifItem> items = new ArrayList<GifItem>();
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    Query myquery;
    int count;
    FirebaseStorage storage;
    public RecyclerManagerAdapter(Context context) {
        this.context = context;

    }
    public void addItem(GifItem item) {
        items.add(item);
        //notifyDataSetChanged();
    }
    @Override
    public ViewManagerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.manager_cardview, null);
        return new ViewManagerHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewManagerHolder holder, final int position) {
        Typeface typeface = Typeface.createFromAsset(context.getAssets(),"BMHANNA_11yrs_ttf.ttf");
        holder.title.setTypeface(typeface);
        holder.delBtn.setTypeface(typeface);
        holder.agreeBtn.setTypeface(typeface);
        storage = FirebaseStorage.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        myquery = databaseReference.child("gif").orderByChild("number").limitToFirst(1);
        myquery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                // count=(int) dataSnapshot.getChildrenCount();
                GifItem gitem = dataSnapshot.getValue(GifItem.class);
                count=gitem.getNumber();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //1. CardView기본변수선언 및 Image가져오기
        final String urladd = items.get(position).getDownloadUrl();

        holder.title.setText(items.get(position).getGifname());
        Glide.with(context)
                .load(Uri.parse(items.get(position).getJpgUrl()))
                .apply(new RequestOptions().override(1200,1000).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                .into(holder.image);
        //2. cardView클릭시, BigImageActivity이동 (이미지커지도록)
        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context,BigImageActivity.class);
                intent.putExtra("url",urladd);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

            }
        });
        //3. 삭제버튼::delBtn클릭시, RealTime DB value & Storage file 삭제
        holder.delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.agreeBtn.setClickable(false);
                holder.delBtn.setClickable(false);
                //Toast.makeText(context, "!!"+String.valueOf(count), Toast.LENGTH_SHORT).show();
                delete_content(position);
               // Toast.makeText(context, "삭제!! "+items.get(position).getPkKey(), Toast.LENGTH_SHORT).show();
              //  Toast.makeText(context, "삭제!! "+items.get(position).getMember(), Toast.LENGTH_SHORT).show();
             //  Toast.makeText(context, "삭제!! "+items.get(position).getGifname(), Toast.LENGTH_SHORT).show();
               // Toast.makeText(context, "번호!! "+String.valueOf(position), Toast.LENGTH_SHORT).show();

            }
        });
        //4. 승인버튼::agreeBtn클릭시, gifManager DB만 삭제하고 gif DB로 value push.
        holder.agreeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.agreeBtn.setClickable(false);
                holder.delBtn.setClickable(false);
                String jpgUrl=items.get(position).getJpgUrl();
                String downloadUrl=items.get(position).getDownloadUrl();
                String filename=items.get(position).getFilename();
                String gifname=items.get(position).getGifname();
                String day=items.get(position).getDay();
                String category=items.get(position).getCategory();
                String member=items.get(position).getMember();
                int number=count-1;
                int number2=1000000+number;
                String caNum=category+number2;
                //Toast.makeText(context, "카테고리 : "+category, Toast.LENGTH_SHORT).show();

                GifItem gitem = new GifItem(jpgUrl,downloadUrl, filename, gifname, day,caNum,number,category,member);
                //gifItem gitem = new gifItem(filename, editText.getText().toString(), file);
                databaseReference.child("gif").push().setValue(gitem); //승인클릭시 아래의 메소드로인해 gifManager db삭제& 또 gif db항목으로 하나입력됨.
                delete_db(position);
               // Toast.makeText(context, "승인!! "+items.get(position).getGifname(), Toast.LENGTH_SHORT).show();


            }
        });
        //5. 수정버튼
        holder.modifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String modifyfilename = holder.modifyEdit.getText().toString();
                if (modifyfilename.getBytes().length > 0) {
                    //holder.modifyBtn.setClickable(false); //여러번 수정을 위해
                    //holder.agreeBtn.setClickable(false); 수정되도록
                    //holder.delBtn.setClickable(false); 삭제되도록

                    GifItem gifItem = new GifItem();
                    String filename = items.get(position).getFilename(); //void -> String으로 변경했음
                    String jpgUrl = items.get(position).getJpgUrl();
                    String downloadUrl = items.get(position).getDownloadUrl();
                    String gifname = modifyfilename;
                    String day = items.get(position).getDay();
                    int number = items.get(position).getNumber();
                    String category = items.get(position).getCategory();
                    String member = items.get(position).getMember();
                    int viewCount = items.get(position).getViewCount();
                    int downCount = items.get(position).getDownCount();
                    int goodCount = items.get(position).getGoodCount();
                    GifItem gitem = new GifItem(jpgUrl, downloadUrl,filename ,gifname, day, number, category,member, viewCount, downCount, goodCount);

                    Toast.makeText(context, modifyfilename, Toast.LENGTH_SHORT).show();

                    //String key = databaseReference.child("gifManager").push().getKey(); //https://firebase.google.com/docs/database/android/save-data?hl=ko
                    String presentkey =
                            databaseReference.child("gifManager").child(items.get(position).getPkKey()).getKey(); //트랜잭션의 적용코드

                    Map<String, Object> postValues = gitem.toMap();
                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put(presentkey, postValues);
                    databaseReference.child("gifManager").updateChildren(childUpdates);


                    holder.title.setText(modifyfilename); //제목은 변경된 이름으로
                    holder.modifyEdit.setText(""); //수정됬다면, 공백으로 변경
                } else {
                    Toast.makeText(context, "수정할 파일명이 입력되지 않았습니다.", Toast.LENGTH_SHORT).show();
                }
            }});//holder.modifyBtn_end
    }
    //4. Count메소드
    @Override
    public int getItemCount() {
        return this.items.size();
    }
    ////////////////////////////////////////삭제메소드

    //5.1 삭제메소드 (Storage gif삭제)
    private void delete_content(final int position) {
        //Toast.makeText(context, items.get(position).getFilename()+" 삭제하자 "+String.valueOf(storage.getReference()), Toast.LENGTH_SHORT).show();
        storage.getReference().child("GIF").child(items.get(position).getFilename()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {

            @Override
            public void onSuccess(Void aVoid) {
                //Toast.makeText(context, "삭제 완료", Toast.LENGTH_SHORT).show();
                delete_content_jpg(position);
               // delete_db(position); //삭제버튼클릭시 -> 스토리지삭제 -> DB삭제
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Toast.makeText(context, "삭제 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //5.2 삭제메소드 (DB삭제)
    private void delete_db(final int position) {
        //Toast.makeText(context, "삭제 "+items.get(position).getDay(), Toast.LENGTH_SHORT).show();
        databaseReference.child("gifManager").child(items.get(position).getPkKey()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            items.remove(position); //승인클릭시 -> db삭제
             notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }
    //5.3 삭제메소드 (Storage jpg삭제)
    private void delete_content_jpg(final int position) {
        //Toast.makeText(context, items.get(position).getFilename()+" 삭제하자 "+String.valueOf(storage.getReference()), Toast.LENGTH_SHORT).show();
        storage.getReference().child("JPG").child(items.get(position).getFilename()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {

            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "삭제 완료", Toast.LENGTH_SHORT).show();
                delete_db(position); //삭제버튼클릭시 -> 스토리지삭제 -> DB삭제
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "삭제 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }
}