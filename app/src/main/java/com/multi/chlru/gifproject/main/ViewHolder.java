package com.multi.chlru.gifproject.main;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.multi.chlru.gifproject.R;

//MainActivity에 보여지는 CardView항목들.
public class ViewHolder extends RecyclerView.ViewHolder {
    ImageView image;
    TextView title;
    CardView cardview;
    Button recBtn;
    Button saveBtn;
    TextView viewCount;
    TextView downCount;
    TextView goodCount;
    Button kakaoBtn;
    public ViewHolder(View itemView) {
        super(itemView);
        image = (ImageView) itemView.findViewById(R.id.image);
        title = (TextView) itemView.findViewById(R.id.title);
        cardview = (CardView) itemView.findViewById(R.id.cardview);
        saveBtn = (Button) itemView.findViewById(R.id.saveBtn);
        //recBtn = (Button) itemView.findViewById(R.id.recoBtn);
        viewCount=(TextView)itemView.findViewById(R.id.view_count);
        downCount=(TextView)itemView.findViewById(R.id.down_count);
        goodCount=(TextView)itemView.findViewById(R.id.good_count);
        kakaoBtn=(Button)itemView.findViewById(R.id.kakaoBtn);
    }
}