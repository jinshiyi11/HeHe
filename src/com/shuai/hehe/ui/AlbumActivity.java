package com.shuai.hehe.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import android.R.integer;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.shuai.base.view.BaseActivity;
import com.shuai.base.view.ExpandableTextView;
import com.shuai.base.view.PopUpMenuButton;
import com.shuai.base.view.PopUpMenuButton.OnMenuListener;
import com.shuai.hehe.HeHeApplication;
import com.shuai.hehe.R;
import com.shuai.hehe.adapter.AlbumAdapter;
import com.shuai.hehe.data.AlbumFeed;
import com.shuai.hehe.data.Constants;
import com.shuai.hehe.data.DataManager;
import com.shuai.hehe.data.PicInfo;
import com.shuai.hehe.protocol.GetAlbumPicsRequest;
import com.shuai.hehe.protocol.ProtocolError;
import com.shuai.utils.AnimUtils;
import com.shuai.utils.DisplayUtils;
import com.shuai.utils.SocialUtils;
import com.shuai.utils.StorageUtils;
import com.shuai.utils.WallpaperUtils;
import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.sso.UMSsoHandler;

public class AlbumActivity extends BaseActivity {
    private DataManager mDataManager;
    private ViewGroup mNoNetworkContainer;
    private ViewGroup mLoadingContainer;
    private ViewGroup mMainContainer;
    enum Status {
        /**
         * 正在加载并且还没有任何数据
         */
        STATUS_LOADING,
        /**
         * 有数据
         */
        STATUS_GOT_DATA,
        /**
         * 加载失败并且没有任何数据
         */
        STATUS_NO_NETWORK_OR_DATA
    }
    
    private Status mStatus;
    /**
     * 当前显示的是第几张图片(如：2/9)
     */
    private TextView mTvPageNum;
    private RelativeLayout mRlPageNum;
    private PopUpMenuButton mIbMenuMore;
    private ViewPager mViewPager;
    
    /**
     * 图片描述
     */
    private ExpandableTextView mEtvDesc;
    
    /**
     * 是否显示当前是第几张图片和图片描述(可通过单击ViewPager显示和隐藏该信息)
     */
    private boolean mShowPicInfo=true;
    private AlbumAdapter mAlbumAdapter;
    private AlbumFeed mFeed;
    private ArrayList<PicInfo> mPicInfos;
    
    /**
     * 异步请求队列
     */
    private RequestQueue mRequestQueue;
    
    /**
     * 响应菜单点击
     */
    private MenuItemClickListener mMenuItemClickListener=new MenuItemClickListener();
    
    /**
     * 响应菜单类
     */
    private class MenuItemClickListener implements OnClickListener{

