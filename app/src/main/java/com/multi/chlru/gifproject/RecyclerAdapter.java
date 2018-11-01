package com.multi.chlru.gifproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<ViewHolder> {
    Context context;
    int item_layout;
    final String folderName = "움짤공방";
    String search;
    private ArrayList<GifItem> items = new ArrayList<GifItem>();

    public RecyclerAdapter(Context context,String search) {
        this.context = context;
        this.search=search;
    }
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
        Typeface typeface = Typeface.createFromAsset(context.getAssets(),"BMHANNA_11yrs_ttf.ttf");
        holder.title.setTypeface(typeface);
        holder.saveBtn.setTypeface(typeface);
        if(search!=null){
            SpannableStringBuilder sb=new SpannableStringBuilder();
            String str=items.get(position).getGifname();
            sb.append(str);
            //sb.setSpan(new StyleSpan(Typeface.BOLD),str.indexOf(search),str.indexOf(search)+search.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            sb.setSpan(new ForegroundColorSpan(Color.parseColor("#03a9f4")),str.indexOf(search),str.indexOf(search)+search.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.title.setText(sb);
        }else {
            holder.title.setText(items.get(position).getGifname());
        }
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