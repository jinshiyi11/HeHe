package com.shuai.base.view;

import com.shuai.hehe.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import io.vov.vitamio.widget.MediaController;

public class MediaControllerEx extends MediaController {

    public MediaControllerEx(Context context) {
        super(context);
    }

    public MediaControllerEx(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected View makeControllerView() {
        return ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.mediacontroller, this);
    }
    

}
