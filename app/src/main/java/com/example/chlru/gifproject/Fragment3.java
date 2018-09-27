package com.example.chlru.gifproject;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class Fragment3 extends Fragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup view3 = (ViewGroup) inflater.inflate(R.layout.fragment3, container, false);

        RecyclerView recyclerView = (RecyclerView) view3.findViewById(R.id.recyclerview);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(),2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        FirebaseStorage fs = FirebaseStorage.getInstance();
        StorageReference ref1 = fs.getReference("sample01.gif");
        StorageReference ref2 = fs.getReference("sample02.gif");
        StorageReference ref3 = fs.getReference("sample03.gif");
        StorageReference ref4 = fs.getReference("sample04.gif");

        List<Item> items = new ArrayList<>();
        Item[] item = new Item[4];
        item[0] = new Item(ref1, "마 이게 수비다");
        item[1] = new Item(ref2, "방방");
        item[2] = new Item(ref3, "어이쿠야");
        item[3] = new Item(ref4, "ㅋㅋㅋㅋ");
        //item[2] = new Item(R.drawable.zebry, "지브리");

        for (int i = 0; i < item.length; i++) {
            items.add(item[i]);
        }

        recyclerView.setAdapter(new RecyclerAdapter(getContext(), items, R.layout.activity_main));

        return view3;
    }


}
