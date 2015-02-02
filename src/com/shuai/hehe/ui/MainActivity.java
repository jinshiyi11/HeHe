package com.shuai.hehe.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.shuai.base.view.BaseFragmentActivity;
import com.shuai.base.view.PopUpMenuButton;
import com.shuai.base.view.PopUpMenuButton.OnMenuListener;
import com.shuai.hehe.R;
import com.shuai.utils.DisplayUtils;
import com.umeng.fb.FeedbackAgent;
import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.update.UmengUpdateAgent;

public class MainActivity extends BaseFragmentActivity implements OnClickListener {
	private static final String TAG="MainActivity";
    private View mTitleContainer;
    private FeedFragment mFeedFragment;
    private PopUpMenuButton mIbMenuMore;
    /**
     * 上次按下back按钮的时间
     */
    private long mLastBackPressedTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//检查升级
		UmengUpdateAgent.update(this);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);		
		setContentView(R.layout.activity_main);
		
		mTitleContainer=findViewById(R.id.rl_title);
		mIbMenuMore=(PopUpMenuButton) findViewById(R.id.ib_menu_more);
		mFeedFragment=(FeedFragment)getSupportFragmentManager().findFragmentById(R.id.feed_fragment);
		mTitleContainer.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                mFeedFragment.onTitleClicked();
                
            }
        });
		
		mIbMenuMore.setOnMenuListener(new OnMenuListener() {
		    
		    @Override
            public void onCreateMenu(PopupWindow popupWindow) {
		        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.main_menu, null);
                popupWindow.setContentView(view);
                popupWindow.setWidth(DisplayUtils.dp2px(mContext, 150));
                popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

                view.findViewById(R.id.tv_star).setOnClickListener(MainActivity.this);
                view.findViewById(R.id.tv_feedback).setOnClickListener(MainActivity.this);
                view.findViewById(R.id.tv_about).setOnClickListener(MainActivity.this);
            }
            
            @Override
            public void onPreShowMenu(PopupWindow popupWindow) {
            }
   
        });
	}
	
	@Override 
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share",
                RequestType.SOCIAL);
	    /**使用SSO授权必须添加如下代码 */
	    UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode) ;
	    if(ssoHandler != null){
	       ssoHandler.authorizeCallBack(requestCode, resultCode, data);
	    }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    mIbMenuMore.showMenu();
		return false;
	}
	
	@Override
    public void onBackPressed() {
	    if(System.currentTimeMillis()-mLastBackPressedTime>2000){
	        mLastBackPressedTime=System.currentTimeMillis();
	        Toast.makeText(mContext, R.string.one_more_exit, Toast.LENGTH_SHORT).show();
	    }else{
	        super.onBackPressed();
	    }
    }

    @Override
	public void onClick(View v) {
	    mIbMenuMore.hideMenu();
		
		//响应选中菜单项
		int id=v.getId();
		switch (id) {
		//我的收藏
        case R.id.tv_star: {
            Intent intent = new Intent(mContext, FavActivity.class);
            startActivity(intent);
            break;
        }
        //反馈
		case R.id.tv_feedback: {
            FeedbackAgent agent = new FeedbackAgent(mContext);
            agent.startFeedbackActivity();
            break;
        }
		//关于
		case R.id.tv_about: {
			Intent intent = new Intent(mContext, AboutActivity.class);
			startActivity(intent);
			break;
		}
		default:
			break;
		}
	}

}