        @Override
        public void onClick(View v) {
            mIbMenuMore.hideMenu();
            
            switch (v.getId()) {
            case R.id.tv_star:
            {
                //收藏或取消收藏
                boolean isStarred=mDataManager.isStarFeed(mFeed.getId());
                if(isStarred){
                    mDataManager.removeStarFeed(mFeed.getId());
                    Toast.makeText(mContext, R.string.remove_star_tip, Toast.LENGTH_SHORT).show();
                }else{
                    mDataManager.addStarFeed(mFeed);
                    Toast.makeText(mContext, R.string.add_star_tip, Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case R.id.tv_share:
            {
                //分享
                PicInfo info = mPicInfos.get(mViewPager.getCurrentItem());
                SocialUtils.sharePic((Activity) mContext, mFeed.getTitle(),info.getPicDescription(), info.getBigPicUrl());
                break;
            }
            case R.id.tv_set_as_wallpaper:
            {
                //设为壁纸
                PicInfo info = mPicInfos.get(mViewPager.getCurrentItem());
                ImageLoader.getInstance().loadImage(info.getBigPicUrl(), new ImageLoadingListener() {

                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        Toast.makeText(mContext, R.string.set_wallpaper_failed, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        try {
                            WallpaperUtils.setWallper(mContext, loadedImage);
                            Toast.makeText(mContext, R.string.set_wallpaper_success, Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            //TODO:log it
                            e.printStackTrace();
                            Toast.makeText(mContext, R.string.set_wallpaper_failed, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                    }
                    
                });
                break;
            }
            case R.id.tv_download_pic:
            {
                //下载图片
                PicInfo info = mPicInfos.get(mViewPager.getCurrentItem());
                ImageLoader.getInstance().loadImage(info.getBigPicUrl(), new ImageLoadingListener() {
                    
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                    }
                    
                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        Toast.makeText(mContext, getString(R.string.pic_save_failed, failReason.getType().toString()), Toast.LENGTH_SHORT).show();
                    }
                    
                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        File imageFile = ImageLoader.getInstance().getDiscCache().get(imageUri);
                        
                        File saveFile=new File(StorageUtils.getMyPicturesDirectory(),mFeed.getType()+"_"+Uri.parse(imageUri).getLastPathSegment());
                        try {
                            StorageUtils.copyFile(imageFile, saveFile);
                            
                            Toast.makeText(mContext, getString(R.string.pic_save_success, saveFile.getAbsolutePath()), Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            Toast.makeText(mContext, getString(R.string.pic_save_failed, ""), Toast.LENGTH_SHORT).show();
                        }

                        // Tell the media scanner about the new file so that it is
                        // immediately available to the user.
                        MediaScannerConnection.scanFile(mContext, new String[] { saveFile.toString() }, null, null);
                    }
                    
                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                    }
                });

                break;
            }
//            case R.id.tv_download_album:
//            {
//                //下载相册
//                break;
//            }
            
            case R.id.tv_to_first_pic:
            {
                mViewPager.setCurrentItem(0);
                break;
            }
            case R.id.tv_to_last_pic:
            {
                mViewPager.setCurrentItem(mAlbumAdapter.getCount()-1);
                break;
            }
            default:
                break;
            }
        }
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mDataManager=DataManager.getInstance();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        mRequestQueue=HeHeApplication.getRequestQueue();
        
        mNoNetworkContainer=(ViewGroup) findViewById(R.id.no_network_container);
        mLoadingContainer=(ViewGroup) findViewById(R.id.loading_container);
        mMainContainer=(ViewGroup) findViewById(R.id.main_container);
        mRlPageNum=(RelativeLayout) findViewById(R.id.ll_pagenum);
        mTvPageNum=(TextView) findViewById(R.id.tv_pagenum);
        mTvPageNum.setVisibility(View.INVISIBLE);
        mIbMenuMore=(PopUpMenuButton) findViewById(R.id.ib_menu_more);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mEtvDesc=(ExpandableTextView) findViewById(R.id.etv_desc);
        mEtvDesc.setVisibility(View.INVISIBLE);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setOnPageChangeListener(new OnPageChangeListener(){

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                AlbumActivity.this.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                
            }
            
        });
        
        mViewPager.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                //单击显示或隐藏当前是第几张图片和图片描述
                mShowPicInfo=!mShowPicInfo;
                if(mShowPicInfo){
                    Animation fromTopAnimation = AnimUtils.slideAnimation(mContext, AnimUtils.SlideType.SLIDE_IN_FROM_TOP,mRlPageNum);
                    mRlPageNum.startAnimation(fromTopAnimation);
                    
                    if (!mEtvDesc.isEmpty()) {
                        Animation fromBottomAnimation = AnimUtils.slideAnimation(mContext, AnimUtils.SlideType.SLIDE_IN_FROM_BOTTOM,mEtvDesc);
                        mEtvDesc.startAnimation(fromBottomAnimation);
                    }
                }else{
                    Animation fromTopAnimation = AnimUtils.slideAnimation(mContext, AnimUtils.SlideType.SLIDE_OUT_FROM_TOP,mRlPageNum);
                    mRlPageNum.startAnimation(fromTopAnimation);
                    
                    if (!mEtvDesc.isEmpty()) {
                        Animation fromBottomAnimation = AnimUtils.slideAnimation(mContext, AnimUtils.SlideType.SLIDE_OUT_FROM_BOTTOM,mEtvDesc);
                        mEtvDesc.startAnimation(fromBottomAnimation);
                    }
                }
            }
        });
        
