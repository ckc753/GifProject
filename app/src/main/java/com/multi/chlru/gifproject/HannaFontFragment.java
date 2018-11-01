package com.multi.chlru.gifproject;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class HannaFontFragment extends Fragment {
    private static Typeface typeface;

    @Override

    public void onActivityCreated(@Nullable Bundle savedInstanceState)

    {
        super.onActivityCreated(savedInstanceState);
        if(typeface == null) {
            typeface = Typeface.createFromAsset(getActivity().getAssets(), "BMHANNA_11yrs_ttf.ttf");
        }
        setGlobalFont(getActivity().getWindow().getDecorView());
    }

    private void setGlobalFont(View view) {
        if(view != null) {
            if(view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup)view;
                int vgCnt = viewGroup.getChildCount();
                for(int i = 0; i<vgCnt; i++) {
                    View v = viewGroup.getChildAt(i);
                    if(v instanceof TextView) {
                        ((TextView) v).setTypeface(typeface);
                    }
                    setGlobalFont(v);
                }
            }
        }
    }
}
