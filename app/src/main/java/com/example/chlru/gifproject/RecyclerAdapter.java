package com.example.chlru.gifproject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<ViewHolder> {
    Context context;
    int item_layout;
    final String folderName = "움짤공방";
    private ArrayList<GifItem> items = new ArrayList<GifItem>();


    public RecyclerAdapter(Context context) {
        this.context = context;

    }
    public void addItem(GifItem item) {
        items.add(item);
        notifyDataSetChanged();
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cardview, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final String urladd = items.get(position).getDownloadUrl();

        holder.title.setText(items.get(position).getGifname());
        Glide.with(context)
                .load(Uri.parse(items.get(position).getDownloadUrl()))
                .into(holder.image);
        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context,BigImageActivity.class);
                intent.putExtra("url",urladd);
                context.startActivity(intent);
                //Intent intent = new Intent(context, BigImageActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //intent.putExtra("url",urladd);
                //context.startActivity(intent);




                //Toast.makeText(context, items.get(position).getGifname(), Toast.LENGTH_SHORT).show();
            }
        });

        holder.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DownGif downGif = new DownGif(context);

                String gif = items.get(position).getFilename();
                StorageReference storageRef =  downGif.downloadUrl(gif);
                File fileDir = downGif.makeDir(folderName);
               downGif.downloadLocal(storageRef,fileDir);

            }
        });

    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }


}