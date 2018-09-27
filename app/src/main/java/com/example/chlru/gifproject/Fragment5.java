package com.example.chlru.gifproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

public class Fragment5 extends Fragment {
    private Button button;
    private ImageView imageView5;
    private ViewGroup view5;
    private TextView textView5;
    String string_path=null;
    final String folderName = "움짤공방";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view5 = (ViewGroup) inflater.inflate(R.layout.fragment5, container, false);
        textView5 = view5.findViewById(R.id.textView5);
        button = view5.findViewById(R.id.fragBtn5);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String gif = "giphy.gif";
                StorageReference storageRef =  downloadUrl(gif);
                File fileDir = makeDir(folderName);
                downloadLocal(storageRef,fileDir);
            }
        });
        return view5;
    }

    public StorageReference downloadUrl(String gif){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference StorageRef = storage.getReferenceFromUrl("gs://gifproject-60db8.appspot.com");

        //다운로드할 파일을 가르키는 참조 만들기
        StorageReference pathRef = StorageRef.child(gif);
        //Url을 다운받기
        pathRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Toast.makeText(getContext(), "다운로드 성공" + uri, Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "다운로드 실패", Toast.LENGTH_SHORT).show();
            }
        });

        return pathRef;

    }

    //폴더 생성
    public File makeDir(String folder_name){
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()+"/";
        string_path = root + folder_name+"/";

        File file_path = null;
        try {
            file_path = new File(string_path);
            if (!file_path.isDirectory()) {
                file_path.mkdirs();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return file_path;
    }

    //로컬 저장소에 저장
    public void downloadLocal(StorageReference pathRef, File file_path){
        try{
            final File tempFile = File.createTempFile("images",".gif",file_path);
            pathRef.getFile(tempFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    String scanning_path = string_path+tempFile.getName();
                    getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + scanning_path))); //갤러리 갱신
                    textView5.setText("tempFile 이름 = " + scanning_path);
                    Toast.makeText(getContext(), "파일 저장 성공", Toast.LENGTH_SHORT).show();
                    tempFile.deleteOnExit();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "파일 저장 실패", Toast.LENGTH_SHORT).show();
                }
            });
        }catch (IOException e){
            Toast.makeText(getContext(), "예외발생!!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}





