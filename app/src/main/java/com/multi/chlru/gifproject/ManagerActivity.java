package com.multi.chlru.gifproject;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ManagerActivity extends HannaFontActivity {
    RecyclerManagerAdapter adapter;
    FirebaseStorage storage;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    Query myquery;
    RecyclerView recycler;
    AlertDialog.Builder dialog;
    Toolbar managertoolbar;
    SweetAlertDialog sweetalert;
    //Context context는 Fragment에서만 사용하는거ㅣ까 필요없다. (어댑터 쓰려면 사용할 것)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);

        //1. AlertDialog선언
        /*dialog = new AlertDialog.Builder(new ContextThemeWrapper(this,R.style.AlertDialog));
        dialog.setTitle("＊＊＊ 관리자 모드 ＊＊＊");
        dialog.setMessage("사용자가 올린 DB값을 조회/승인/삭제할 수 있습니다");
        dialog.setPositiveButton("확인", null);
        dialog.show();*/

        //2. SweetAlertDialog선언 (툴바보다 먼저 선언하지않으면 sweetalert는 사용x)
        sweetalert=new SweetAlertDialog(ManagerActivity.this, SweetAlertDialog.SUCCESS_TYPE);
        sweetalert.setTitleText("＊＊＊ 관리자 모드 ＊＊＊");
        sweetalert.setContentText("사용자가 올린 DB값을 조회/승인/삭제할 수 있습니다");
        sweetalert.setConfirmText("확인");
        sweetalert.show();

        managertoolbar = (Toolbar) findViewById(R.id.managertoolbar);
        setSupportActionBar(managertoolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recycler=(RecyclerView)findViewById(R.id.managerrecycler);//리사이클러뷰
        storage = FirebaseStorage.getInstance();
        adapter = new RecyclerManagerAdapter(getApplicationContext());//adapter
        recycler.setLayoutManager(new GridLayoutManager(getApplicationContext(),2));
        recycler.setAdapter(adapter);//adapter RecyclerView에 넣기

        //3. 업로드시 gifManager에 올라간 DB를 number순으로 정렬해서 조회하는 NO-SQL문
        myquery = databaseReference.child("gifManager").orderByChild("number");//gif 밑 number값으로 sort
        myquery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {//DB에 추가 있을때마다 실행

                final GifItem gitem = dataSnapshot.getValue(GifItem.class);//Gifitem형식으로 데이터 받아옴
                final String key=dataSnapshot.getKey();//PK
                final String jpgurl=gitem.getJpgUrl();
                final String url = gitem.getDownloadUrl();//url주소
                final String filename = gitem.getFilename();//파일이름(ex)sample.gif
                final String name = gitem.getGifname();//gif이름(ex)샘플움짤
                final String day = gitem.getDay();//날짜
                final int number = gitem.getNumber();//게시물번호
                final String category=gitem.getCategory();//카테고리
                final String member=gitem.getMember();//회원
                adapter.addItem(new GifItem(jpgurl,url, filename, name, day, number,category,member,key));//변화값 adapter에 추가
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                /*final GifItem gitem = dataSnapshot.getValue(GifItem.class);//Gifitem형식으로 데이터 받아옴
                final String url = gitem.getDownloadUrl();//url주소
                final String filename = gitem.getFilename();//파일이름(ex)sample.gif
                final String name = gitem.getGifname();//gif이름(ex)샘플움짤
                final String day = gitem.getDay();//날짜
                final int number = gitem.getNumber();//게시물번호
                final String category=gitem.getCategory();//카테고리
                Toast.makeText(getApplicationContext(),"카테고리 : "+category,Toast.LENGTH_SHORT).show();
                adapter.addItem(new GifItem(url, filename, name, day, number,category));//변화값 adapter에 추가*/
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //4. Toolbar_back기능넣기
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //작성안하면 바로종료
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //Glide.get(ManagerActivity.this).clearMemory();
                        Glide.get(getApplicationContext()).clearDiskCache();
                    }
                }).start();

                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    };
}
