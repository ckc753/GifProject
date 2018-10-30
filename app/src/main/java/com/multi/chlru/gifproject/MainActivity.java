package com.multi.chlru.gifproject;

import android.Manifest;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kakao.util.helper.log.Logger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends HannaFontActivity{ //한나체 클래스 상속을 통해 폰트적용
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
    SweetAlertDialog sweetalert;
    TabLayout tabs;

    //Storage권한 사용할 객체
    private static final int MY_PERMISSON_STORAGE = 1111;

    //뒤로가기 버튼 입력시간이 담길long 객체
    private  long pressedTime = 0;
    //리스너 생성
    public interface onBackPressedListener{ public void onBack();  }
    //리스너 객체 생성
    private onBackPressedListener mBackListener;
    //리스너 설정 메소드
    public void setOnBackPressedListener(onBackPressedListener listener){  mBackListener = listener; }

    //0.1 취소버튼 클릭시
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

        //0.2 구글 앱서명키(SHA1) 인코딩 -> 해시키 변환9\
        //byte[] sha1 = { 0x3B, (byte)0xDA, (byte)0xA0, 0x5B, 0x4F, 0x35, 0x71, 0x02, 0x4E, 0x27, 0x22, (byte)0xB9, (byte)0xAc, (byte)0xB2, 0x77, 0x2F,(byte)0x9D, (byte)0xA9, (byte)0x9B, (byte)0xD9  };
        byte[] sha2 = { 0x20,0x7F,0x6D,(byte)0x9A,(byte)0xB9,0x36,0x21,(byte)0x0A,(byte)0xEE,0x14,0x67,(byte)0xAC,(byte)0x92,(byte)0x96,(byte)0xE6,(byte)0xFE,(byte)0xEC,0x3F,(byte)0x94,(byte)0xF5  };
        Logger.e("Release Google_Signing keyHash: " + Base64.encodeToString(sha2, Base64.NO_WRAP));


        //1. 앱실행시 직접적으로 저장권한설정 메소드
        checkPermission();checkPermission();

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

        //2. 검색 시작////////////////////////////////////////////
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
            //google이메일 @로 끊어서 배열만들기
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

        //3. 메뉴버튼 설정
        menuBtn = (ImageButton) findViewById(R.id.menuBtn);
        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getApplicationContext(), "open", Toast.LENGTH_SHORT).show();
                drawerLayout.openDrawer(linearLayout); //메뉴버튼클릭시 drawerLayout.openDrawer속성에의해 만들어져있던 linearLayout을 슬라이드효과로 연다.(listView대신 LinearLayout를 사용함으로써 메뉴버튼클릭시, linearLayout내부에 MainLoginButton을 적용시킨채로 표현가능)
            }
        });

        //4. 로그인버튼 설정
        MainLoginButton = (Button)findViewById(R.id.MainLoginButton);
        intent = getIntent();
        final String name = intent.getStringExtra("name");

        //5. 로그인기능 설정 - 시작////////////////////////////////////////
        if(temp != null){
            String tempname[]=temp.split("@");
            slidetext.setText(tempname[0]);
            //Toast.makeText(this, tempname[0]+"님 환영합니다", Toast.LENGTH_SHORT).show();
            MainLoginButton.setText("로그아웃 ");
            MainLoginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sessionedit.remove("sessionid");
                    sessionedit.remove("sessonpk");
                    sessionedit.commit();
                    temp = sessionsp.getString("sessionid",null);
                    pkid=sessionsp.getString("sessonpk",null);
                    //Toast.makeText(getApplicationContext(),"로그아웃하셨습니다.",Toast.LENGTH_LONG).show();
                    drawerLayout.closeDrawer(linearLayout);
                    //로그인시, 업로드창에서 로그아웃하면 이용하지못하도록 Fragment.replace처리
                    TabLayout.Tab tabposition=tabs.getTabAt(0);//tab포지션을 0으로 다시 설정
                    tabposition.select();
                    //getSupportFragmentManager().beginTransaction().replace(R.id.container2, fragment1).commit();
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
        //로그인기능 설정-끝 /////////////////////////////////////////////////////////////////////

        //6. 메인화면 defalut화면을 fragment1로 설정.
        getSupportFragmentManager().beginTransaction().replace(R.id.container2, fragment1).commit();

        //7. 상단에 3가지 Tab 기능 설정.
        tabs = (TabLayout) findViewById(R.id.tabs);
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
                        /*aDialog.setTitle("＊＊＊ 로그인 하세요 ＊＊＊");
                        aDialog.setMessage("회원이 아니기때문에 업로드를 이용할 수 없습니다.");
                        aDialog.setPositiveButton("확인", null);
                        aDialog.show();*/
                        sweetalert = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE);
                        sweetalert.setTitleText("＊＊＊ 로그인 하세요 ＊＊＊");
                        sweetalert.setContentText("회원이 아니기때문에 업로드를 이용할 수 없습니다.");
                        sweetalert.setConfirmText("확인");
                        sweetalert.show();
                        TabLayout.Tab tabposition=tabs.getTabAt(0);//tab포지션을 0으로 다시 설정
                        tabposition.select();
                        selected = fragment1;
                    }
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.container2, selected).commit();
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {           }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {           }
        });
        getHashKey();
    }//onCreate_end
    ////////////////////////////////////////////////////

    //권한설정 시작//////////////////////////////////////////////////////////////////
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
                        //Toast.makeText(MainActivity.this, "해당 권한을 활성화 하셔야 합니다.", Toast.LENGTH_SHORT).show();
                        sweetalert=new SweetAlertDialog(MainActivity.this,SweetAlertDialog.WARNING_TYPE);
                        sweetalert.setTitleText("＊＊＊ 경고 ＊＊＊");
                        sweetalert.setContentText("해당 권한을 활성화 하셔야 합니다.");
                        sweetalert.setConfirmText("확인");
                        sweetalert.show();
                        return;
                    }
                }
                break;
        }
    }
    //권한설정 끝 /////////////////////////////////////////////////////////////

    //검색관련 프레그먼트 띄우기 시작/////////////////////////////////////////
    //SearchFragment띄우기1
    private void openSearchFragment1(){
        String searchtxt = editText.getText().toString();
        Bundle searchbundle = new Bundle();
        searchbundle.putString("SearchTxt", searchtxt);
        fragment_search.setArguments(searchbundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.container2, fragment_search).commit();
    }
    //SearchFragment띄우기2
    private void openSearchFragment2(){
        String searchtxt = editText.getText().toString();
        Bundle searchbundle = new Bundle();
        searchbundle.putString("SearchTxt", searchtxt);
        fragment_search2.setArguments(searchbundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.container2, fragment_search2).commit();
    }
    //SearchFragment1 닫기
    private void removeFragment1() {
        getSupportFragmentManager().beginTransaction()
                .remove(fragment_search).commit();
    }
    //SearchFragment2 닫기
    private void removeFragment2() {
        getSupportFragmentManager().beginTransaction()
                .remove(fragment_search2).commit();
    }
    //검색관련 프레그먼트 띄우기 종료//////////////////////////////////////////////

    //getHashKey메소드/////////////////////////////////////////////////////////////
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



    //8. 메뉴버튼클릭시 =>> listView를 이용한 메뉴슬라이드 (버튼클릭시, AlertDialog실행되도록)
    private class DrawerItemListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> av, View view, int pos, long id) {
            Object vo = (Object)av.getAdapter().getItem(pos);
            String name = vo.toString();
            if(temp!=null) {
                switch (pos) {
                    /*case 0:
                        Toast.makeText(getApplicationContext(), name, Toast.LENGTH_SHORT).show();
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
                        break;*/
                    case 0:
                        sweetalert=new SweetAlertDialog(MainActivity.this,SweetAlertDialog.WARNING_TYPE);
                        sweetalert.setTitleText("＊＊＊ 2018/10/16공지 ＊＊＊");
                        sweetalert.setContentText("어플이 개발중입니다! \n 곧 움짤어플이 출시될 예정이오니 많은 관심바랍니다. ^_^");
                        sweetalert.setConfirmText("확인");
                        sweetalert.show();
                        break;
                    case 1:
                        sweetalert=new SweetAlertDialog(MainActivity.this,SweetAlertDialog.WARNING_TYPE);
                        sweetalert.setTitleText("＊＊＊ 2018/10/16공지 ＊＊＊");
                        sweetalert.setContentText("어플이 개발중입니다! \n 곧 움짤어플이 출시될 예정이오니 많은 관심바랍니다. ^_^");
                        sweetalert.setConfirmText("확인");
                        sweetalert.show();
                        break;
                    case 2:
                        Bundle searchbundle = new Bundle();
                        searchbundle.putString("pkid", pkid);
                        fragment_member.setArguments(searchbundle);
                        getSupportFragmentManager().beginTransaction().replace(R.id.container2, fragment_member).commit();
                        break;


                }//switch_end
            }else{//if_end
                switch (pos) {
                    case 0:
                        sweetalert=new SweetAlertDialog(MainActivity.this,SweetAlertDialog.WARNING_TYPE);
                        sweetalert.setTitleText("＊＊＊ 2018/10/16공지 ＊＊＊");
                        sweetalert.setContentText("어플이 개발중입니다! \n 곧 움짤어플이 출시될 예정이오니 많은 관심바랍니다. ^_^");
                        sweetalert.setConfirmText("확인");
                        sweetalert.show();
                        break;
                    case 1:
                        sweetalert=new SweetAlertDialog(MainActivity.this,SweetAlertDialog.WARNING_TYPE);
                        sweetalert.setTitleText("＊＊＊ 2018/10/16공지 ＊＊＊");
                        sweetalert.setContentText("어플이 개발중입니다! \n 곧 움짤어플이 출시될 예정이오니 많은 관심바랍니다. ^_^");
                        sweetalert.setConfirmText("확인");
                        sweetalert.show();
                        break;
                }//switch_end
            }
            drawerLayout.closeDrawer(linearLayout);
        }
    }
}