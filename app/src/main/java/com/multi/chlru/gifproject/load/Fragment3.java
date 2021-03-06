package com.multi.chlru.gifproject.load;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import com.multi.chlru.gifproject.GifItem;
import com.multi.chlru.gifproject.HannaFontFragment;
import com.multi.chlru.gifproject.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class Fragment3 extends HannaFontFragment {
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
    private Button[] cButton = new Button[12];
    InputMethodManager mInputMethodManager;
    String member; //내가올린자료를 위한 pk값을 저장할 변수
    ArrayList<String> arr;
    String gifdown;
    String jpgdown;
    ProgressDialog progressDialog;
    String file;
    String filename;
    SweetAlertDialog sweetalert;

    public Fragment3() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup view3 = (ViewGroup) inflater.inflate(R.layout.fragment3, container, false);
        ViewGroup view2 = (ViewGroup) inflater.inflate(R.layout.fragment2, container, false);
        //1. 사용자의 고유 pk값저장(카카오, 구글, firebase유저의 pk값)
        member = getArguments().getString("pkid");
        arr = new ArrayList<String>();
        cButton[0] = (Button) view2.findViewById(R.id.CBtn1);
        cButton[1] = (Button) view2.findViewById(R.id.CBtn2);
        cButton[2] = (Button) view2.findViewById(R.id.CBtn3);
        cButton[3] = (Button) view2.findViewById(R.id.CBtn4);
        cButton[4] = (Button) view2.findViewById(R.id.CBtn5);
        cButton[5] = (Button) view2.findViewById(R.id.CBtn6);
        cButton[6] = (Button) view2.findViewById(R.id.CBtn7);
        cButton[7] = (Button) view2.findViewById(R.id.CBtn8);
        cButton[8] = (Button) view2.findViewById(R.id.CBtn9);
        cButton[9] = (Button) view2.findViewById(R.id.CBtn10);
        cButton[10] = (Button) view2.findViewById(R.id.CBtn11);
        cButton[11] = (Button) view2.findViewById(R.id.CBtn12);
        //Toast.makeText(getContext(), "확인합시다! "+member, Toast.LENGTH_SHORT).show();
        for (int i = 0; i < 12; i++) {
            arr.add(cButton[i].getText().toString());
        }

        storage = FirebaseStorage.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        searchbtu = (Button) view3.findViewById(R.id.searchbutton);
        upbtu = (Button) view3.findViewById(R.id.upbutton);
        img = (ImageView) view3.findViewById(R.id.preimg);
        editText = (EditText) view3.findViewById(R.id.editText);

        //2. 스피너클릭시 arr배열의 위치를 가져오도록한다.
        spinner = (Spinner) view3.findViewById(R.id.spin);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, arr);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                category = arr.get(position);
                // Toast.makeText(getContext(), "확인합시다! "+category, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //3. 검색버튼을 통해 업로드할 이미지 선택
        searchbtu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getContext(),android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(getContext(),android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    //if (ActivityCompat.shouldShowRequestPermissionRationale(mactivity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                    sweetalert = new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE);
                    sweetalert.setTitleText("＊＊＊ 경고 ＊＊＊");
                    sweetalert.setContentText("저장소 권한이 거부되었습니다. 업로드기능을 사용하시려면 해당 권한을 직접 허용하셔야 합니다.");
                    sweetalert.setCancelText("취소");
                    sweetalert.setConfirmText("설정");
                    sweetalert.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                            sweetAlertDialog.cancel();
                        }
                    });
                    sweetalert.show();
                }else {
                    reCode = 101;
                    Intent intent = new Intent();
                    intent.setType("image/gif");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "이미지 선택!"), reCode);
                }
            }
        });

        //4. 업로드버튼을 통해 업로드실행.
        upbtu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadFile();
            }
        });
        return view3;
    }//onCreateView_end

    //5. 업로드 기능
    private void uploadFile() {
        if ((filePath != null) && (editText.getText().toString().length() != 0)) {
            //5.1 ProgressDialog 생성
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getActivity(),"취소완료!",Toast.LENGTH_SHORT).show();
                }
            });
            progressDialog.setTitle("업로드중...");
            progressDialog.show();

            //5.2 Storage경로에 (SimpleDateFormat Type).gif으로 파일명 설정하고 gifManager에 위에서 1개 끊어서 조회.

            final StorageReference storageRef = storage.getReferenceFromUrl("gs://gifproject-60db8.appspot.com").child("GIF").child(filename);
            myquery = databaseReference.child("gifManager").orderByChild("number").limitToFirst(1);
            myquery.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    // count=(int) dataSnapshot.getChildrenCount();
                    GifItem gitem = dataSnapshot.getValue(GifItem.class);
                    count = gitem.getNumber();
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
            //6.1 업로드 완료시 ProgressDialog종료

            storageRef.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                       /* progressDialog.cancel();
                        Toast.makeText(getContext(), "업로드 완료!", Toast.LENGTH_SHORT).show();*/
                    gifdown = String.valueOf(taskSnapshot.getDownloadUrl());
                    uploadjpgFile();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    @SuppressWarnings("visibleForTests")
                    //6.4 ProgressDialog 이벤트
                            double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    progressDialog.setMessage("Uploaded " + ((int) progress) + "% ...");

                }
            });

            Log.d("태그", " 파일명 : " + filename);

        } else if ((filePath != null) && (editText.getText().toString().length() == 0)) {
            Toast.makeText(getContext(), "움짤제목을 정해주세요!!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "파일을 선택하세요!!", Toast.LENGTH_SHORT).show();
        }
    }

    /////////////////////////////////////////////////////이미지 업로드
    private void uploadjpgFile() {
        //5.1 ProgressDialog 생성
            /*final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("업로드중...");
            progressDialog.show();*/
        try {
            //5.2 Storage경로에 (SimpleDateFormat Type).gif으로 파일명 설정하고 gifManager에 위에서 1개 끊어서 조회.

            final StorageReference storageRef = storage.getReferenceFromUrl("gs://gifproject-60db8.appspot.com").child("JPG").child(filename);
            Bitmap bitmapjpg = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), filePath);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmapjpg.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] data = baos.toByteArray();


            //6.1 jpg업로드 완료시 ProgressDialog종료

            storageRef.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    progressDialog.cancel();
                    Toast.makeText(getContext(), "업로드 완료!", Toast.LENGTH_SHORT).show();
                    jpgdown = String.valueOf(taskSnapshot.getDownloadUrl());
                    GifItem gitem = new GifItem(jpgdown, gifdown, filename, editText.getText().toString(), file, count - 1, category, member);

                    //6.2 gifManager DB에 Push
                    databaseReference.child("gifManager").push().setValue(gitem); //DB값 넣기

                    //6.3 업로드창 초기화
                    editText.setText("");
                    img.setImageDrawable(getResources().getDrawable(R.drawable.zzari3));
                    filePath = null;
                    mInputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);//키보드 내리기
                    mInputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });/*.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        @SuppressWarnings("visibleForTests")
                        //6.4 ProgressDialog 이벤트
                                double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        progressDialog.setMessage("Uploaded " + ((int) progress) + "% ...");

                    }
                });*/

            Log.d("태그", " 파일명 : " + file);
        } catch (IOException e) {
        }

    }

    //////////////////////
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            try {
                //7. 갤러리에서 사진을 선택했을때, 선택된 이미지를 보여주는 메소드 (※하나의 동작이 실행되었을때의 결과를 보여주는 메소드※)
                filePath = data.getData();
                Log.d("TAG!!", "uri : " + String.valueOf(filePath) + " 파일명 입니다");
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), filePath);
                img.setImageBitmap(bitmap);
                SimpleDateFormat Dateformat = new SimpleDateFormat("yyyyMMddHHmmss");
                Date now = new Date();
                file = Dateformat.format(now);
                filename = file + "gifmarket";
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException e1) {
                e1.printStackTrace();
                Log.e("exception : ", " nullpoint 널널널널");
            }
        }
    }

    @Override
    public void onDetach() {
        filePath = null;
        editText.setText("");
        super.onDetach();
    }
}
