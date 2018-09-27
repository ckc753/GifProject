package com.example.chlru.gifproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    Fragment fragment1;
    Fragment fragment2;
    Fragment fragment3;
    Fragment fragment5;
    final String[] navItems = {"내가올린움짤", "공지사항", "이벤트", "광고문의"};
    ListView listView;
    DrawerLayout drawerLayout;
    Button menuBtn;
    Intent intent;

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

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);

        fragment1 = new Fragment1();
        fragment2 = new Fragment2();
        fragment3 = new Fragment3();
        fragment5 = new Fragment5();

        listView = (ListView) findViewById(R.id.slide_listView);
        drawerLayout = (DrawerLayout) findViewById(R.id.main_drawer);
        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, navItems));
        listView.setOnItemClickListener(new DrawerItemListener());
        menuBtn = (Button) findViewById(R.id.menuBtn);
        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "open", Toast.LENGTH_SHORT).show();
                drawerLayout.openDrawer(listView);
            }
        });

        getSupportFragmentManager().beginTransaction().replace(R.id.container2, fragment1).commit();

        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.addTab(tabs.newTab().setText("베스트 움짤"));
        tabs.addTab(tabs.newTab().setText("주제별 움짤"));
        tabs.addTab(tabs.newTab().setText("업로드 하기"));
        tabs.addTab(tabs.newTab().setText("파이어연동다운"));

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
                    selected = fragment3;
                } else if (position == 3) {
                    selected = fragment5;
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

    }

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
