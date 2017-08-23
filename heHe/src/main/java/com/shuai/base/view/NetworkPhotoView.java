package com.shuai.base.view;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import uk.co.senab.photoview.PhotoView;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;

import com.shuai.hehe.R;

/**
 * 使用圆形进度条表示下载进度，支持gif图片
 */
public class NetworkPhotoView extends PhotoView {
    static final String ANDROID_NS = "http://schemas.android.com/apk/res/android";
    private int mLoadingProgress;
    private Paint mProgressHintPaint = new Paint();
    private Paint mProgressPaint = new Paint();
    private int mProgressColor = Color.WHITE;
    /**
     * 圆形进度条的半径
     */
    private float mProgressRadius=30;
    
    /**
     * 圆形进度条的圆圈线的宽度
     */
    private float mProgressCircleWidth=1;
    //private float mHintTextSize=15;

    public NetworkPhotoView(Context context) {
        super(context);
        init(context,null);
    }

    public NetworkPhotoView(Context context, AttributeSet attr) {
        super(context, attr);
        init(context, attr);
        trySetGifDrawable(attr, getResources());
    }

    public NetworkPhotoView(Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);
        init(context, attr);
        trySetGifDrawable(attr, getResources());
    }

    private void init(Context context, AttributeSet attr) {
        TypedArray a = context.obtainStyledAttributes(attr, R.styleable.NetworkPhotoView);
        mProgressRadius=a.getDimension(R.styleable.NetworkPhotoView_progressRadius, mProgressRadius);
        mProgressCircleWidth=a.getDimension(R.styleable.NetworkPhotoView_progressCircleWidth, mProgressCircleWidth);
        mProgressPaint.setColor(mProgressColor);
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setStyle(Style.FILL);
        
        mProgressHintPaint.setColor(mProgressColor);
        mProgressHintPaint.setAntiAlias(true);
        mProgressHintPaint.setStyle(Style.FILL);
        mProgressHintPaint.setTextSize(a.getDimension(R.styleable.NetworkPhotoView_hintTextSize, 15));
        
        a.recycle();
    }
    
    @Override
    public void setImageResource(int resId) {
        setResource(true, resId, getResources());
    }

    @Override
    public void setBackgroundResource(int resId) {
        setResource(false, resId, getResources());
    }

    void trySetGifDrawable(AttributeSet attrs, Resources res) {
        if (attrs != null && res != null && !isInEditMode()) {
            int resId = attrs.getAttributeResourceValue(ANDROID_NS, "src", -1);
            if (resId > 0 && "drawable".equals(res.getResourceTypeName(resId)))
                setResource(true, resId, res);

            resId = attrs.getAttributeResourceValue(ANDROID_NS, "background", -1);
            if (resId > 0 && "drawable".equals(res.getResourceTypeName(resId)))
                setResource(false, resId, res);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @SuppressWarnings("deprecation")
        //new method not available on older API levels
    void setResource(boolean isSrc, int resId, Resources res) {
        try {
            GifDrawable d = new GifDrawable(res, resId);
            if (isSrc)
                setImageDrawable(d);
            else if (Build.VERSION.SDK_INT >= 16)
                setBackground(d);
            else
                setBackgroundDrawable(d);
            return;
        } catch (IOException ignored) {
            //ignored
        }catch (NotFoundException ignored) {
            //ignored
        }
        if (isSrc)
            super.setImageResource(resId);
        else
            super.setBackgroundResource(resId);
    }

    /**
     * Sets the content of this GifImageView to the specified Uri.
     * If uri destination is not a GIF then {@link android.widget.ImageView#setImageURI(android.net.Uri)}
     * is called as fallback.
     * For supported URI schemes see: {@link android.content.ContentResolver#openAssetFileDescriptor(android.net.Uri, String)}.
     *
     * @param uri The Uri of an image
     */
    @Override
    public void setImageURI(Uri uri) {
        if (uri != null)
            try {
                setImageDrawable(new GifDrawable(getContext().getContentResolver(), uri));
                return;
            } catch (IOException ignored) {
                //ignored
            }
        super.setImageURI(uri);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mLoadingProgress >= 0 && mLoadingProgress < 100) {
            float sweepAngle = (float) (mLoadingProgress / 100.0 * 360);
            
            float centerX=getWidth()/2;
            float centerY=getHeight()/2;
            RectF rect=new RectF(centerX-mProgressRadius, centerY-mProgressRadius, centerX+mProgressRadius, centerY+mProgressRadius);
            mProgressPaint.setStyle(Style.STROKE);
            mProgressPaint.setStrokeWidth(mProgressCircleWidth);
            canvas.drawCircle(centerX, centerY, mProgressRadius, mProgressPaint);
            mProgressPaint.setStyle(Style.FILL);
            canvas.drawArc(rect, -90, sweepAngle, true, mProgressPaint);
        }
    }
    
    /**
     * 设置圆形进度条的半径
     * @param progress
     */
    public void setProgressRadius(float progressRadius){
        mProgressRadius=progressRadius;
        invalidate();
    }
    
    /**
     * 设置圆形进度条的圆圈线的宽度
     */
    public void setProgressCircleWidth(float width){
        mProgressCircleWidth=width;
    }

    public void setProgress(int progress) {
        mLoadingProgress = progress;
        invalidate();
    }
    
    public void onLoadingComplete() {
        setProgress(100);
    }
    
    public void onLoadingFailed() {
        setProgress(100);
    }

}
