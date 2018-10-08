package com.example.chlru.gifproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.view.textclassifier.TextClassificationSessionId;
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

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(listView)) {
            drawerLayout.closeDrawer(listView);
        } else {
            super.onBackPressed();
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
        editText=(EditText)findViewById(R.id.editText);
        mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        mInputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);

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
        //if (temp != null | name != null | user != null){
            Toast.makeText(this, temp+"님 환영합니다", Toast.LENGTH_SHORT).show();

            //String username = user.getDisplayName();
            ////String email = user.getEmail();
            //Uri photo_url = user.getPhotoUrl();
            //String uid = user.getUid();

            //if(email != null) { //구글회원정보
            //    Toast.makeText(getApplicationContext(), email+"님 환영합니다", Toast.LENGTH_LONG).show();
            //}else if(name != null ){ //카카오회원정보
            //    Toast.makeText(getApplicationContext(),name+"님 환영합니다.",Toast.LENGTH_LONG).show();
            //}

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

                    break;
                }
            }
            drawerLayout.closeDrawer(listView);
        }
    }
}