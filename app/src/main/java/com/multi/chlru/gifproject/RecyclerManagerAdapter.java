package com.multi.chlru.gifproject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

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

        final String urladd = items.get(position).getDownloadUrl();

        holder.title.setText(items.get(position).getGifname());
        Glide.with(context)
                .load(Uri.parse(items.get(position).getDownloadUrl()))
                .into(holder.image);
        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context,BigImageActivity.class);
                intent.putExtra("url",urladd);
                context.startActivity(intent);

            }
        });
        ////////////////////////////////////////////////////삭제
        holder.delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context, "!!"+String.valueOf(count), Toast.LENGTH_SHORT).show();
                delete_content(position);
               // Toast.makeText(context, "삭제!! "+items.get(position).getPkKey(), Toast.LENGTH_SHORT).show();
                Toast.makeText(context, "삭제!! "+items.get(position).getMember(), Toast.LENGTH_SHORT).show();
               Toast.makeText(context, "삭제!! "+items.get(position).getGifname(), Toast.LENGTH_SHORT).show();
               // Toast.makeText(context, "번호!! "+String.valueOf(position), Toast.LENGTH_SHORT).show();

            }
        });
        ///////////////////////////////////////////////////승인
        holder.agreeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String downloadUrl=items.get(position).getDownloadUrl();
                String filename=items.get(position).getFilename();
                String gifname=items.get(position).getGifname();
                String day=items.get(position).getDay();
                String category=items.get(position).getCategory();
                String member=items.get(position).getMember();
                //Toast.makeText(context, "카테고리 : "+category, Toast.LENGTH_SHORT).show();
                GifItem gitem = new GifItem(downloadUrl, filename, gifname, day,count-1,category,member);
                //gifItem gitem = new gifItem(filename, editText.getText().toString(), file);
                databaseReference.child("gif").push().setValue(gitem); //승인클릭시 아래의 메소드로인해 gifManager db삭제& 또 gif db항목으로 하나입력됨.
                delete_db(position);
                Toast.makeText(context, "승인!! "+items.get(position).getGifname(), Toast.LENGTH_SHORT).show();


            }
        });

    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }
    ////////////////////////////////////////삭제메소드

    private void delete_content(final int position) {
        //Toast.makeText(context, items.get(position).getFilename()+" 삭제하자 "+String.valueOf(storage.getReference()), Toast.LENGTH_SHORT).show();
        storage.getReference().child(items.get(position).getFilename()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {

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
}