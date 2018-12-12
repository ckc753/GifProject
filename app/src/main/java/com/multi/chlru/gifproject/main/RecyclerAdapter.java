package com.multi.chlru.gifproject.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.storage.StorageReference;
import com.multi.chlru.gifproject.GifItem;
import com.multi.chlru.gifproject.R;
import com.multi.chlru.gifproject.load.DownGif;

import java.io.File;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class RecyclerAdapter extends RecyclerView.Adapter<ViewHolder> {
    Context context;
    int item_layout;
    final String folderName = "움짤마켓";
    String search;
    private ArrayList<GifItem> items = new ArrayList<GifItem>();
    Activity mactivity;
    SweetAlertDialog sweetalert;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    /*public RecyclerAdapter(Context context,String search) {
        this.context = context;
        this.search=search;
    }*/
    /*public RecyclerAdapter(Context context) {
        this.context = context;

    }*/
    public RecyclerAdapter(Context context, Activity mactivity) {
        this.context = context;
        this.mactivity=mactivity;
    }
    public RecyclerAdapter(Context context,String search, Activity mactivity) {
        this.context = context;
        this.search=search;
        this.mactivity=mactivity;
    }
    public void addItem(GifItem item) {
        items.add(item);
        notifyDataSetChanged();
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardview, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
       // Toast.makeText(context,String.valueOf(position)+" : "+String.valueOf(items.get(position).getPkKey()),Toast.LENGTH_LONG).show();
        final String urladd = items.get(position).getDownloadUrl();
        Typeface typeface = Typeface.createFromAsset(context.getAssets(),"BMHANNA_11yrs_ttf.ttf");
        holder.title.setTypeface(typeface);
        holder.saveBtn.setTypeface(typeface);
        holder.viewCount.setText(String.valueOf(items.get(position).getViewCount()));
        holder.downCount.setText(String.valueOf(items.get(position).getDownCount()));
        holder.goodCount.setText(String.valueOf(items.get(position).getGoodCount()));
        if(search!=null){
            SpannableStringBuilder sb=new SpannableStringBuilder();
            String str=items.get(position).getGifname();
            //검색어 색깔 바꾸기.
            sb.append(str);
            sb.setSpan(new ForegroundColorSpan(Color.parseColor("#03a9f4")),str.indexOf(search),str.indexOf(search)+search.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.title.setText(sb);
        }else {
            holder.title.setText(items.get(position).getGifname());
        }
       // Toast.makeText(context,String.valueOf(position)+" : "+String.valueOf(items.get(position).getJpgUrl()+" : "+String.valueOf(items.get(position).getGifname())),Toast.LENGTH_LONG).show();
        Glide.with(context)
                .load(Uri.parse(items.get(position).getJpgUrl()))
                .into(holder.image);
        //1. 카드뷰클릭시, BigImageActivity이동 (이미지커지도록)
        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  Toast.makeText(context,String.valueOf(items.get(position).getCaNum())+" : : : "+String.valueOf(items.get(position).getNumber()),Toast.LENGTH_LONG).show();
                Intent intent = new Intent(context,BigImageActivity.class);
                intent.putExtra("url",urladd);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                Count(databaseReference,"view",position);
               // Toast.makeText(context,"view : "+String.valueOf(items.get(position).getViewCount()),Toast.LENGTH_LONG).show();
                holder.viewCount.setText(String.valueOf(items.get(position).getViewCount()+1));
            }
        });
        //2. saveBtn클릭시, downGif이동 (파일저장되도록)
        holder.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (ContextCompat.checkSelfPermission(context,android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(context,android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                    //if (ActivityCompat.shouldShowRequestPermissionRationale(mactivity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                        sweetalert = new SweetAlertDialog(mactivity, SweetAlertDialog.WARNING_TYPE);
                        sweetalert.setTitleText("＊＊＊ 경고 ＊＊＊");
                        sweetalert.setContentText("저장소 권한이 거부되었습니다. 저장기능을 사용하시려면 해당 권한을 직접 허용하셔야 합니다.");
                        sweetalert.setCancelText("취소");
                        sweetalert.setConfirmText("설정");
                        sweetalert.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                ActivityCompat.requestPermissions(mactivity, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                                sweetAlertDialog.cancel();
                            }
                        });
                        sweetalert.show();
                        /*new AlertDialog.Builder(context).setTitle("알림").setMessage("저장소 권한이 거부되었습니다. 저장기능을 사용하시려면 해당 권한을 직접 허용하셔야 합니다.").setNeutralButton("설정", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                ActivityCompat.requestPermissions(mactivity, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                            }
                        }).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                //finish();
                            }
                        }).setCancelable(false).create().show();*/
                   // }
                    /*ActivityCompat.requestPermissions(mactivity,new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);*/

                }else {


                    DownGif downGif = new DownGif(mactivity);

                    String gif = items.get(position).getFilename();
                    StorageReference storageRef = downGif.downloadUrl(gif);
                    File fileDir = downGif.makeDir(folderName);
                    downGif.downloadLocal(storageRef, fileDir);
                    Count(databaseReference,"down",position);

                    holder.downCount.setText(String.valueOf(items.get(position).getDownCount()+1));
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    //count증가
    private void Count(DatabaseReference gifRef, final String type,int position){
        gifRef=gifRef.child("gif").child(items.get(position).getPkKey());
       // Toast.makeText(mactivity,String.valueOf(gifRef),Toast.LENGTH_SHORT).show();
        gifRef.runTransaction(new Transaction.Handler() {

            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                GifItem gitem = mutableData.getValue(GifItem.class);
                if (gitem == null) {
                    return Transaction.success(mutableData);
                }
                if(type.equals("view")){
                    Log.d("Transaction view : ",type);
                    gitem.setViewCount(gitem.getViewCount()+1);

                }
                else if(type.equals("down")){
                    Log.d("Transaction down : ",type);
                    gitem.setDownCount(gitem.getDownCount()+1);

                }
                else if(type.equals("good")){
                    Log.d("Transaction good : ",type);
                    gitem.setGoodCount(gitem.getGoodCount()+1);
                }
                mutableData.setValue(gitem);
                return Transaction.success(mutableData);
            }
            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                Log.d("TAG","Transaction onComplete : "+databaseError);
            }
        });


    }

}