        mNoNetworkContainer.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                getData();
            }
        });
        
        mIbMenuMore.setOnMenuListener(new OnMenuListener() {

            @Override
            public void onCreateMenu(PopupWindow popupWindow) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.album_menu, null);
                popupWindow.setContentView(view);
                popupWindow.setWidth(DisplayUtils.dp2px(mContext, 150));
                popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

                view.findViewById(R.id.tv_star).setOnClickListener(mMenuItemClickListener);
                view.findViewById(R.id.tv_share).setOnClickListener(mMenuItemClickListener);
                view.findViewById(R.id.tv_set_as_wallpaper).setOnClickListener(mMenuItemClickListener);
                view.findViewById(R.id.tv_download_pic).setOnClickListener(mMenuItemClickListener);
                //view.findViewById(R.id.tv_download_album).setOnClickListener(mMenuItemClickListener);
                view.findViewById(R.id.tv_to_first_pic).setOnClickListener(mMenuItemClickListener);
                view.findViewById(R.id.tv_to_last_pic).setOnClickListener(mMenuItemClickListener);
                
                if(mAlbumAdapter.getCount()<=5){
                    view.findViewById(R.id.to_first_pic_split).setVisibility(View.GONE);
                    view.findViewById(R.id.tv_to_first_pic).setVisibility(View.GONE);
                    view.findViewById(R.id.to_last_pic_split).setVisibility(View.GONE);
                    view.findViewById(R.id.tv_to_last_pic).setVisibility(View.GONE);
                }
            }

            @Override
            public void onPreShowMenu(PopupWindow popupWindow) {
                View view=popupWindow.getContentView();
                TextView tvStar=(TextView) view.findViewById(R.id.tv_star);
                if(mDataManager.isStarFeed(mFeed.getId())){
                    tvStar.setText(R.string.remove_star);
                }else{
                    tvStar.setText(R.string.add_star);
                }
            }

        });
        Intent intent=getIntent();
        mFeed=intent.getParcelableExtra(Constants.FEED_ALBUM);
        
        getData();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mIbMenuMore.showMenu();
        return false;
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
    
    private void setStatus(Status status) {
        mStatus = status;
        switch (status) {
        case STATUS_LOADING:
            mLoadingContainer.setVisibility(View.VISIBLE);
            mMainContainer.setVisibility(View.GONE);
            mNoNetworkContainer.setVisibility(View.GONE);
            break;
        case STATUS_GOT_DATA:
            mLoadingContainer.setVisibility(View.GONE);
            mMainContainer.setVisibility(View.VISIBLE);
            mNoNetworkContainer.setVisibility(View.GONE);
            break;
        case STATUS_NO_NETWORK_OR_DATA:
            mLoadingContainer.setVisibility(View.GONE);
            mMainContainer.setVisibility(View.GONE);
            mNoNetworkContainer.setVisibility(View.VISIBLE);
            break;
        default:
            break;
        }

    }
    
    private void getData(){
        if(mAlbumAdapter==null || mAlbumAdapter.getCount()==0){
            setStatus(Status.STATUS_LOADING);
        }
        
        GetAlbumPicsRequest request=new GetAlbumPicsRequest(mContext,mFeed.getId(), new Listener<ArrayList<PicInfo>>() {

            @Override
            public void onResponse(ArrayList<PicInfo> response) {
                mPicInfos = response;
                if (mPicInfos.size() > 0) {
                    setStatus(Status.STATUS_GOT_DATA);
                    mTvPageNum.setVisibility(View.VISIBLE);
                    //mEtvDesc.setVisibility(View.VISIBLE);
                    mAlbumAdapter = new AlbumAdapter(mContext, mPicInfos);
                    mViewPager.setAdapter(mAlbumAdapter);
                    
                    int picPosition=mDataManager.getLastAlbumPicPosition(mFeed.getId());
                    if(picPosition<0)
                        picPosition=0;
                    else if(picPosition>=mAlbumAdapter.getCount())
                        picPosition=mAlbumAdapter.getCount()-1;
                    
                    mViewPager.setCurrentItem(picPosition, false);
                    //TODO:如果picPosition为0，onPageSelected不会被触发，所以这里强制触发一次
                    onPageSelected(picPosition);
                }else{
                    setStatus(Status.STATUS_NO_NETWORK_OR_DATA);
                    Toast.makeText(mContext, R.string.error_data, Toast.LENGTH_SHORT).show();
                }
            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                setStatus(Status.STATUS_NO_NETWORK_OR_DATA);
                Toast.makeText(mContext, ProtocolError.getErrorMessage(mContext, error), Toast.LENGTH_SHORT).show();
            }
        });
        
        request.setTag(this);       
        mRequestQueue.add(request);
    }

    @Override
    protected void onDestroy() {
        mRequestQueue.cancelAll(this);
        super.onDestroy();
    }
    
    private void onPageSelected(int position) {
        if (mAlbumAdapter != null && mAlbumAdapter.getCount() > 0) {
            mDataManager.setLastAlbumPicPosition(mFeed.getId(), position);
            mTvPageNum.setText(String.format("%d/%d", position + 1, mAlbumAdapter.getCount()));

            PicInfo info = mPicInfos.get(position);
            String desc = info.getPicDescription();
            mEtvDesc.setText(Html.fromHtml(desc));
            if (!TextUtils.isEmpty(desc) && mShowPicInfo) {
                mEtvDesc.setVisibility(View.VISIBLE);
            } else {
                mEtvDesc.setVisibility(View.INVISIBLE);
            }
        }
    }

}
