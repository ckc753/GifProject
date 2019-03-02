package com.multi.chlru.gifproject.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.multi.chlru.gifproject.GifItem;
import com.multi.chlru.gifproject.HannaFontActivity;
import com.multi.chlru.gifproject.R;
import com.multi.chlru.gifproject.homeKeyEvnet;

import java.util.Timer;
import java.util.TimerTask;

public class SearchActivity extends HannaFontActivity {

    RecyclerAdapter adapter;
    FirebaseStorage storage;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    Query myquery;
    RecyclerView recycler;
    Context context;
    String search;
    ViewGroup view_sear;
    TextView textView;
    TextView nosearchResult;
    RelativeLayout relative;
    int count = 0;

    @Override
    public void onBackPressed() {
        homekey.setHomeflag(true);
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        recycler=(RecyclerView)findViewById(R.id.recyclerSearch);//리사이클러뷰
        storage = FirebaseStorage.getInstance();
        // context=getContext();
        search=getIntent().getStringExtra("search");
        textView = (TextView)findViewById(R.id.sname);
        nosearchResult=(TextView) findViewById(R.id.nosearchResult);
        textView.setText("'"+search+"'");
        adapter = new RecyclerAdapter(getApplicationContext(),search,SearchActivity.this);//adapter
        recycler.setLayoutManager(new GridLayoutManager(SearchActivity.this,2));
        recycler.setAdapter(adapter);//adapter RecyclerView에 넣기


        myquery = databaseReference.child("gif").orderByChild("number");

        myquery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {//DB에 추가 있을때마다 실행

                final GifItem gitem = dataSnapshot.getValue(GifItem.class);//Gifitem형식으로 데이터 받아옴
                final String url = gitem.getDownloadUrl();//url주소
                final String jpgurl = gitem.getJpgUrl();
                final String filename = gitem.getFilename();//파일이름(ex)sample.gif
                final String name = gitem.getGifname();//gif이름(ex)샘플움짤
                final String day = gitem.getDay();//날짜
                final int number = gitem.getNumber();//게시물번호
                final String caNum=gitem.getCaNum();
                final String key=dataSnapshot.getKey();//PK
                final int viewCount=gitem.getViewCount();
                final int downCount=gitem.getDownCount();
                final int goodCount=gitem.getGoodCount();

                if(name.contains(search)){
                    count++;
                    //relative.setVisibility(View.GONE);
                    nosearchResult.setVisibility(View.GONE);
                    adapter.addItem(new GifItem(jpgurl, url, filename, name, day, number,caNum,key,viewCount,downCount,goodCount));//변화값 adapter에 추가
                    adapter.notifyDataSetChanged();
                }else{

                    if(count <1){
                        //Toast.makeText(SearchActivity.this, "count = " + count, Toast.LENGTH_SHORT).show();
                        nosearchResult.setVisibility(View.VISIBLE);

                    }
                }


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    homeKeyEvnet homekey = new homeKeyEvnet();
    /*public static boolean homeflag=false;
    public static boolean homestatus=false;*/
    Timer timer;
    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        //homestatus=true;
        homekey.setHomestatus(true);
        Log.d("홈", "서치 홈버튼 누른 상태 "+homekey.isHomestatus()+" "+homekey.isHomeflag());
        //Log.d("홈", "홈버튼 누른 상태 "+homestatus+" "+homeflag);
    }
    @Override
    protected void onPause() {

        Log.d("홈", "서치 pause상태 "+homekey.isHomestatus()+" "+homekey.isHomeflag());
        //Log.d("홈", "pause상태 "+homestatus+" "+homeflag);
        if(homekey.isHomestatus()==true&&homekey.isHomeflag()==false){
            Log.d("홈", "timer 실행상태 "+homekey.isHomestatus()+" "+homekey.isHomeflag());
            timer.schedule( new TimerTask()
                            {
                                public void run()
                                {
                                    finish();
                                    android.os.Process.killProcess(android.os.Process.myPid());
                                }
                            }
                    , 5000);
        }else{
        }

        super.onPause();
    }
    @Override
    public void onResume() {

        homekey.setHomestatus(false);
        homekey.setHomeflag(false);
        Log.d("홈", "서치 resume상태 "+homekey.isHomestatus()+" "+homekey.isHomeflag());
        if(timer!=null) {
            timer.cancel();
        }
        timer = new Timer();

        super.onResume();  // Always call the superclass method first

        // Get the Camera instance as the activity achieves full user focus

    }
}

