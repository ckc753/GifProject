package com.example.chlru.gifproject;

import android.content.Context;
import android.net.Uri;
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
    //List<Item> items;
    int item_layout;
    final String folderName = "움짤공방";
    private ArrayList<GifItem> items = new ArrayList<GifItem>();
    private Context gContext;
    private LayoutInflater inflater;

    /*public RecyclerAdapter(Context context, List<Item> items, int item_layout) {
        this.context = context;
        this.items = items;
        this.item_layout = item_layout;
    }*/
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
    public void onBindViewHolder(ViewHolder holder, final int position) {
        /*final Item item = items.get(position);
        //Drawable drawable = ContextCompat.getDrawable(context, item.getImage());
        Glide.with(context)
                .load(items.get(position).getImage())
                .into(holder.image);*/
        //holder.image.setBackground(drawable);
        holder.title.setText(items.get(position).getGifname());
        Glide.with(context)
                .load(Uri.parse(items.get(position).getDownloadUrl()))
                .into(holder.image);
        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, items.get(position).getGifname(), Toast.LENGTH_SHORT).show();
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