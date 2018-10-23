package com.example.chlru.gifproject;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;

public class ManagerActivity extends AppCompatActivity {
    RecyclerAdapter adapter;
    FirebaseStorage storage;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    Query myquery;
    RecyclerView recycler;
    //Context context는 Fragment에서만 사용하는거ㅣ까 필요없다. (handler스려면 사용할 것)

    AlertDialog.Builder dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);

        dialog = new AlertDialog.Builder(new ContextThemeWrapper(this,R.style.AlertDialog));
        dialog.setTitle("＊＊＊ 관리자 모드 ＊＊＊");
        dialog.setMessage("사용자가 올린 DB값을 조회/승인/삭제할 수 있습니다");
        dialog.setPositiveButton("확인", null);
        dialog.show();

        recycler=(RecyclerView)findViewById(R.id.managerrecycler);//리사이클러뷰
        storage = FirebaseStorage.getInstance();
        adapter = new RecyclerAdapter(getApplicationContext());//adapter
        recycler.setLayoutManager(new GridLayoutManager(getApplicationContext(),2));
        recycler.setAdapter(adapter);//adapter RecyclerView에 넣기
        myquery = databaseReference.child("gifManager").orderByChild("number");//gif 밑 number값으로 sort
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
