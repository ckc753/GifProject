package com.multi.chlru.gifproject.main;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.storage.StorageReference;
import com.multi.chlru.gifproject.GifItem;
import com.multi.chlru.gifproject.GlideApp;
import com.multi.chlru.gifproject.HannaFontActivity;
import com.multi.chlru.gifproject.R;
import com.multi.chlru.gifproject.load.DownGif;

import java.io.File;
import java.io.IOException;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class BigImageActivity extends HannaFontActivity {
    // FirebaseStorage storage;
    Query myquery;
    ImageView bigimage;
    String url;
    String gifname;
    String Pkkey;
    Button saveBtn;
    Button kakaoBtn;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    final String folderName = "움짤마켓";
    String tempFolderName="움짤마켓Temp";
    SweetAlertDialog sweetalert;
    File tempFile;
    LinearLayout btnlayout;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.big_image);
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        gifname = intent.getStringExtra("gifname");
        Pkkey=intent.getStringExtra("Pkkey");
        // Toast.makeText(getApplicationContext(), "url주소는 = "+url, Toast.LENGTH_SHORT).show();
        bigimage = (ImageView)findViewById(R.id.bigimage);
        saveBtn = (Button)findViewById(R.id.saveBtn);
        kakaoBtn=(Button)findViewById(R.id.kakaoBtn);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();

        RequestOptions requestOptions = new RequestOptions();
        requestOptions
                .override(1200, 1000).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.RESOURCE);

        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress);
        progressBar.getIndeterminateDrawable().setColorFilter(Color.rgb(3,169,244), PorterDuff.Mode.MULTIPLY); //1. 빅이미지 카드뷰 Progressbar적용 (BigImage와 RecyclerAdapter)
        btnlayout = (LinearLayout) findViewById(R.id.btnLayout);
        GlideApp.with(BigImageActivity.this)
                .load(Uri.parse(url))
                .apply(requestOptions)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        btnlayout.setVisibility(View.VISIBLE);
                        return false;
                    }
                })
                .into(bigimage).clearOnDetach();
        kakaoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ContextCompat.checkSelfPermission(getApplicationContext(),android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(getApplicationContext(),android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                    //if (ActivityCompat.shouldShowRequestPermissionRationale(mactivity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                    sweetalert = new SweetAlertDialog(BigImageActivity.this, SweetAlertDialog.WARNING_TYPE);
                    sweetalert.setTitleText("＊＊＊ 경고 ＊＊＊");
                    sweetalert.setContentText("저장소 권한이 거부되었습니다. 저장기능을 사용하시려면 해당 권한을 직접 허용하셔야 합니다.");
                    sweetalert.setCancelText("취소");
                    sweetalert.setConfirmText("설정");
                    sweetalert.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            ActivityCompat.requestPermissions(BigImageActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                            sweetAlertDialog.cancel();
                        }
                    });
                    sweetalert.show();


                }else {
                    //String imgUrl2 = "https://firebasestorage.googleapis.com/v0/b/gifproject-60db8.appspot.com/o/GIF%2F20181125174348gifmarket?alt=media&token=7060e916-59dc-4f3f-bd94-4f302535cc4b";

                    DownGif downGif = new DownGif(BigImageActivity.this);
                    StorageReference storageRef = downGif.downloadUrl(gifname);
                    File fileDir = downGif.makeDir(tempFolderName);
                    tempFile = downGif.downloadLocal2(storageRef, fileDir);

                }
            }
        });
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (ContextCompat.checkSelfPermission(getApplicationContext(),android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(getApplicationContext(),android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                    //if (ActivityCompat.shouldShowRequestPermissionRationale(mactivity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                    sweetalert = new SweetAlertDialog(BigImageActivity.this, SweetAlertDialog.WARNING_TYPE);
                    sweetalert.setTitleText("＊＊＊ 경고 ＊＊＊");
                    sweetalert.setContentText("저장소 권한이 거부되었습니다. 저장기능을 사용하시려면 해당 권한을 직접 허용하셔야 합니다.");
                    sweetalert.setCancelText("취소");
                    sweetalert.setConfirmText("설정");
                    sweetalert.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            ActivityCompat.requestPermissions(BigImageActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                            sweetAlertDialog.cancel();
                        }
                    });
                    sweetalert.show();


                }else {

                    DownGif downGif = new DownGif(BigImageActivity.this);
                    StorageReference storageRef = downGif.downloadUrl(gifname);
                    File fileDir = downGif.makeDir(folderName);
                    downGif.downloadLocal(storageRef, fileDir);
                    Count(databaseReference,"down",Pkkey);
                    //downCount++;
                    //holder.downCount.setText(String.valueOf(items.get(position).getDownCount()+1));
                }
            }
        });
    }
    //count증가
    private void Count(DatabaseReference gifRef, final String type,String Pkkey){
        gifRef=gifRef.child("gif").child(Pkkey); //절대건드리지말것. gifRef.child("gif")를 작성하지않으면, 상위계층 gif가 다 지워지고 트랜잭션실행값만 새롭게 추가된다.
        //때문에, child("gif")해야 gif내부에 새롭게 추가된다. 안그러면 db 다 날라감.
        // Toast.makeText(mactivity,String.valueOf(gifRef),Toast.LENGTH_SHORT).show();
        gifRef.runTransaction(new Transaction.Handler() {

            @Override
            public Transaction.Result doTransaction(MutableData mutableData) { //mutableData는 트랜잭션사용시 가져오는 data
                GifItem gitem = mutableData.getValue(GifItem.class); //getValue=> gifItem.set~ => setValue방식으로 트랜잭션을 추가한다.
                if (gitem == null) {
                    return Transaction.success(mutableData);
                }

                else if(type.equals("down")){
                    Log.d("Transaction down : ",type);
                    gitem.setDownCount(gitem.getDownCount()+1);

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
    @Override
    public void onBackPressed() {
        if(tempFile!=null) {
           // Logger.e("Temp파일 지우기전 " + tempFile.toString());
            try {
                tempFile.getAbsoluteFile().delete();
               // tempFile.delete();
               // Logger.e("Temp파일 지우기1 " + tempFile.toString());
                if (tempFile.exists()) {
                  //  Logger.e("Temp파일 지우기2 " + tempFile.toString());
                    tempFile.getCanonicalFile().delete();
                    if (tempFile.exists()) {
                        getApplicationContext().deleteFile(tempFile.getName());
                   //     Logger.e("Temp파일 지우기3 " + tempFile.toString());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(tempFile)));//갤러리 갱신
          //  Logger.e("Temp파일 지우기4 " + tempFile.toString());
        }
        /*if(tempFile!=null){
            Logger.e("Temp파일 지우기전 " + tempFile.toString());
            tempFile.delete();
            Logger.e("Temp파일 지우기후 " + tempFile.toString());
        }*/

        finish();
    }
}
