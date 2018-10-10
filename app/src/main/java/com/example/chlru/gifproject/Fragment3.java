package com.example.chlru.gifproject;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class Fragment3 extends Fragment {
    int reCode;
    Uri filePath;
    ImageView img;
    Button upbtu;
    Button searchbtu;
    FirebaseStorage storage;
    EditText editText;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    Query myquery;
    int count;
    Spinner spinner;
    String category;
    private Button [] cButton = new Button[9];

    ArrayList<String> arr;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup view3 = (ViewGroup) inflater.inflate(R.layout.fragment3, container, false);
        ViewGroup view2 = (ViewGroup) inflater.inflate(R.layout.fragment2, container, false);

        arr=new ArrayList<String>();
        cButton[0] = (Button) view2.findViewById(R.id.CBtn1);
        cButton[1] = (Button) view2.findViewById(R.id.CBtn2);
        cButton[2] = (Button) view2.findViewById(R.id.CBtn3);
        cButton[3] = (Button) view2.findViewById(R.id.CBtn4);
        cButton[4] = (Button) view2.findViewById(R.id.CBtn5);
        cButton[5] = (Button) view2.findViewById(R.id.CBtn6);
        cButton[6] = (Button) view2.findViewById(R.id.CBtn7);
        cButton[7] = (Button) view2.findViewById(R.id.CBtn8);
        cButton[8] = (Button) view2.findViewById(R.id.CBtn9);

        for(int i =0; i<cButton.length;i++){
            arr.add(cButton[i].getText().toString());
        }

       // Toast.makeText(getContext(), "확인!!!  "+cate, Toast.LENGTH_SHORT).show();
        storage = FirebaseStorage.getInstance();
        //StorageReference storageRef =storage.getReferenceFromUrl("gs://gifproject-60db8.appspot.com");
        //Toast.makeText(getContext(), "확인!"+String.valueOf(storageRef), Toast.LENGTH_SHORT).show();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        searchbtu = (Button) view3.findViewById(R.id.searchbutton);
        upbtu = (Button) view3.findViewById(R.id.upbutton);
        img = (ImageView) view3.findViewById(R.id.preimg);
        editText = (EditText) view3.findViewById(R.id.editText);


        spinner=(Spinner)view3.findViewById(R.id.spin);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, arr);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
               category =arr.get(position);
               // Toast.makeText(getContext(), "확인합시다! "+category, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //업로드할 이미지 선택
        searchbtu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reCode = 101;
                Intent intent = new Intent();
                intent.setType("image/gif");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "이미지 선택!"), reCode);
                //onBackPressed();

            }
        });

        upbtu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //showMessage();
                uploadFile();
               // mInputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);

            }
        });


        return view3;
    }

    //업로드!!!
    private void uploadFile() {
        //Toast.makeText(getContext(),editText.getText().toString(),Toast.LENGTH_SHORT).show();

        if ((filePath != null)&&(editText.getText().toString().length()!=0)) {
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("업로드중...");
            progressDialog.show();

            SimpleDateFormat Dateformat = new SimpleDateFormat("yyyyMMddHHmmss");
            Date now = new Date();
            final String file = Dateformat.format(now);
            final String filename = file + ".gif";
            final StorageReference storageRef = storage.getReferenceFromUrl("gs://gifproject-60db8.appspot.com").child(filename);
            myquery = databaseReference.child("gif").orderByChild("number").limitToFirst(1);
            myquery.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    // count=(int) dataSnapshot.getChildrenCount();
                    GifItem gitem = dataSnapshot.getValue(GifItem.class);
                    count=gitem.getNumber();
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            //Toast.makeText(getApplicationContext(), "DB업로드!", Toast.LENGTH_SHORT).show();
            storageRef.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    progressDialog.cancel();
                    Toast.makeText(getContext(), "업로드 완료!", Toast.LENGTH_SHORT).show();
                    String down = String.valueOf(taskSnapshot.getDownloadUrl());
                    //Toast.makeText(getApplicationContext(), down, Toast.LENGTH_SHORT).show();

                    GifItem gitem = new GifItem(down, filename, editText.getText().toString(), file,count-1,category);
                    //gifItem gitem = new gifItem(filename, editText.getText().toString(), file);
                    databaseReference.child("gif").push().setValue(gitem);
                    editText.setText("");



                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    @SuppressWarnings("visibleForTests")
                    double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    progressDialog.setMessage("Uploaded " + ((int) progress) + "% ...");

                }
            });
            Log.d("태그", " 파일명 : " + filename);
        } else if((filePath != null)&&(editText.getText().toString().length()==0)){
            Toast.makeText(getContext(), "움짤제목을 정해주세요!!", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getContext(), "파일을 선택하세요!!", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       // mInputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        if (requestCode == 101) {
            try {
            filePath = data.getData();
            Log.d("TAG!!", "uri : " + String.valueOf(filePath) + " 파일명 입니다");
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), filePath);
                img.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }catch (NullPointerException e1){
                e1.printStackTrace();
                Log.e("exception : "," nullpoint 널널널널");
            }
        }
    }
}
