package com.example.chlru.gifproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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

//TextInputLayout생략
public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        chk_auto = (CheckBox) findViewById(R.id.autoLoginCheck);
        BasicLoginButton  = (Button) findViewById(R.id.BasicLoginButton);
        editTextEmail = (EditText)findViewById(R.id.editTextEmail);
        editTextPw = (EditText)findViewById(R.id.editTextPw);

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

        setting = getSharedPreferences("setting", 0);
        editor = setting.edit();
        if (setting.getBoolean("chk_auto", false)) {
            editTextEmail.setText(setting.getString("ID", ""));
            editTextPw.setText(setting.getString("PW", ""));
            chk_auto.setChecked(true);
        }
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
                } else {
                    editor.clear();
                    editor.commit();
                }

                if(editTextEmail.getText().toString().getBytes().length <= 0 || editTextPw.getText().toString().getBytes().length <= 0){//빈값이 넘어올때의 처리
                    Toast.makeText(getApplicationContext(), "값을 입력하세요.", Toast.LENGTH_SHORT).show();
                }else{
                    //String temp1 = "이메일 입력 내용 : " + editTextEmail.getText().toString();
                    //String temp2 = "비번 입력 내용 : " + editTextPw.getText().toString();
                    //Toast.makeText(getApplicationContext(), temp1+" "+temp2, Toast.LENGTH_SHORT).show();
                    signUser(editTextEmail.getText().toString(), editTextPw.getText().toString());
                }
            }
        });

        TextView SignPage = (TextView)findViewById(R.id.SignPage);
        SignPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignActivity.class);
                startActivity(intent);
                finish();
            }
        });

        editTextPw.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editTextPw.setOnEditorActionListener(new TextView.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

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

                if(editTextEmail.getText().toString().getBytes().length <= 0 || editTextPw.getText().toString().getBytes().length <= 0){//빈값이 넘어올때의 처리
                    Toast.makeText(getApplicationContext(), "값을 입력하세요.", Toast.LENGTH_SHORT).show();
                }
                else if (actionId == EditorInfo.IME_ACTION_DONE) {
                    signUser(editTextEmail.getText().toString(), editTextPw.getText().toString());
                    return true;
                }
                return false;
            }//onEditorAction_end
        });

        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);
    }//onCreate_end

    public void signUser(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(editTextEmail.getText().toString().equals("manager") && editTextPw.getText().toString().equals("manager")){
                            Intent intent = new Intent(getApplicationContext(), ManagerActivity.class);
                            startActivity(intent);
                            finish();

                        }else {
                            if (!task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "로그인실패", Toast.LENGTH_LONG).show();
                            } else {

                                sessionsp = getSharedPreferences("session", 0);
                                sessionedit = sessionsp.edit();
                                sessionedit.putString("sessionid", editTextEmail.getText().toString());
                                sessionedit.commit();

                                //Toast.makeText(getApplicationContext(),"로그인완료",Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }
                });
    }

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

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            sessionsp = getSharedPreferences("session", 0);
                            sessionedit = sessionsp.edit();
                            sessionedit.putString("sessionid", mAuth.getCurrentUser().getEmail());
                            sessionedit.commit();

                            //구글로그인시 입력값이 들어간다면 Session할 수 있도록
                            //Toast.makeText(getApplicationContext(),"아이디생성완료",Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {

                        }
                    }
                });
    }//firebaseAuthWithGoogle_end

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }//onConnectionFailed_end

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseAuth.getInstance().signOut();
    }//onStop_end

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

                @Override
                public void onSuccess(UserProfile userProfile) {

                    Log.e("★UserProfile★", userProfile.toString()); //Logcat ==> Debug로 확인가능하다.
                    String name = userProfile.getNickname();

                    sessionsp = getSharedPreferences("session", 0);
                    sessionedit = sessionsp.edit();
                    sessionedit.putString("sessionid", userProfile.getNickname());
                    sessionedit.commit();

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);

                    //intent.putExtra("name", name);
                    //startActivityForResult(intent, 1);
                    finish();
                    finishAffinity();
                }
            });
        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
        }
    }//SessionCallBack_end

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();



    }
}


