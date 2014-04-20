package com.shuai.hehe.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;

import com.shuai.base.view.BaseFragmentActivity;
import com.shuai.hehe.R;
import com.shuai.utils.DisplayUtils;
import com.umeng.update.UmengUpdateAgent;

public class MainActivity extends BaseFragmentActivity implements OnClickListener {
	private static final String TAG="MainActivity";
	private Context mContext;
    private View mTitleContainer;
    private FeedFragment mFeedFragment;
    private ImageButton mIbMenuMore;
    private PopupWindow mMainMenuWindow;
    
    /**
     * 记录上次主菜单dismiss的时间
     * 该变量用于处理当菜单已展示时单击显示菜单按钮，菜单先消失然后又展示的问题
     */
    private long mMainMenuLastDismissTime=System.currentTimeMillis();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext=this;
		//检查升级
		UmengUpdateAgent.update(this);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);		
		setContentView(R.layout.activity_main);
		
		mTitleContainer=findViewById(R.id.rl_title);
		mIbMenuMore=(ImageButton) findViewById(R.id.ib_menu_more);
		mFeedFragment=(FeedFragment)getSupportFragmentManager().findFragmentById(R.id.feed_fragment);
		mTitleContainer.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                mFeedFragment.onTitleClicked();
                
            }
        });
		
		mIbMenuMore.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showMenu();
			}
		});
	}
	
	private void showMenu(){
		//处理当菜单已展示时单击显示菜单按钮，菜单先消失然后又展示的问题
		//Log.i(TAG, Long.toString(System.currentTimeMillis() - mMainMenuLastDismissTime));
		if (System.currentTimeMillis() - mMainMenuLastDismissTime <= 200) {
			return;
		}
		
		if (mMainMenuWindow != null && mMainMenuWindow.isShowing()) {
			mMainMenuWindow.dismiss();
		} else {
			if (mMainMenuWindow == null) {
				mMainMenuWindow = new PopupWindow(mContext);
				mMainMenuWindow.setBackgroundDrawable(new BitmapDrawable());
				mMainMenuWindow.setOutsideTouchable(true);
				mMainMenuWindow.setOnDismissListener(new OnDismissListener() {
					
					@Override
					public void onDismiss() {
						mMainMenuLastDismissTime=System.currentTimeMillis();
					}
				});
				LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View view = inflater.inflate(R.layout.main_menu, null);
				mMainMenuWindow.setContentView(view);
				mMainMenuWindow.setWidth(DisplayUtils.dp2px(mContext, 150));
				mMainMenuWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

				view.findViewById(R.id.tv_myfav).setOnClickListener(this);
				view.findViewById(R.id.tv_feedback).setOnClickListener(this);
				view.findViewById(R.id.tv_about).setOnClickListener(this);
			}
			mMainMenuWindow.showAsDropDown(mIbMenuMore);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		showMenu();
		return false;
	}

	@Override
	public void onClick(View v) {
		if(mMainMenuWindow!=null){
			mMainMenuWindow.dismiss();
		}
		
		//响应选中菜单项
		int id=v.getId();
		switch (id) {
		case R.id.tv_about:
		{
			Intent intent=new Intent(mContext, AboutActivity.class);
			startActivity(intent);
		}
			break;
		default:
			break;
		}
	}

}
