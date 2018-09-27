package com.example.chlru.gifproject;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Fragment1 extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final ViewGroup view1 = (ViewGroup) inflater.inflate(R.layout.fragment1, container, false);
        ImageView imageView1 = (ImageView) view1.findViewById(R.id.image1);
        ImageView imageView2 = (ImageView) view1.findViewById(R.id.image2);

        FirebaseStorage fs = FirebaseStorage.getInstance();
        StorageReference ref = fs.getReference("sample01.gif");
        StorageReference ref2 = fs.getReference("sample02.gif");

        Glide.with(this)
                .load(ref)
                .into(imageView1);

        Glide.with(this)
                .load(ref2)
                .into(imageView2);


        return view1;
    }

}
