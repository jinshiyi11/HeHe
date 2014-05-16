package com.shuai.base.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;

public class PopUpMenuButton extends ImageButton {
    private Context mContext;
    private PopupWindow mPopupMenuWindow;
    
    /**
     * 记录上次主菜单dismiss的时间
     * 该变量用于处理当菜单已展示时单击显示菜单按钮，菜单先消失然后又展示的问题
     */
    private long mMainMenuLastDismissTime=System.currentTimeMillis();
    private OnMenuListener mOnMenuListener;
    
    public interface OnMenuListener{
        public void onCreateMenu(PopupWindow popupWindow);
        
        public void onUpdateMenu(PopupWindow popupWindow);
    }

    public PopUpMenuButton(Context context) {
        super(context);
        mContext=context;
        init();
    }

    public PopUpMenuButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext=context;
        init();
    }

    public PopUpMenuButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext=context;
        init();
    }
    
    private void init(){
        setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                showMenu();
            }
        });
    }
    
    public void setOnMenuListener(OnMenuListener listener){
        mOnMenuListener=listener;
    }
    
    public void showMenu() {
      //处理当菜单已展示时单击显示菜单按钮，菜单先消失然后又展示的问题
        //Log.i(TAG, Long.toString(System.currentTimeMillis() - mMainMenuLastDismissTime));
        if (System.currentTimeMillis() - mMainMenuLastDismissTime <= 200) {
            return;
        }
        
        if (mPopupMenuWindow != null && mPopupMenuWindow.isShowing()) {
            mPopupMenuWindow.dismiss();
        } else {
            if (mPopupMenuWindow == null) {
                mPopupMenuWindow = new PopupWindow(mContext);
                mPopupMenuWindow.setBackgroundDrawable(new BitmapDrawable());
                mPopupMenuWindow.setOutsideTouchable(true);
                mPopupMenuWindow.setOnDismissListener(new OnDismissListener() {
                    
                    @Override
                    public void onDismiss() {
                        mMainMenuLastDismissTime=System.currentTimeMillis();
                    }
                });
                mOnMenuListener.onCreateMenu(mPopupMenuWindow);
                
            }else {
                mOnMenuListener.onUpdateMenu(mPopupMenuWindow);
            }
            mPopupMenuWindow.showAsDropDown(this);
        }
    }
    
    public void hideMenu(){
        if(mPopupMenuWindow!=null && mPopupMenuWindow.isShowing()){
            mPopupMenuWindow.dismiss();
        }
    }
    
    

}
