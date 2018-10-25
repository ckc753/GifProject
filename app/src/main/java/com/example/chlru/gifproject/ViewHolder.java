package com.example.chlru.gifproject;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by hi on 2018-10-02.
 */

public class ViewHolder extends RecyclerView.ViewHolder {
    ImageView image;
    TextView title;
    CardView cardview;
    Button recBtn;
    Button saveBtn;

    public ViewHolder(View itemView) {
        super(itemView);
        image = (ImageView) itemView.findViewById(R.id.image);
        title = (TextView) itemView.findViewById(R.id.title);
        cardview = (CardView) itemView.findViewById(R.id.cardview);
        //recBtn = (Button) itemView.findViewById(R.id.recoBtn);
        saveBtn = (Button) itemView.findViewById(R.id.saveBtn);

    }
}