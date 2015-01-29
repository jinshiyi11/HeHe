package com.shuai.utils;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;

import com.shuai.hehe.R;

public class AnimUtils {
    public static enum SlideType{
        SLIDE_IN_FROM_TOP,
        SLIDE_OUT_FROM_TOP,
        SLIDE_IN_FROM_BOTTOM,
        SLIDE_OUT_FROM_BOTTOM
    }
    
    public static Animation slideAnimation(final Context context,final SlideType type,final View target){
        Animation animation = null;
        switch (type) {
        case SLIDE_IN_FROM_BOTTOM:
            animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_from_bottom);
            break;
        case SLIDE_IN_FROM_TOP:
            animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_from_top);
            break;
        case SLIDE_OUT_FROM_BOTTOM:
            animation = AnimationUtils.loadAnimation(context, R.anim.slide_out_from_bottom);
            break;
        case SLIDE_OUT_FROM_TOP:
            animation = AnimationUtils.loadAnimation(context, R.anim.slide_out_from_top);
            break;
        default:
            break;
        }
        
        animation.setAnimationListener(new AnimationListener() {
            
            @Override
            public void onAnimationStart(Animation animation) {
            }
            
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
            
            @Override
            public void onAnimationEnd(Animation animation) {
                switch (type) {
                case SLIDE_IN_FROM_BOTTOM:
                    target.setVisibility(View.VISIBLE);
                    break;
                case SLIDE_IN_FROM_TOP:
                    target.setVisibility(View.VISIBLE);
                    break;
                case SLIDE_OUT_FROM_BOTTOM:
                    target.setVisibility(View.GONE);
                    break;
                case SLIDE_OUT_FROM_TOP:
                    target.setVisibility(View.GONE);
                    break;
                default:
                    break;
                
                }
            }
        });
        
        return animation;
    }

}
