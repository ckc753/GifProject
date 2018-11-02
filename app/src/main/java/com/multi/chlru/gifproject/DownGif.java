package com.multi.chlru.gifproject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class DownGif {

    String string_path=null;
    Context context;
    SweetAlertDialog sweetalert;

    public DownGif(Context context) {
        this.context = context;
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
                //Toast.makeText(context, "다운로드 성공!!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Toast.makeText(context, "다운로드 실패", Toast.LENGTH_SHORT).show();
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
    //
    //로컬 저장소에 저장
    public void downloadLocal(StorageReference pathRef, File file_path){
        try{
            final ProgressDialog progressDialog = new ProgressDialog(context);
            //1.다운로드 프로그레스바 표시
            progressDialog.setTitle("파일저장중...");
            progressDialog.show();
            final File tempFile = File.createTempFile("images",".gif",file_path);
            pathRef.getFile(tempFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    String scanning_path = string_path+tempFile.getName();
                    context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + scanning_path))); //갤러리 갱신
                    //textView5.setText("tempFile 이름 = " + scanning_path);
                    //2.다운로드 프로그레스바 종료
                    progressDialog.cancel();
                    //Toast.makeText(context, "파일 저장 성공!!", Toast.LENGTH_SHORT).show();
                    sweetalert=new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE);
                    sweetalert.setTitleText(" 파일 저장 성공 ");
                    sweetalert.setConfirmText("확인");
                    sweetalert.show();
                    tempFile.deleteOnExit();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //Toast.makeText(context, "파일 저장 실패", Toast.LENGTH_SHORT).show();
                    sweetalert=new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE);
                    sweetalert.setTitleText(" 파일 저장 실패 ");
                    sweetalert.setConfirmText("확인");
                    sweetalert.show();
                }
            }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                //3.프로그레스바 리스너 (진행되는동안 표시되는 메시지 설정)
                @Override
                public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    progressDialog.setMessage("Downloaded " + ((int) progress) + "% ...");
                }
            });
        }catch (IOException e){
            //Toast.makeText(context, "해당앱의 저장권한을 확인하세요!!", Toast.LENGTH_SHORT).show();
            sweetalert=new SweetAlertDialog(context,SweetAlertDialog.WARNING_TYPE);
            sweetalert.setTitleText("＊＊＊ 경고 ＊＊＊");
            sweetalert.setContentText("해당앱의 저장권한을 확인하세요!!");
            sweetalert.setConfirmText("확인");
            sweetalert.show();
            e.printStackTrace();
        }
    }

}
