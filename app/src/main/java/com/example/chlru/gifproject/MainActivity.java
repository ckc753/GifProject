package com.example.chlru.gifproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    Fragment fragment1;
    Fragment fragment2;
    Fragment fragment3;
    Fragment fragment_search;
    EditText editText;
    InputMethodManager mInputMethodManager;
    final String[] navItems = {"내가올린움짤", "공지사항", "이벤트", "광고문의"};
    ListView listView;
    DrawerLayout drawerLayout;
    Button menuBtn;
    TextView MainLoginButton;
    Intent intent;
    FirebaseAuth auth;
    FirebaseUser user;
    String temp;
    Button searchBtu;
    //뒤로가기 버튼 입력시간이 담길long 객체
    private  long pressedTime = 0;
    //리스너 생성
    public interface onBackPressedListener{
        public void onBack();
    }
    //리스너 객체 생성
    private onBackPressedListener mBackListener;
    //리스너 설정 메소드
    public void setOnBackPressedListener(onBackPressedListener listener){
        mBackListener = listener;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(listView)) {
            drawerLayout.closeDrawer(listView);
        } else {
            if(mBackListener != null){ //다른 Fragment에서 리스너를 설정했을 때 처리
                mBackListener.onBack();
                Log.e("!!!","Listener is not null");
            }else{ //리스너가 설정되지 않은상태(ex.메인Fragment)라면 뒤로가기 연속2번클릭시 앱종료
                Log.e("!!!","Listener is null");
                if(pressedTime == 0){
                    Toast.makeText(getApplicationContext(),
                            "한번더 누르면 종료됩니다.",Toast.LENGTH_LONG).show();
                    pressedTime = System.currentTimeMillis();
                }else{ // pressedTime != 0인 경우
                    int seconds = (int)(System.currentTimeMillis() - pressedTime);
                    if(seconds > 2000){
                        Toast.makeText(getApplicationContext(),
                                "한번더 누르면 종료됩니다.",Toast.LENGTH_LONG).show();
                        pressedTime = 0;
                    }else{
                        super.onBackPressed();
                        Log.e("!!!","onBakcPressed : finish,KillProcess");
                        finish();
                        finishAffinity(); //카카오톡 종료 (로그인 ->메인 -> 로그인 Activity중복에러완료)
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                }
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);

        fragment1 = new Fragment1();
        fragment2 = new Fragment2();
        fragment3 = new Fragment3();
        fragment_search=new Fragment_search();
        editText=(EditText)findViewById(R.id.editText);
        mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        mInputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);





        searchBtu=(Button)findViewById(R.id.searchBtu);
        searchBtu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean k=fragment_search.isVisible();
                if(k!=true){
                    String searchtxt = editText.getText().toString();
                    Bundle searchbundle = new Bundle();
                    searchbundle.putString("SearchTxt", searchtxt);
                    fragment_search.setArguments(searchbundle);
                    //Toast.makeText(getApplicationContext(), "검색1회차 "+String.valueOf(k), Toast.LENGTH_LONG).show();
                    getSupportFragmentManager().beginTransaction().replace(R.id.container2, fragment_search).commit();
                    editText.setText("");
                }else{
                    getSupportFragmentManager().beginTransaction().remove(fragment_search).commit();
                    // fragment_search.onDestroy();
                    String searchtxt = editText.getText().toString();
                    Bundle searchbundle = new Bundle();
                    searchbundle.putString("SearchTxt", searchtxt);
                    fragment_search.setArguments(searchbundle);
                    //                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          Toast.makeText(getApplicationContext(), "검색다회차 "+String.valueOf(k), Toast.LENGTH_LONG).show();
                    getSupportFragmentManager().beginTransaction().replace(R.id.container2, fragment_search).commit();
                    editText.setText("");
                }
            }
        });
        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener(){

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    boolean k=fragment_search.isVisible();
                    if(k!=true){
                        String searchtxt = editText.getText().toString();
                        Bundle searchbundle = new Bundle();
                        searchbundle.putString("SearchTxt", searchtxt);
                        fragment_search.setArguments(searchbundle);
                        // Toast.makeText(getApplicationContext(), "검색1회차 "+String.valueOf(k), Toast.LENGTH_LONG).show();
                        getSupportFragmentManager().beginTransaction().replace(R.id.container2, fragment_search).commit();
                        editText.setText("");
                        return true;
                    }else{
                        getSupportFragmentManager().beginTransaction()
                                .remove(fragment_search).commit();
                        // fragment_search.onDestroy();
                        String searchtxt = editText.getText().toString();
                        Bundle searchbundle = new Bundle();
                        searchbundle.putString("SearchTxt", searchtxt);
                        fragment_search.setArguments(searchbundle);
                        // Toast.makeText(getApplicationContext(), "검색다회차 "+String.valueOf(k), Toast.LENGTH_LONG).show();
                        getSupportFragmentManager().beginTransaction().replace(R.id.container2, fragment_search).commit();
                        editText.setText("");
                        return true;
                    }
                }


                return false;
            }//onEditorAction_end
        });

        listView = (ListView) findViewById(R.id.slide_listView);
        drawerLayout = (DrawerLayout) findViewById(R.id.main_drawer);
        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, navItems));
        listView.setOnItemClickListener(new DrawerItemListener());
        menuBtn = (Button) findViewById(R.id.menuBtn);
        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getApplicationContext(), "open", Toast.LENGTH_SHORT).show();
                drawerLayout.openDrawer(listView);
            }
        });

        MainLoginButton = (TextView)findViewById(R.id.MainLoginButton);
        intent = getIntent();
        String name = intent.getStringExtra("name");

        SharedPreferences sessionsp = getSharedPreferences("session", 0);
        final SharedPreferences.Editor sessionedit = sessionsp.edit();
        temp = sessionsp.getString("sessionid",null); //만약 defValue를 ""로 했다면 로그아웃시에도 ""로 해야한다

        if(temp != null){
            Toast.makeText(this, temp+"님 환영합니다", Toast.LENGTH_SHORT).show();
            MainLoginButton.setText("로그아웃 ");
            MainLoginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sessionedit.remove("sessionid");
                    sessionedit.commit();
                    intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }else if(temp ==null | name == null & user ==null){
            Toast.makeText(getApplicationContext(),"로그인하세요",Toast.LENGTH_LONG).show();
            MainLoginButton.setText("로그인하시오 ");
            MainLoginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.container2, fragment1).commit();

        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.addTab(tabs.newTab().setText("베스트 움짤"));
        tabs.addTab(tabs.newTab().setText("주제별 움짤"));
        tabs.addTab(tabs.newTab().setText("업로드 하기"));

        tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mInputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                int position = tab.getPosition();
                Log.d("MainActivity", "선택된 탭 : " + position);

                Fragment selected = null;
                if (position == 0) {
                    selected = fragment1;
                } else if (position == 1) {
                    selected = fragment2;
                } else if (position == 2) {
                    selected = fragment3;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.container2, selected).commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        getHashKey();
    }//onCreate_end
    ////////////////////////////////////////////////////
    private void getHashKey(){
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo == null)
            Log.e("KeyHash", "KeyHash:null");

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            } catch (NoSuchAlgorithmException e) {
                Log.e("KeyHash", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
    }
    ////////////////////////////////////////////////////

    private class DrawerItemListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> av, View view, int pos, long id) {
            Object vo = (Object)av.getAdapter().getItem(pos);
            String name = vo.toString();
            switch (pos) {
                case 0:
                    Toast.makeText(getApplicationContext(), name, Toast.LENGTH_SHORT).show();
                    break;

                case 1:
                    Toast.makeText(getApplicationContext(), name, Toast.LENGTH_SHORT).show();
                    break;

                case 2:
                    Toast.makeText(getApplicationContext(), name, Toast.LENGTH_SHORT).show();
                    break;

                case 3: {
                    intent = new Intent(getApplicationContext(), AdActivity.class);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), name, Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                }
            }
            drawerLayout.closeDrawer(listView);
        }
    }
}