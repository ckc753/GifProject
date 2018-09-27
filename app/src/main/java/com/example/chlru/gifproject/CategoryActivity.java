package com.example.chlru.gifproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class CategoryActivity extends AppCompatActivity {
    Intent intent;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_main);

        intent = new Intent(this.getIntent());
        int result = intent.getIntExtra("주제 id",0);
        String name = intent.getStringExtra("Buttonname");

        TextView textView = findViewById(R.id.resultView);

        textView.setText("주제 id = " + result + " 버튼이름 = " + name);


    }
}
