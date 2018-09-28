package com.example.chlru.gifproject;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by hi on 2018-09-20.
 */

public class GifViewHolder extends RecyclerView.ViewHolder {
    TextView title;
    ImageView image;
    Button saveBtn;

    public AdapterView.OnItemClickListener itemClickListener;
    public void setOnItemClickListenner(AdapterView.OnItemClickListener listenner){
        itemClickListener=listenner;
    }
    public GifViewHolder(@NonNull View itemView) {
        super(itemView);

        title =(TextView)itemView.findViewById(R.id.title);

        image=(ImageView)itemView.findViewById(R.id.image);

        saveBtn = (Button)itemView.findViewById(R.id.saveBtn);
     }


}