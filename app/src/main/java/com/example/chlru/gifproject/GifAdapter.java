package com.example.chlru.gifproject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by hi on 2018-09-20.
 */

public class GifAdapter extends RecyclerView.Adapter<GifViewHolder>implements AdapterView.OnItemClickListener {

  private  ArrayList<GifItem> items = new ArrayList<GifItem>();
  private Context gContext;
  private LayoutInflater inflater;

    public GifAdapter(Context gContext) {
        this.gContext=gContext;


    }

    public void addItem(GifItem item) {
        items.add(item);
        notifyDataSetChanged();
    }

    AdapterView.OnItemClickListener itemClickListener;
    public void setOnItemClickListener(AdapterView.OnItemClickListener listener){
        itemClickListener=listener;
    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if(itemClickListener!=null){
            itemClickListener.onItemClick(adapterView,view,position,l);
        }
    }

    @NonNull
    @Override
    public GifViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        inflater =LayoutInflater.from(viewGroup.getContext());
        View view=inflater.inflate(R.layout.item_cardview,viewGroup,false);
        GifViewHolder holder=new GifViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull GifViewHolder holder, final int position) {
        //gifViewHolder.setData(items.get(position));


       holder.title.setText(items.get(position).getGifname());
        Glide.with(gContext)
             .load(Uri.parse(items.get(position).getDownloadUrl()))
             .into(holder.image);
        holder.saveBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View view){
                download(position);
            }


        });




    }

    @Override
    public int getItemCount() {
        if(items==null){
            return 0;
        }
        return items.size();
    }

    public GifItem getItem(int position){
        if(position<0||position>=items.size()){
            return null;
        }
        return items.get(position);
    }
    public void download(int position) {
        // Toast.makeText(context,  gifname + " 다운!!", Toast.LENGTH_LONG).show();


        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageRef1 = storage.getReferenceFromUrl("gs://gifproject-60db8.appspot.com");
        StorageReference storageRef = storageRef1.child(items.get(position).getFilename());


        //StorageReference ref1 = storage.getReference("sample01.gif");



        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        String folder_name = "/" + "움짤공방" + "/";
        final String string_path = root + folder_name;

        File file_path = null;
        try {
            file_path = new File(string_path);
            if (!file_path.isDirectory()) {
                file_path.mkdirs();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        try {

            final File localFile = File.createTempFile("gif", ".gif", file_path);


            Toast.makeText(gContext, " 임시파일 생성! ", Toast.LENGTH_LONG).show();

            storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                    String upgallery = string_path + localFile.getName();

                    gContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + upgallery))); //갤러리 갱신

                    Toast.makeText(gContext, "파일 저장 성공", Toast.LENGTH_SHORT).show();
                    localFile.deleteOnExit();



                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Toast.makeText(gContext, "저장 실패!!", Toast.LENGTH_LONG).show();
                    // Handle any errors
                }
            });
        } catch (Exception e) {
            Toast.makeText(gContext, "앱 권한 설정을 확인해 주세요!!", Toast.LENGTH_LONG).show();
        }

    }

}
