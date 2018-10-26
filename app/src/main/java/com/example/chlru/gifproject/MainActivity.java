package com.example.chlru.gifproject;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends HannaFontActivity{
    Toolbar toolbar;
    Fragment fragment1;
    Fragment fragment2;
    Fragment fragment3;
    Fragment fragment_search;
    Fragment fragment_search2;
    Fragment fragment_member;
    EditText editText;
    InputMethodManager mInputMethodManager;
    ListView listView;
    DrawerLayout drawerLayout;
    LinearLayout linearLayout;
    ImageButton menuBtn;
    Button MainLoginButton;
    TextView slidetext;
    Intent intent;
    FirebaseAuth auth;
    FirebaseUser user;
    String temp;
    String pkid;
    ImageButton searchBtu;
    AlertDialog.Builder aDialog;
    SharedPreferences sessionsp;
    private static final int MY_PERMISSON_STORAGE = 1111;

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
        if (drawerLayout.isDrawerOpen(linearLayout)) {
            drawerLayout.closeDrawer(linearLayout);
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
        aDialog = new AlertDialog.Builder(new ContextThemeWrapper(this,R.style.AlertDialog));
        sessionsp = getSharedPreferences("session", 0);
        final SharedPreferences.Editor sessionedit = sessionsp.edit();
        temp = sessionsp.getString("sessionid",null); //만약 defValue를 ""로 했다면 로그아웃시에도 ""로 해야한다
        pkid=sessionsp.getString("sessonpk",null);
        checkPermission();
        //Toast.makeText(getApplicationContext(), pkid, Toast.LENGTH_LONG).show();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);

        fragment1 = new Fragment1();
        fragment2 = new Fragment2();
        fragment3 = new Fragment3();
        fragment_search=new Fragment_search();
        fragment_search2=new Fragment_search2();
        fragment_member=new Fragment_member();
        editText=(EditText)findViewById(R.id.editText);




        //검색 시작////////////////////////////////////////////

        searchBtu=(ImageButton)findViewById(R.id.searchBtu);
        searchBtu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                boolean k=fragment_search.isVisible();
                boolean k2=fragment_search2.isVisible();
                //Toast.makeText(getApplicationContext(), String.valueOf(k)+" "+String.valueOf(k2), Toast.LENGTH_LONG).show();
                if(k==false&&k2==false){
                    //Toast.makeText(getApplicationContext(), "1", Toast.LENGTH_LONG).show();
                    openSearchFragment1();
                    editText.setText("");
                    mInputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);//키보드 내리기
                    mInputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);

                }else if(k==false&&k2==true){
                    //Toast.makeText(getApplicationContext(), "2", Toast.LENGTH_LONG).show();
                    removeFragment2();
                    openSearchFragment1();
                    editText.setText("");
                    mInputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);//키보드 내리기
                    mInputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                }else if(k==true&&k2==false){
                    //Toast.makeText(getApplicationContext(), "3", Toast.LENGTH_LONG).show();
                    removeFragment1();
                    openSearchFragment2();
                    editText.setText("");
                    mInputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);//키보드 내리기
                    mInputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                }else{
                    removeFragment1();
                    removeFragment2();
                }

            }
        });
        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener(){

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    boolean k=fragment_search.isVisible();
                    boolean k2=fragment_search2.isVisible();
                    //Toast.makeText(getApplicationContext(), String.valueOf(k)+" "+String.valueOf(k2), Toast.LENGTH_LONG).show();
                    if(k==false&&k2==false){
                        //Toast.makeText(getApplicationContext(), "1", Toast.LENGTH_LONG).show();
                        openSearchFragment1();
                        editText.setText("");

                    }else if(k==false&&k2==true){
                        //Toast.makeText(getApplicationContext(), "2", Toast.LENGTH_LONG).show();
                        removeFragment2();
                        openSearchFragment1();
                        editText.setText("");
                    }else if(k==true&&k2==false){
                        //Toast.makeText(getApplicationContext(), "3", Toast.LENGTH_LONG).show();
                        removeFragment1();
                        openSearchFragment2();
                        editText.setText("");
                    }else{
                        removeFragment1();
                        removeFragment2();
                    }
                }


                return false;
            }//onEditorAction_end
        });

        //검색 끝////////////////////////////////////////////

        listView = (ListView) findViewById(R.id.slide_listView);
        drawerLayout = (DrawerLayout) findViewById(R.id.main_drawer);
        linearLayout=(LinearLayout)findViewById(R.id.slide_layout);
        slidetext=(TextView)findViewById(R.id.slide_text);
        if(temp!=null){
            String tempname[]=temp.split("@");
            slidetext.setText(tempname[0]);
            final String navItems[] = {"공지사항", "이벤트","내가올린 움짤"};
            listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, navItems));
            listView.setOnItemClickListener(new DrawerItemListener());
        }else{
            final String navItems[] = {"공지사항", "이벤트"};
            listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, navItems));
            listView.setOnItemClickListener(new DrawerItemListener());
        }

        menuBtn = (ImageButton) findViewById(R.id.menuBtn);
        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getApplicationContext(), "open", Toast.LENGTH_SHORT).show();
                drawerLayout.openDrawer(linearLayout);
            }
        });

        MainLoginButton = (Button)findViewById(R.id.MainLoginButton);
        intent = getIntent();
        final String name = intent.getStringExtra("name");

        if(temp != null){
            String tempname[]=temp.split("@");
            slidetext.setText(tempname[0]);
            Toast.makeText(this, tempname[0]+"님 환영합니다", Toast.LENGTH_SHORT).show();
            MainLoginButton.setText("로그아웃 ");
            MainLoginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sessionedit.remove("sessionid");
                    sessionedit.remove("sessonpk");
                    sessionedit.commit();
                    temp = sessionsp.getString("sessionid",null);
                    pkid=sessionsp.getString("sessonpk",null);
                    Toast.makeText(getApplicationContext(),"로그아웃하셨습니다.",Toast.LENGTH_LONG).show();
                    drawerLayout.closeDrawer(linearLayout);
                    //로그인시, 업로드창에서 로그아웃하면 이용하지못하도록 Fragment.replace처리
                    getSupportFragmentManager().beginTransaction().replace(R.id.container2, fragment1).commit();
                    //intent = new Intent(getApplicationContext(), MainActivity.class);
                    //startActivity(intent);
                    //finish();
                    slidetext.setText("환영합니다.");
                    MainLoginButton.setText("로그인 ");
                    MainLoginButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                }
            });
        }
        else if(temp ==null | name == null & user ==null){
            //Toast.makeText(getApplicationContext(),"로그인하세요",Toast.LENGTH_LONG).show();
            MainLoginButton.setText("로그인 ");
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

                int position = tab.getPosition();
                Log.d("MainActivity", "선택된 탭 : " + position);

                Fragment selected = null;
                if (position == 0) {
                    selected = fragment1;
                } else if (position == 1) {
                    selected = fragment2;
                } else if (position == 2) {
                    if(temp!=null) {
                        Bundle searchbundle = new Bundle();
                        searchbundle.putString("pkid", pkid);
                        fragment3.setArguments(searchbundle);
                        selected = fragment3;
                    }else{
                        //Toast.makeText(getApplicationContext(), "로그인하세요", Toast.LENGTH_LONG).show();
                        aDialog.setTitle("＊＊＊ 로그인 하세요 ＊＊＊");
                        aDialog.setMessage("회원이 아니기때문에 업로드를 이용할 수 없습니다.");
                        aDialog.setPositiveButton("확인", null);
                        aDialog.show();
                        selected = fragment1;
                    }
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

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                new AlertDialog.Builder(this).setTitle("알림").setMessage("저장소 권한이 거부되었습니다. 사용을 원하시면 설정에서 해당 권한을 직접 허용하셔야 합니다.").setNeutralButton("설정", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + getPackageName()));
                        startActivity(intent);
                    }
                }).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }).setCancelable(false).create().show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSON_STORAGE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSON_STORAGE:
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] < 0) {
                        Toast.makeText(MainActivity.this, "해당 권한을 활성화 하셔야 합니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                break;
        }
    }

    private void openSearchFragment1(){
        String searchtxt = editText.getText().toString();
        Bundle searchbundle = new Bundle();
        searchbundle.putString("SearchTxt", searchtxt);
        fragment_search.setArguments(searchbundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.container2, fragment_search).commit();
        //Toast.makeText(getApplicationContext(), "search1 open", Toast.LENGTH_LONG).show();

    }
    private void openSearchFragment2(){
        String searchtxt = editText.getText().toString();
        Bundle searchbundle = new Bundle();
        searchbundle.putString("SearchTxt", searchtxt);
        fragment_search2.setArguments(searchbundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.container2, fragment_search2).commit();
        //Toast.makeText(getApplicationContext(), "search2 open", Toast.LENGTH_LONG).show();
    }
    private void removeFragment1() {
        getSupportFragmentManager().beginTransaction()
                .remove(fragment_search).commit();
    }
    private void removeFragment2() {
        getSupportFragmentManager().beginTransaction()
                .remove(fragment_search2).commit();
    }

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
    //listView를 이용한 메뉴슬라이드 (ListView.OnItemClickListener 이용)
    private class DrawerItemListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> av, View view, int pos, long id) {
            Object vo = (Object)av.getAdapter().getItem(pos);
            String name = vo.toString();
            if(temp!=null) {
                switch (pos) {
                    /*case 0:
                        Toast.makeText(getApplicationContext(), name, Toast.LENGTH_SHORT).show();
                        break;

                    case 1:
                        aDialog.setTitle("＊＊＊ 내가 올린 움짤 ＊＊＊");
                        aDialog.setMessage("내가 올린 움짤 기능은 개발중입니다! \n좀 더 편리한 기능으로 찾아뵙겠습니다. ^_^");
                        aDialog.setPositiveButton("확인", null);
                        aDialog.show();
                        break;*/

                    case 0:
                        aDialog.setTitle("＊＊＊ 2018/10/16공지 ＊＊＊");
                        aDialog.setMessage("어플이 개발중입니다! \n 곧 움짤어플이 출시될 예정이오니 많은 관심바랍니다. ^_^");
                        aDialog.setPositiveButton("확인", null);
                        aDialog.show();
                        break;

                    case 1:
                        aDialog.setTitle("＊＊＊ 이달의 이벤트 ＊＊＊");
                        aDialog.setMessage("현재 진행중인 이벤트가 없습니다. \n 추후에 공지사항을 통해 미리 알려드리겠습니다. ^_^");
                        aDialog.setPositiveButton("확인", null);
                        aDialog.show();
                        break;
                    case 2:
                        Bundle memberpk = new Bundle();
                       // Toast.makeText(getApplicationContext(), pkid, Toast.LENGTH_SHORT).show();
                        memberpk.putString("pkid", pkid);
                        fragment_member.setArguments(memberpk);
                        getSupportFragmentManager().beginTransaction().replace(R.id.container2, fragment_member).commit();
                        break;


                }//switch_end
            }else{//if_end
                switch (pos) {
                    /*case 0:
                        Toast.makeText(getApplicationContext(), name, Toast.LENGTH_SHORT).show();
                        break;*/
                    case 0:
                        aDialog.setTitle("＊＊＊ 2018/10/16공지 ＊＊＊");
                        aDialog.setMessage("어플이 개발중입니다! \n 곧 움짤어플이 출시될 예정이오니 많은 관심바랍니다. ^_^");
                        aDialog.setPositiveButton("확인", null);
                        aDialog.show();
                        break;

                    case 1:
                        aDialog.setTitle("＊＊＊ 이달의 이벤트 ＊＊＊");
                        aDialog.setMessage("현재 진행중인 이벤트가 없습니다. \n추후에 공지사항을 통해 미리 알려드리겠습니다. ^_^");
                        aDialog.setPositiveButton("확인", null);
                        aDialog.show();
                        break;
                }//switch_end
            }
            drawerLayout.closeDrawer(linearLayout);
        }
    }

}