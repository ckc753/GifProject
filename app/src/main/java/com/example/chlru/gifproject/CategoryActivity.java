package com.example.chlru.gifproject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;

public class CategoryActivity extends AppCompatActivity {
    Intent intent;
    //GifAdapter adapter;
    RecyclerAdapter adapter;
    FirebaseStorage storage;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    Query myquery;
    RecyclerView recycler;
    Context context;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_main);

        intent = new Intent(this.getIntent());
        int result = intent.getIntExtra("주제 id",0);
        String name = intent.getStringExtra("Buttonname");

        recycler=(RecyclerView)findViewById(R.id.recycler);//리사이클러뷰
        storage = FirebaseStorage.getInstance();
        /*mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        mInputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);*/
        context=this;
        adapter = new RecyclerAdapter(context);//adapter
/*
        TextView textView = findViewById(R.id.resultView);

        textView.setText("주제 id = " + result + " 버튼이름 = " + name);*/
        recycler.setLayoutManager(new GridLayoutManager(this,2));

        recycler.setAdapter(adapter);//adapter RecyclerView에 넣기

        myquery = databaseReference.child("gif").orderByChild("number");//gif 밑 number값으로 sort


        myquery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {//DB에 추가 있을때마다 실행

                final GifItem gitem = dataSnapshot.getValue(GifItem.class);//Gifitem형식으로 데이터 받아옴

                final String url = gitem.getDownloadUrl();//url주소
                final String filename = gitem.getFilename();//파일이름(ex)sample.gif
                final String name = gitem.getGifname();//gif이름(ex)샘플움짤
                final String day = gitem.getDay();//날짜
                final int number = gitem.getNumber();//게시물번호


                adapter.addItem(new GifItem(url, filename, name, day, number));//변화값 adapter에 추가
                adapter.notifyDataSetChanged();

                /*mInputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);*/




            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                final GifItem gitem = dataSnapshot.getValue(GifItem.class);

                final String url = gitem.getDownloadUrl();
                final String filename = gitem.getFilename();
                final String name = gitem.getGifname();
                final String day = gitem.getDay();
                final int number = gitem.getNumber();
                adapter.addItem(new GifItem(url, filename, name, day, number));
                adapter.notifyDataSetChanged();

               // mInputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





    }
}
