package com.example.chlru.gifproject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;

public class BigImageActivity extends AppCompatActivity{
    FirebaseStorage storage;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    Query myquery;
    ImageView bigimage;
    String url;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.big_image);
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        //Toast.makeText(getApplicationContext(), "url주소는 = "+url, Toast.LENGTH_SHORT).show();

        bigimage = (ImageView) findViewById(R.id.bigimage);
        storage = FirebaseStorage.getInstance();
        myquery = databaseReference.child("gif").orderByChild("number");//gif 밑 number값으로 sort
        Glide.with(getApplicationContext())
                .load(Uri.parse(url))
                .into(bigimage);

    }
}
