package com.multi.chlru.gifproject;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;

public class Fragment_search extends HannaFontFragment {

    RecyclerAdapter adapter;
    FirebaseStorage storage;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    Query myquery;
    RecyclerView recycler;
    Context context;
    String search;
    ViewGroup view_sear;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view_sear = (ViewGroup) inflater.inflate(R.layout.fragment_sear1, container, false);

        try {
            search = getArguments().getString("SearchTxt");
        }catch (NullPointerException e){

        }
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        // Toast.makeText(getContext(), search+" 검색! ", Toast.LENGTH_SHORT).show();


        recycler=(RecyclerView)view_sear.findViewById(R.id.recycler_sear1);//리사이클러뷰
        storage = FirebaseStorage.getInstance();
        context=getContext();
        adapter = new RecyclerAdapter(context,search);//adapter
        recycler.setLayoutManager(new GridLayoutManager(getContext(),2));
        recycler.setAdapter(adapter);//adapter RecyclerView에 넣기

        /*if(search!=null){
            myquery=databaseReference.child("gif").orderByChild("gifname").startAt(search).endAt(search+"\uf8ff");

        }else {
            myquery = databaseReference.child("gif").orderByChild("number");//gif 밑 number값으로 sort
        }*/
        myquery = databaseReference.child("gif").orderByChild("number");

        myquery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {//DB에 추가 있을때마다 실행

                final GifItem gitem = dataSnapshot.getValue(GifItem.class);//Gifitem형식으로 데이터 받아옴
                final String url = gitem.getDownloadUrl();//url주소
                final String jpgurl=gitem.getJpgUrl();
                final String filename = gitem.getFilename();//파일이름(ex)sample.gif
                final String name = gitem.getGifname();//gif이름(ex)샘플움짤
                final String day = gitem.getDay();//날짜
                final int number = gitem.getNumber();//게시물번호
                if(name.contains(search)){
                    adapter.addItem(new GifItem(jpgurl,url, filename, name, day, number));//변화값 adapter에 추가
                    adapter.notifyDataSetChanged();}
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



        return view_sear;
    }
}
