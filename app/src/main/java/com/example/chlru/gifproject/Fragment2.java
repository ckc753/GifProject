package com.example.chlru.gifproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class Fragment2 extends Fragment implements View.OnClickListener {
    ViewGroup view2;
    private Button [] cButton = new Button[9];
    private ArrayList<String> cDataList;
    Intent intent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view2 = (ViewGroup) inflater.inflate(R.layout.fragment2, container, false);

        cDataList = new ArrayList<>();
        cButton[0] = (Button) view2.findViewById(R.id.CBtn1);
        cButton[1] = (Button) view2.findViewById(R.id.CBtn2);
        cButton[2] = (Button) view2.findViewById(R.id.CBtn3);
        cButton[3] = (Button) view2.findViewById(R.id.CBtn4);
        cButton[4] = (Button) view2.findViewById(R.id.CBtn5);
        cButton[5] = (Button) view2.findViewById(R.id.CBtn6);
        cButton[6] = (Button) view2.findViewById(R.id.CBtn7);
        cButton[7] = (Button) view2.findViewById(R.id.CBtn8);
        cButton[8] = (Button) view2.findViewById(R.id.CBtn9);

        for (int i = 0; i <9 ; i++) {
            //버튼의 포지션(배열에서의 index)를 태그로 저장
            cButton[i].setTag(i);

            //클릭 리스너 등록
            cButton[i].setOnClickListener(this);

            //출력데이터 생성
            cDataList.add("하이" + i + "입니다");
        }

        return view2;
    }

    @Override
    public void onClick(View v) {
        //클릭된 뷰를 버튼으로 받아옴
        Button newButton = (Button) v;

        for(Button tempButton : cButton){
            if(tempButton == newButton){
                int position = (Integer)v.getTag();
                String name = newButton.getText().toString();
                intent = new Intent(getContext(),CategoryActivity.class);
                intent.putExtra("주제 id",position);
                intent.putExtra("Buttonname",name);
                startActivity(intent);

                Toast.makeText(getContext(), cDataList.get(position), Toast.LENGTH_SHORT).show();

            }
        }//for

    }
}



