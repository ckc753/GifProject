package com.multi.chlru.gifproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.multi.chlru.gifproject.main.MainActivity;

public class SplashActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{
            Thread.sleep(1000);

        }catch(InterruptedException e){
            e.printStackTrace();
        }
        startActivity(new Intent(this, MainActivity.class));
        finish(); //프래그먼트가 아닌이상, Intent를 이용한 화면전환시 띄워져있는 엑티비티 종료는 필수! (※중복열람을 방지※)
    }
}
