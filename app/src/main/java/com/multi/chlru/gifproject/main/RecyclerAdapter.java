package com.multi.chlru.gifproject.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
    public void clearAdapter(){
        items.clear();
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
        holder.downText.setTypeface(typeface);
        holder.viewText.setTypeface(typeface);
        //holder.saveBtn.setTypeface(typeface);
       // holder.kakaoBtn.setTypeface(typeface);
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
       // Toast.makeText(context,String.valueOf(position)+" : "+String.valueOf(items.get(position).getJpgUrl()+" : "+String.valueOf(items.get(position).getGifname())),Toast.LENGTH_LONG).show();
        GlideApp.with(context)
                .load(Uri.parse(items.get(position).getJpgUrl()))
                .placeholder(R.drawable.loadingimage)
                .into(holder.image);
        //1. 카드뷰클릭시, BigImageActivity이동 (이미지커지도록)
        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  Toast.makeText(context,String.valueOf(items.get(position).getCaNum())+" : : : "+String.valueOf(items.get(position).getNumber()),Toast.LENGTH_LONG).show();
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

        //2. 메인에서 보여지는 CardView 카카오버튼클릭시 downgif실행
       /* holder.kakaoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String imgUrl = items.get(position).getJpgUrl();
                String gifUrl = items.get(position).getDownloadUrl();
                //String imgUrl2 = "https://firebasestorage.googleapis.com/v0/b/gifproject-60db8.appspot.com/o/GIF%2F20181125174348gifmarket?alt=media&token=7060e916-59dc-4f3f-bd94-4f302535cc4b";

                DownGif downGif = new DownGif(mactivity);
                String gifname = items.get(position).getFilename();
                String filename = items.get(position).getFilename();
                StorageReference storageRef = downGif.downloadUrl(gifname);
                File fileDir = downGif.makeDir(tempFolderName);
                File tempFile = downGif.downloadLocal2(storageRef, fileDir);

                //tempFile.delete(); //RecyclerAdapter의 주석을 DownGif로 옮김. 카톡공유버튼클릭시, 사진을 갤러리저장 => 공유완료 => 갤러리에서 사진삭제하는 기능
                //Log.e("로그 템프파일 출력입니다 =  ", tempFile.toString()); //x
                //String tempUri = temp_path + filename;

                // Log.d("로그 user 출력한 값 = " , mAuth.getCurrentUser().toString());

               *//* FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    // do your stuff
                } else {
                    Logger.e("user가 널입니다!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    //signInAnonymously();
                }*//*

                // Logger.e("tempUri 경로 = " + context.getPackageName().toString());

                   *//* if (Build.VERSION.SDK_INT < 24) {
                        providerUri = Uri.fromFile(tempFile);
                    } else {
                        providerUri = FileProvider.getUriForFile(context, "{package_name}.fileprovider", tempFile);
                    }

                    try {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("image/*");

                        intent.putExtra(Intent.EXTRA_STREAM, providerUri);
                        //intent.putExtra(Intent.EXTRA_TEXT,gifUrl );
                        //Logger.e("imgurl = " + gifUrl);
                        //Logger.e("Uri.parse(imgUrl) = " + Uri.parse(gifUrl));
                        //intent.setPackage("com.kakao.talk");
                        Intent chooser = Intent.createChooser(intent, "공유하기");
                        mactivity.startActivity(chooser);
                        Logger.e("Temp파일 지우기전 " + tempFile.toString());
                        tempFile.delete();
                        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(tempFile)));
                        Logger.e("Temp파일 지운후 " + tempFile.toString());
                    } catch (Exception e) {
                        Log.e("로그exception  = ", e.toString());
                    }*//*
                    //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(imgUrl));

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
                        *//*new AlertDialog.Builder(context).setTitle("알림").setMessage("저장소 권한이 거부되었습니다. 저장기능을 사용하시려면 해당 권한을 직접 허용하셔야 합니다.").setNeutralButton("설정", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                ActivityCompat.requestPermissions(mactivity, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                            }
                        }).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                //finish();
                            }
                        }).setCancelable(false).create().show();*//*
                   // }
                    *//*ActivityCompat.requestPermissions(mactivity,new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);*//*

                }else {


                    DownGif downGif = new DownGif(mactivity);

                    String gif = items.get(position).getFilename();
                    StorageReference storageRef = downGif.downloadUrl(gif);
                    File fileDir = downGif.makeDir(folderName);
                    downGif.downloadLocal(storageRef, fileDir);
                    Count(databaseReference,"down",position);
                    //downCount++;
                    holder.downCount.setText(String.valueOf(items.get(position).getDownCount()+1));
                }
            }
        });
*/
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