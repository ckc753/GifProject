package com.multi.chlru.gifproject.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

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

public class MemberActivity extends HannaFontActivity {
    Fragment fragment1;
    RecyclerAdapter adapter;
    FirebaseStorage storage;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    Query myquery;
    RecyclerView recycler;
    Context context;
    String pkid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);
        /*final ViewGroup view_cat = (ViewGroup) inflater.inflate(R.layout.fragment_cat, container, false);
        fragment1 = new Fragment1();
        String pkid = getArguments().getString("pkid");*/
        // Toast.makeText(getContext(),"Buttonname="+Buttonname, Toast.LENGTH_SHORT).show();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        recycler=(RecyclerView)findViewById(R.id.memberrecycler);//리사이클러뷰
        storage = FirebaseStorage.getInstance();
        context=getApplicationContext();
        adapter = new RecyclerAdapter(getApplicationContext(),MemberActivity.this);//adapter
        recycler.setLayoutManager(new GridLayoutManager(getApplicationContext(),2));
        recycler.setAdapter(adapter);//adapter RecyclerView에 넣기
        pkid =getIntent().getStringExtra("pkid");
       // Toast.makeText(getApplicationContext(),pkid,Toast.LENGTH_SHORT).show();
        myquery = databaseReference.child("gif").orderByChild("member").equalTo(pkid);//gif아래에 member값으로 sort(현재사용중인 pkid값과 같은 것만)
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

                adapter.addItem(new GifItem(jpgurl, url, filename, name, day, number,caNum,key,viewCount,downCount,goodCount));//변화값 adapter에 추가
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
               // adapter.notifyDataSetChanged();
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.membertoolbar);
        setSupportActionBar(toolbar); //activity_main.xml의 액션바부분에 toolbar를 적용시킨다.
        getSupportActionBar().setDisplayShowTitleEnabled(false); //기본 타이틀 보여줄지 말지 설정
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 툴바를 이용한 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김 https://dreamaz.tistory.com/109
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }else if(id == android.R.id.home){ //1.4.1 home은 툴바(액션바)를 클릭했을경우이고, 나머지는 메뉴툴바이다.
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }//액션바 뒤로가기도 생성
    //12. 뒤로가기버튼 클릭시 Main으로 되돌아가는 Override메소드
    @Override
    public void onBackPressed() {
        homekey.setHomeflag(true);
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();



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
        Log.d("홈", "멤버 홈버튼 누른 상태 "+homekey.isHomestatus()+" "+homekey.isHomeflag());
        //Log.d("홈", "홈버튼 누른 상태 "+homestatus+" "+homeflag);
    }
    @Override
    protected void onPause() {

        Log.d("홈", "멤버 pause상태 "+homekey.isHomestatus()+" "+homekey.isHomeflag());
        //Log.d("홈", "pause상태 "+homestatus+" "+homeflag);
        if(homekey.isHomestatus()==true&&homekey.isHomeflag()==false){
            Log.d("홈", "멤버 timer 실행상태 "+homekey.isHomestatus()+" "+homekey.isHomeflag());
            timer.schedule( new TimerTask()
                            {
                                public void run()
                                {
                                    finish();
                                    android.os.Process.killProcess(android.os.Process.myPid());
                                }
                            }
                    , 600000);
        }else{
        }

        super.onPause();
    }
    @Override
    public void onResume() {

        homekey.setHomestatus(false);
        homekey.setHomeflag(false);
        Log.d("홈", "멤버 resume상태 "+homekey.isHomestatus()+" "+homekey.isHomeflag());
        if(timer!=null) {
            timer.cancel();
        }
        timer = new Timer();

        super.onResume();  // Always call the superclass method first

        // Get the Camera instance as the activity achieves full user focus

    }
}
