package com.multi.chlru.gifproject.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.kakao.auth.ErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;
import com.multi.chlru.gifproject.HannaFontActivity;
import com.multi.chlru.gifproject.R;
import com.multi.chlru.gifproject.homeKeyEvnet;
import com.multi.chlru.gifproject.main.MainActivity;
import com.multi.chlru.gifproject.manager.ManagerActivity;

import java.util.Timer;
import java.util.TimerTask;

import cn.pedant.SweetAlert.SweetAlertDialog;

//TextInputLayout생략
public class LoginActivity extends HannaFontActivity implements GoogleApiClient.OnConnectionFailedListener{
    private static final int RC_SIGN_IN = 10;
    SignInButton googleLoginButton;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private EditText editTextEmail;
    private EditText editTextPw;
    String email;

    SharedPreferences setting;
    SharedPreferences.Editor editor;
    CheckBox chk_auto;
    Intent intent;
    String ID;
    String PW;

    Button BasicLoginButton;
    SessionCallback callback;

    SharedPreferences sessionsp;
    SharedPreferences.Editor sessionedit;
    private  long pressedTime = 0;
    SweetAlertDialog sweetalert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        chk_auto = (CheckBox) findViewById(R.id.autoLoginCheck);
        BasicLoginButton  = (Button) findViewById(R.id.BasicLoginButton);
        editTextEmail = (EditText)findViewById(R.id.editTextEmail);
        editTextPw = (EditText)findViewById(R.id.editTextPw);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "malgunbd.ttf");
        editTextPw.setTypeface(typeface);


        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        googleLoginButton = (SignInButton)findViewById(R.id.googleLoginButton);
        googleLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });


        //1.SharePreperence Setting
        setting = getSharedPreferences("setting", 0);
        editor = setting.edit();
        if (setting.getBoolean("chk_auto", false)) {
            editTextEmail.setText(setting.getString("ID", ""));
            editTextPw.setText(setting.getString("PW", ""));
            chk_auto.setChecked(true);
        }

        //2. 기본 로그인버튼 이벤트
        BasicLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chk_auto.isChecked()) {
                    String ID = editTextEmail.getText().toString();
                    String PW = editTextPw.getText().toString();
                    editor.putString("ID", ID);
                    editor.putString("PW", PW);
                    editor.putBoolean("chk_auto", true);
                    editor.commit();
                    //2-1. 버튼이벤트 중복없이 한번만 실행되도록 적용
                    BasicLoginButton.setClickable(false);
                } else {
                    editor.clear();
                    editor.commit();
                }
                //2-2. 빈값이 넘어올때의 로그인실패 & 값이 틀릴경우 로그인실패 & 나머지는 로그인성공
                if(editTextEmail.getText().toString().getBytes().length <= 0 || editTextPw.getText().toString().getBytes().length <= 0){//빈값이 넘어올때의 처리
                    //Toast.makeText(getApplicationContext(), "값을 입력하세요.", Toast.LENGTH_SHORT).show();
                    sweetalert=new SweetAlertDialog(LoginActivity.this,SweetAlertDialog.WARNING_TYPE);
                    sweetalert.setTitleText("- 로그인 실패 -");
                    sweetalert.setContentText("값을 입력하세요.");
                    sweetalert.setConfirmText("확인");
                    sweetalert.show();
                }else{
                    //String temp1 = "이메일 입력 내용 : " + editTextEmail.getText().toString();
                    //String temp2 = "비번 입력 내용 : " + editTextPw.getText().toString();
                    //Toast.makeText(getApplicationContext(), temp1+" "+temp2, Toast.LENGTH_SHORT).show();
                    signUser(editTextEmail.getText().toString(), editTextPw.getText().toString());
                }
            }
        });
        //3. 회원가입 버튼이벤트
        TextView SignPage = (TextView)findViewById(R.id.SignPage);
        SignPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homekey.setHomeflag(true);
                Intent intent = new Intent(getApplicationContext(), SignActivity.class);
                startActivity(intent);
                finish();
            }
        });
        //4. 로그인버튼없이 "완료"키보드를 통해 로그인되는 방식
        editTextPw.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editTextPw.setOnEditorActionListener(new TextView.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //4-1. SharePreperence를 통해 로그인된 값 저장
                if (chk_auto.isChecked()) {
                    String ID = editTextEmail.getText().toString();
                    String PW = editTextPw.getText().toString();
                    editor.putString("ID", ID);
                    editor.putString("PW", PW);
                    editor.putBoolean("chk_auto", true);
                    editor.commit();
                } else {
                    editor.clear();
                    editor.commit();
                }
                //4-2. 빈값이 넘어올때의 로그인실패 & 값이 틀릴경우 로그인실패 & 나머지는 로그인성공
                if(editTextEmail.getText().toString().getBytes().length <= 0 || editTextPw.getText().toString().getBytes().length <= 0){//빈값이 넘어올때의 처리
                    //Toast.makeText(getApplicationContext(), "값을 입력하세요.", Toast.LENGTH_SHORT).show();
                    sweetalert=new SweetAlertDialog(LoginActivity.this,SweetAlertDialog.WARNING_TYPE);
                    sweetalert.setTitleText("- 로그인 실패 -");
                    sweetalert.setContentText("값을 입력하세요.");
                    sweetalert.setConfirmText("확인");
                    sweetalert.show();
                }
                else if (actionId == EditorInfo.IME_ACTION_DONE) {
                    signUser(editTextEmail.getText().toString(), editTextPw.getText().toString());
                    return true;
                }
                return false;
            }//onEditorAction_end
        });
        //5. 카카오로그인에 사용되는 SessionCallback클래스 정의.
        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);
    }//onCreate_end

    //6. signUser (기본로그인 메소드)
    public void signUser(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(editTextEmail.getText().toString().equals("manager") && editTextPw.getText().toString().equals("manager")){
                            homekey.setHomeflag(true);
                            Intent intent = new Intent(getApplicationContext(), ManagerActivity.class);
                            startActivity(intent);
                            finish();
                        }else { //6-1. 로그인완료시
                            if (!task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "로그인실패", Toast.LENGTH_LONG).show();
                            } else {

                                FirebaseUser user = mAuth.getCurrentUser();
                                sessionsp = getSharedPreferences("session", 0);
                                sessionedit = sessionsp.edit();
                                sessionedit.putString("sessionid", editTextEmail.getText().toString()); //아이디값 세션처리
                                sessionedit.putString("sessonpk",user.getUid()); //pk값 세션처리
                                sessionedit.commit();

                                //Toast.makeText(getApplicationContext(),"로그인완료",Toast.LENGTH_LONG).show();
                                homekey.setHomeflag(true);
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }
                });
    }

    //7. 맨 처음 구글 & 카카오로그인에 사용되는 메소드
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }
    }//onActivityResult_end

    //8. 구글로그인연동에 사용되는 두번째
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            sessionsp = getSharedPreferences("session", 0);
                            sessionedit = sessionsp.edit();
                            sessionedit.putString("sessionid", mAuth.getCurrentUser().getEmail()); //id값 세션처리
                            sessionedit.putString("sessonpk",mAuth.getCurrentUser().getUid()); //pk값 세션처리
                            sessionedit.commit();

                            //구글로그인시 입력값이 들어간다면 Session할 수 있도록
                            //Toast.makeText(getApplicationContext(),"아이디생성완료",Toast.LENGTH_LONG).show();
                            homekey.setHomeflag(true);
                            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {

                        }
                    }
                });
    }//firebaseAuthWithGoogle_end


    //9. firebase연결실패시 메소드
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }//onConnectionFailed_end


    //10. firebase계정 로그아웃 메소드
    @Override
    protected void onStop() {
        super.onStop();
        FirebaseAuth.getInstance().signOut();
    }//onStop_end

    //11. SessionCallback (카카오로그인연동에 사용되는 클래스)
    private class SessionCallback implements ISessionCallback {
        @Override
        public void onSessionOpened() {
            UserManagement.requestMe(new MeResponseCallback() {
                @Override
                public void onFailure(ErrorResult errorResult) {
                    String message = "failed to get user info. msg=" + errorResult;
                    Logger.d(message);

                    ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                    if (result == ErrorCode.CLIENT_ERROR_CODE) {
                        homekey.setHomeflag(true);
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        //redirectMainActivity();
                    }
                }

                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                }

                @Override
                public void onNotSignedUp() {
                }

                //11-1. 카카오로그인연동 성공시 값 전달& 엑티비티 전환.
                @Override
                public void onSuccess(UserProfile userProfile) {
                    Log.e("★UserProfile★", userProfile.toString()); //Logcat ==> Debug로 확인가능하다.
                    String name = userProfile.getNickname();
                    sessionsp = getSharedPreferences("session", 0);
                    sessionedit = sessionsp.edit();
                    sessionedit.putString("sessionid", userProfile.getNickname()); //아이디값 세션처리
                    sessionedit.putString("sessonpk",userProfile.getUUID()); //pk값 세션처리
                    sessionedit.commit();
                    homekey.setHomeflag(true);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);

                    //intent.putExtra("name", name);
                    //startActivityForResult(intent, 1);
                    finish();
                    finishAffinity();
                }
            });
        }

        //11-4. Session실패시
        @Override
        public void onSessionOpenFailed(KakaoException exception) {
        }
    }//SessionCallBack_end


    //12. 뒤로가기버튼 클릭시 Main으로 되돌아가는 Override메소드
    @Override
    public void onBackPressed() {
        homekey.setHomeflag(true);
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();



    }
    homeKeyEvnet homekey = new homeKeyEvnet();
    /*public static boolean homeflag=false;
    public static boolean homestatus=false;*/
    Timer timer;
    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        //homestatus=true;
        homekey.setHomestatus(true);
        Log.d("홈", "로그인 홈버튼 누른 상태 "+homekey.isHomestatus()+" "+homekey.isHomeflag());
        //Log.d("홈", "홈버튼 누른 상태 "+homestatus+" "+homeflag);
    }
    @Override
    protected void onPause() {

        Log.d("홈", "로그인 pause상태 "+homekey.isHomestatus()+" "+homekey.isHomeflag());
        //Log.d("홈", "pause상태 "+homestatus+" "+homeflag);
        if(homekey.isHomestatus()==true&&homekey.isHomeflag()==false){
            Log.d("홈", "로그인 timer 실행상태 "+homekey.isHomestatus()+" "+homekey.isHomeflag());
            timer.schedule( new TimerTask()
                            {
                                public void run()
                                {
                                    finish();
                                    android.os.Process.killProcess(android.os.Process.myPid());
                                }
                            }
                    , 5000);
        }else{
        }

        super.onPause();
    }
    @Override
    public void onResume() {

        homekey.setHomestatus(false);
        homekey.setHomeflag(false);
        Log.d("홈", "로그인 resume상태 "+homekey.isHomestatus()+" "+homekey.isHomeflag());
        if(timer!=null) {
            timer.cancel();
        }
        timer = new Timer();

        super.onResume();  // Always call the superclass method first

        // Get the Camera instance as the activity achieves full user focus

    }
}


