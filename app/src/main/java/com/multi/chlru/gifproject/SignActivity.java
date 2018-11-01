package com.multi.chlru.gifproject;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.ProviderQueryResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SignActivity extends HannaFontActivity {
    private FirebaseAuth mAuth;
    private EditText editTextEmail;
    private EditText editTextPw;
    private EditText editTextPw2;
    String email;
    SweetAlertDialog sweetalert;
    TextView idsucess;
    TextView pwsucess;
    TextView pwvalid;
    SpannableStringBuilder sb;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        mAuth = FirebaseAuth.getInstance();
        idsucess=(TextView)findViewById(R.id.idsuccess);
        pwsucess=(TextView)findViewById(R.id.pwsuccess);
        pwvalid=(TextView)findViewById(R.id.pwvalid);
        editTextEmail = (EditText)findViewById(R.id.editTextEmail);
        editTextPw = (EditText)findViewById(R.id.editTextPw);
        editTextPw2 = (EditText)findViewById(R.id.editTextPw2);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "malgunbd.ttf");
        editTextPw.setTypeface(typeface);
        editTextPw2.setTypeface(typeface);
        sb=new SpannableStringBuilder();
        editTextEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus==false){
                    if (editTextEmail.getText().length()!=0){
                        String email=String.valueOf(editTextEmail.getText());


                        if(Patterns.EMAIL_ADDRESS.matcher(email).matches()){//이메일형식 유효성검사
                            mAuth.fetchProvidersForEmail(email).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                                @Override
                                public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                                    if (task.getResult().getProviders().size() > 0) {
                                        String str="중복된 아이디 입니다.";
                                        sb.append(str);
                                        sb.setSpan(new ForegroundColorSpan(Color.parseColor("#FF4081")),0,str.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                        idsucess.setText(sb);
                                        sb.clear();
                                    }else{
                                        String str="사용가능";
                                        idsucess.setText(str);
                                    }

                                }
                            });

                        }else{//이메일 형식이 아님
                            String str="이메일을 다시확인하세요";
                            sb.append(str);
                            sb.setSpan(new ForegroundColorSpan(Color.parseColor("#FF4081")),0,str.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            idsucess.setText(sb);
                            sb.clear();
                        }
                    }else{
                        idsucess.setText("");
                        //Toast.makeText(getApplicationContext(),"이메일검증 포커스뺌+값입력X",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    //Toast.makeText(getApplicationContext(),"이메일검증 포커스됨",Toast.LENGTH_SHORT).show();
                }
            }
        });
        editTextPw.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus==false){

                    String pw=String.valueOf(editTextPw.getText());
                    boolean pass=isValidPasswd(pw);
                    if(pass==true){
                        String str="비밀번호 확인 완료";
                        pwvalid.setText(str);
                    }else{
                        String str="6~12자의 영문 대 소문자, 숫자를 사용하세요.";
                        sb.append(str);
                        sb.setSpan(new ForegroundColorSpan(Color.parseColor("#FF4081")),0,str.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        pwvalid.setText(sb);
                        sb.clear();
                        editTextPw.setText("");
                    }
                }
            }
        });
        editTextPw2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus==false){

                    String pw1=String.valueOf(editTextPw.getText());
                    String pw2=String.valueOf(editTextPw2.getText());
                    if(pw1.equals(pw2)){
                        String str="비밀번호 확인 완료";
                        pwsucess.setText(str);
                    }else{
                        String str="비밀번호를 다시 확인하세요";
                        sb.append(str);
                        sb.setSpan(new ForegroundColorSpan(Color.parseColor("#FF4081")),0,str.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        pwsucess.setText(sb);
                        sb.clear();
                        editTextPw2.setText("");
                    }
                }
            }
        });
        editTextPw2.setOnEditorActionListener(new TextView.OnEditorActionListener() {//완료버튼 눌럿을경우

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String pw1=String.valueOf(editTextPw.getText());
                    String pw2=String.valueOf(editTextPw2.getText());
                    if(pw1.equals(pw2)){
                        String str="비밀번호 확인 완료";
                        pwsucess.setText(str);
                    }else{
                        String str="비밀번호를 다시 확인하세요";
                        sb.append(str);
                        sb.setSpan(new ForegroundColorSpan(Color.parseColor("#FF4081")),0,str.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        pwsucess.setText(sb);
                        sb.clear();
                        editTextPw2.setText("");
                    }
                }
                return false;
            }
        });
        //1. 회원가입 버튼 이벤트
        Button BasicSignButton = (Button) findViewById(R.id.BasicSignButton);
        BasicSignButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1.1 빈값이 넘어올때의 회원가입실패 & 값이 틀릴경우 회원가입실패 & 나머지는 회원가입성공
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

        //1.2 뒤로가기 버튼을 이용하지않고 => 툴바를 이용한 뒤로가기.
        Toolbar toolbar = (Toolbar) findViewById(R.id.signtoolbar);
        setSupportActionBar(toolbar); //activity_main.xml의 액션바부분에 toolbar를 적용시킨다.
        getSupportActionBar().setDisplayShowTitleEnabled(false); //기본 타이틀 보여줄지 말지 설정
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 툴바를 이용한 뒤로가기 버튼, 디폴트로 true만 해도 백버튼이 생김 https://dreamaz.tistory.com/109
        //getSupportActionBar().setHomeButtonEnabled(true);
    }//onCreate_end

    //1.3 회원가입 메소드
    public void createUser(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //Toast.makeText(getApplicationContext(),"회원가입 완료",Toast.LENGTH_LONG).show();
                            sweetalert=new SweetAlertDialog(SignActivity.this,SweetAlertDialog.SUCCESS_TYPE);
                            sweetalert.setTitleText("- 회원가입 완료 -");
                            sweetalert.setConfirmText("확인");
                            sweetalert.show();
                            sweetalert.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
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


    //1.4 액션바 뒤로가기
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }else if(id == android.R.id.home){ //1.4.1 home은 툴바(액션바)를 클릭했을경우이고, 나머지는 메뉴툴바이다.
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }//액션바 뒤로가기도 생성

    //1.5 취소버튼클릭시 LoginActivity로 화면전환.
    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        finish();
    }
    //1.6 비밀번호 유효성검사
    private boolean isValidPasswd(String target) {
        //Pattern p = Pattern.compile("(^.*(?=.{6,12})(?=.*[0-9])(?=.*[a-zA-Z]).*$)");
        Pattern p = Pattern.compile("[A-Za-z0-9]$");//영어 숫자 제한
        Matcher m = p.matcher(target);
        //if (m.find() && !target.matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣]+.*")&&!target.matches(".*[:;~!@#$%\\^&\\*\\(\\)/\\|\\+\\=-_\\'\"\\[\\]{}\\?<>,.]+.*")){
        if(m.find()&&target.matches("(^.*(?=.{6,12})(?=.*[0-9])(?=.*[a-zA-Z]).*$)")){//6~12자리 영어와 숫자 최소1글자는 포함
            return true;
        }else{
            return false;
        }
    }
}
