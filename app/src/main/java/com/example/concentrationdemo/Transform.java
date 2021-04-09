package com.example.concentrationdemo;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

public class Transform implements ViewPager.PageTransformer {
    private static final float MINSCALE=0.5f;
    @Override
    public void transformPage(@NonNull View page, float position) {
        if(position<-1)
            position=-1;
        else if(position>1)
            position=1;
        //pos = 0 返回1
        //pos = 1 返回0
        float tempScale=position<0?1+position:1-position;
        float scaleValue=MINSCALE+tempScale*0.5f;
        page.setScaleX(scaleValue);
        page.setScaleY(scaleValue);
    }
}
