package com.multi.chlru.gifproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;

public class BigImageActivity extends AppCompatActivity{
   // FirebaseStorage storage;
    Query myquery;
    ImageView bigimage;
    String url;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.big_image);
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
       // Toast.makeText(getApplicationContext(), "url주소는 = "+url, Toast.LENGTH_SHORT).show();
        bigimage = (ImageView) findViewById(R.id.bigimage);
       // storage = FirebaseStorage.getInstance();
        /*Glide.with(BigImageActivity.this).asGif()
                .load(Uri.parse(url))
                .apply(new RequestOptions().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.DATA))
                .into(bigimage).clearOnDetach();*/
        Glide.with(BigImageActivity.this).asGif()
                .load(Uri.parse(url))
                .apply(new RequestOptions().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.DATA))
                .into(bigimage).clearOnDetach();

    }

    @Override
    public void onBackPressed() {
      finish();
    }
}
