package com.multi.chlru.gifproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SignActivity extends HannaFontActivity {
    private FirebaseAuth mAuth;
    private EditText editTextEmail;
    private EditText editTextPw;
    String email;
    SweetAlertDialog sweetalert;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        mAuth = FirebaseAuth.getInstance();

        editTextEmail = (EditText)findViewById(R.id.editTextEmail);
        editTextPw = (EditText)findViewById(R.id.editTextPw);
        Button BasicSignButton = (Button) findViewById(R.id.BasicSignButton);
        BasicSignButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1.1 입력
                if(editTextEmail.getText().toString().getBytes().length <= 0 || editTextPw.getText().toString().getBytes().length <= 0){//빈값이 넘어올때의 처리
                    //Toast.makeText(getApplicationContext(), "값을 입력하세요.", Toast.LENGTH_SHORT).show();
                    sweetalert=new SweetAlertDialog(SignActivity.this,SweetAlertDialog.WARNING_TYPE);
                    sweetalert.setTitleText("- 회원가입 실패 -");
                    sweetalert.setContentText("값을 입력하세요.");
                    sweetalert.setConfirmText("확인");
                    sweetalert.show();
                }
                else{
                    String temp1 = "이메일 입력 내용 : " + editTextEmail.getText().toString();
                    String temp2 = "비번 입력 내용 : " + editTextPw.getText().toString();
                    //Toast.makeText(getApplicationContext(), temp1+" "+temp2, Toast.LENGTH_SHORT).show();
                    createUser(editTextEmail.getText().toString(), editTextPw.getText().toString());
                }
            }
        });

        //1.1 뒤로가기 버튼(로그인화면으로 이동)
        /*Button backButton = (Button)findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
            }
        });*/

        //1.2 뒤로가기 버튼을 이용하지않고==>  툴바를 이용한 뒤로가기
        //★ 주의사항으로는 자바파일을 Activity상속이 아니라 AppCompatActivity로 받아야 액션바로 호환할 수 있다. ★
        Toolbar toolbar = (Toolbar) findViewById(R.id.signtoolbar);
        setSupportActionBar(toolbar); //activity_main.xml의 액션바부분에 toolbar를 적용시킨다.
        getSupportActionBar().setDisplayShowTitleEnabled(false); //기본 타이틀 보여줄지 말지 설정
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 툴바를 이용한 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김 https://dreamaz.tistory.com/109
        //getSupportActionBar().setHomeButtonEnabled(true);
    }//onCreate_end

    public void createUser(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //Toast.makeText(getApplicationContext(),"회원가입 완료",Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }else {
                            //Toast.makeText(getApplicationContext(),"회원가입 실패",Toast.LENGTH_LONG).show();
                            sweetalert=new SweetAlertDialog(SignActivity.this,SweetAlertDialog.WARNING_TYPE);
                            sweetalert.setTitleText("- 회원가입 실패 -");
                            sweetalert.setContentText("다시 회원가입해주시기 바랍니다.");
                            sweetalert.setConfirmText("확인");
                            sweetalert.show();
                        }
                    }
                });
    }//createUser_end

    //1.3 액션바 뒤로가기
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }else if(id == android.R.id.home){
            //1. home은 툴바(액션바)를 클릭했을경우이고, 나머지는 메뉴툴바이다.
            // 여기서 액션바는 뒤로가기액션바 : 따라서 뒤로가기 커스터마이징
            //https://m.blog.naver.com/PostView.nhn?blogId=alens82&logNo=220754226712&proxyReferer=https%3A%2F%2Fwww.google.co.kr%2F
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            finish();

            //2. 원래 코드
            //finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }//액션바 뒤로가기도 생성

    //취소버튼클릭시, Sign->LoginActivity로 이동하도록 onBackPressed메소드사용
    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        finish();
    }
}
