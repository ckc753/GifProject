package com.multi.chlru.gifproject.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.multi.chlru.gifproject.GifItem;
import com.multi.chlru.gifproject.GlideApp;
import com.multi.chlru.gifproject.R;
import com.multi.chlru.gifproject.homeKeyEvnet;

import java.io.File;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class RecyclerAdapter extends RecyclerView.Adapter<ViewHolder> {
    Context context;
    int item_layout;
    final String folderName = "움짤마켓";
    String tempFolderName="움짤마켓Temp";
    String search;
    private ArrayList<GifItem> items = new ArrayList<GifItem>();
    Activity mactivity;
    SweetAlertDialog sweetalert;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    int goodCount;
    int viewCount;
    int downCount;
    Uri providerUri;
    File tempFile;
    LinearLayout btnlayout;
    View v;
    homeKeyEvnet homekey=new homeKeyEvnet();

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
    public void clearAdapter(){
        items.clear();
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardview, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
        final String urladd = items.get(position).getDownloadUrl();
        Typeface typeface = Typeface.createFromAsset(context.getAssets(),"BMHANNA_11yrs_ttf.ttf");
        holder.title.setTypeface(typeface);
        holder.downText.setTypeface(typeface);
        holder.viewText.setTypeface(typeface);
        holder.viewCount.setTypeface(typeface);
        holder.downCount.setTypeface(typeface);
        holder.viewCount.setText(String.valueOf(items.get(position).getViewCount()));
        holder.downCount.setText(String.valueOf(items.get(position).getDownCount()));
     //   holder.goodCount.setText(String.valueOf(items.get(position).getGoodCount()));
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

        final ProgressBar progressBar = (ProgressBar) v.findViewById(R.id.cardProgress);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.rgb(3,169,244), PorterDuff.Mode.MULTIPLY); //2. 메인카드뷰에도 progressbar적용 (BigImage와 RecyclerAdapter)
        btnlayout = (LinearLayout) v.findViewById(R.id.btnLayout);
        GlideApp.with(context)
                .load(Uri.parse(items.get(position).getJpgUrl()))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.image);
        //1. 카드뷰클릭시, BigImageActivity이동 (이미지커지도록)
        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  Toast.makeText(context,String.valueOf(items.get(position).getCaNum())+" : : : "+String.valueOf(items.get(position).getNumber()),Toast.LENGTH_LONG).show();
                homekey.setHomeflag(true);
                Intent intent = new Intent(context,BigImageActivity.class);
                String gifname = items.get(position).getFilename();
                intent.putExtra("gifname",gifname);
                intent.putExtra("url",urladd);
                intent.putExtra("Pkkey",items.get(position).getPkKey());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);

                //2. view가 증가하도록 Count메소드실행.
                Count(databaseReference,"view",position);
               // viewCount++;
               // Toast.makeText(context,"view : "+String.valueOf(items.get(position).getViewCount()),Toast.LENGTH_LONG).show();
                holder.viewCount.setText(String.valueOf(items.get(position).getViewCount()+1));
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    //count증가
    private void Count(DatabaseReference gifRef, final String type,int position){
        gifRef=gifRef.child("gif").child(items.get(position).getPkKey()); //절대건드리지말것. gifRef.child("gif")를 작성하지않으면, 상위계층 gif가 다 지워지고 트랜잭션실행값만 새롭게 추가된다.
                                                                          //때문에, child("gif")해야 gif내부에 새롭게 추가된다. 안그러면 db 다 날라감.
       // Toast.makeText(mactivity,String.valueOf(gifRef),Toast.LENGTH_SHORT).show();
        gifRef.runTransaction(new Transaction.Handler() {

            @Override
            public Transaction.Result doTransaction(MutableData mutableData) { //mutableData는 트랜잭션사용시 가져오는 data
                GifItem gitem = mutableData.getValue(GifItem.class); //getValue=> gifItem.set~ => setValue방식으로 트랜잭션을 추가한다.
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