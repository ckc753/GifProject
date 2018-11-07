package com.multi.chlru.gifproject;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.support.v4.app.ActivityCompat.requestPermissions;

public class RecyclerAdapter extends RecyclerView.Adapter<ViewHolder> {
    Context context;
    int item_layout;
    final String folderName = "움짤마켓";
    String search;
    private ArrayList<GifItem> items = new ArrayList<GifItem>();
    Activity mactivity;
    SweetAlertDialog sweetalert;
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
        final String urladd = items.get(position).getDownloadUrl();
        Typeface typeface = Typeface.createFromAsset(context.getAssets(),"BMHANNA_11yrs_ttf.ttf");
        holder.title.setTypeface(typeface);
        holder.saveBtn.setTypeface(typeface);
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

                Intent intent = new Intent(context,BigImageActivity.class);
                intent.putExtra("url",urladd);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
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
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }


}