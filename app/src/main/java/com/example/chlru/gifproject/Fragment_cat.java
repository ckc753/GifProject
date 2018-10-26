package com.example.chlru.gifproject;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;

public class Fragment_cat extends Fragment implements MainActivity.onBackPressedListener{
    Fragment2 fragment2;
    RecyclerAdapter adapter;
    FirebaseStorage storage;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    Query myquery;
    RecyclerView recycler;
    Context context;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final ViewGroup view_cat = (ViewGroup) inflater.inflate(R.layout.fragment_cat, container, false);

        fragment2 = (Fragment2) new Fragment2();

        String Buttonname = getArguments().getString("Buttonname");
       // Toast.makeText(getContext(),"Buttonname="+Buttonname, Toast.LENGTH_SHORT).show();

        recycler=(RecyclerView)view_cat.findViewById(R.id.recycler_cat);//리사이클러뷰
        storage = FirebaseStorage.getInstance();
        context=getContext();
        adapter = new RecyclerAdapter(context);//adapter
        recycler.setLayoutManager(new GridLayoutManager(getContext(),2));
        recycler.setAdapter(adapter);//adapter RecyclerView에 넣기
        myquery = databaseReference.child("gif").orderByChild("category").equalTo(Buttonname);//gif 밑 category값으로 sort(현재 선택된 Buttonname값과 같은 것만)

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

       return view_cat;
    }

    @Override
    public void onBack() {
        Log.e("Other","onBack()");
        //리스너를 설정하기 위해 Activity 받기
        MainActivity activity = (MainActivity)getActivity();
        //한번 뒤로가기 버튼을 누르면 Listner를 null로 해제
        activity.setOnBackPressedListener(null);
        //Mainframent(우리는 Fragment1)로 교체
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container2, fragment2).commit();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.e("Other","onAttach()");
        ((MainActivity)context).setOnBackPressedListener(this);
    }

    @Override
    public void onDetach() {
        MainActivity activity = (MainActivity)getActivity();
        //한번 뒤로가기 버튼을 누르면 Listner를 null로 해제
        activity.setOnBackPressedListener(null);
        super.onDetach();
    }
}
