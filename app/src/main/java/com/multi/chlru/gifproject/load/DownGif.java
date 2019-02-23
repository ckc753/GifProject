package com.multi.chlru.gifproject.load;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.kakao.util.helper.log.Logger;

import java.io.File;
import java.io.IOException;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class DownGif {
    String string_path=null;
    Activity context;
    SweetAlertDialog sweetalert;
    Uri providerUri;
    File tempFile;

    public DownGif(Activity context) {
        this.context = context;
    }

    public StorageReference downloadUrl(String gif){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference StorageRef = storage.getReferenceFromUrl("gs://gifproject-60db8.appspot.com");

        //다운로드할 파일을 가르키는 참조 만들기
        StorageReference pathRef = StorageRef.child("GIF").child(gif);
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
    //카카오톡 다운로드버튼클릭시 RecyclerAdapter에서 DownGif으로 와서 기능실행됨.
    public File downloadLocal2(StorageReference pathRef, File file_path){ //file_path는 파일이 저장될 경로, pathRef는 스토리지된 저장될 경로
        try{
            final ProgressDialog progressDialog = new ProgressDialog(context);
            //1.다운로드 프로그레스바 표시
            progressDialog.setTitle("전송준비중...");
            progressDialog.show();

            tempFile = File.createTempFile("images",".gif",file_path); //해당 파일의 형식으로 생성됨.
            pathRef.getFile(tempFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    String scanning_path = string_path+tempFile.getName();
                    context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + scanning_path))); //갤러리 갱신

                    progressDialog.cancel();
                    /*sweetalert=new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE);
                    sweetalert.setTitleText(" 파일 전송 성공 ");
                    sweetalert.setConfirmText("확인");
                    sweetalert.show();*/
                    if (Build.VERSION.SDK_INT < 24) {
                        providerUri = Uri.fromFile(tempFile);
                    } else {
                        providerUri = FileProvider.getUriForFile(context, "{package_name}.fileprovider", tempFile);
                    }
                        try {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("image/*");

                        intent.putExtra(Intent.EXTRA_STREAM, providerUri);
                        //intent.putExtra(Intent.EXTRA_TEXT,gifUrl );
                        //Logger.e("imgurl = " + gifUrl);
                        //Logger.e("Uri.parse(imgUrl) = " + Uri.parse(gifUrl));
                        //intent.setPackage("com.kakao.talk");
                        Intent chooser = Intent.createChooser(intent, "공유하기");
                        context.startActivity(chooser);
                       // Logger.e("Temp파일 지우기전 " + tempFile.toString());

                        //context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(tempFile)));
                        //Intent mediaIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(tempFile));
                        //context.sendBroadcast(mediaIntent);
                        //new MMediaScannerConnection(context, tempFile).onScanCompleted(string_path, providerUri);
                        /* //미디어스캐닝 완료감지
                        if(intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED)){
                            tempFile.delete();
                        }*/
                        //tempFile.delete(); // x

                       // Logger.e("Temp파일 지운후 " + tempFile.toString());
                    } catch (Exception e) {
                        Log.e("로그exception  = ", e.toString());
                    }
                    //tempFile.deleteOnExit();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    /*sweetalert=new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE);
                    sweetalert.setTitleText(" 파일 전송 실패 ");
                    sweetalert.setConfirmText("확인");
                    sweetalert.show();*/
                }
            }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
                //3.프로그레스바 리스너 (진행되는동안 표시되는 메시지 설정)
                @Override
                public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                   /* double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    progressDialog.setMessage("Downloaded " + ((int) progress) + "% ...");*/
                }
            });
            //tempFile.delete(); //x
            return tempFile;
        }catch (IOException e){
            //Toast.makeText(context, "해당앱의 저장권한을 확인하세요!!", Toast.LENGTH_SHORT).show();
            sweetalert=new SweetAlertDialog(context,SweetAlertDialog.WARNING_TYPE);
            sweetalert.setTitleText("＊＊＊ 경고 ＊＊＊");
            sweetalert.setContentText("해당앱의 저장권한을 확인하세요!!");
            sweetalert.setConfirmText("확인");
            sweetalert.show();
            e.printStackTrace();
            return null;
        }
    }//File_download2


    public class MMediaScannerConnection implements MediaScannerConnection.MediaScannerConnectionClient{
        private MediaScannerConnection mediaScannerConnection;
        private Context context;
        private File file;

        public MMediaScannerConnection(Context context, File file) {
            this.context = context;
            this.file = file;
            mediaScannerConnection = new MediaScannerConnection(context, this);
            mediaScannerConnection.connect(); //연결.
        }

        @Override
        public void onMediaScannerConnected() { // MediaScanner과 연결이 됬을때 client에게 알리기 위해 호출된다.
            mediaScannerConnection.scanFile(file.getAbsolutePath(), "mineType"); //해당 file 경로 미캐닝, mineType형태로 찾는다.
            // mineType : MIME 타입이 NULL의 경우, MIME 형식은 파일 확장자로부터 추정함. (참고)
        }
        @Override
        public void onScanCompleted(String path, Uri uri) { // MediaScanner이 파일 스케닝을 끝냈을때 client에게 알리기 위해 호출된다.
            //mediaScannerConnection.disconnect(); //연결 끊기.
            //tempFile.delete();
        }
    }//MMediaSanningConnection_Class
}//DownGif_Class
