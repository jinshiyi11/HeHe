package com.shuai.base.view;

import com.shuai.hehe.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import uk.co.senab.photoview.PhotoView;

public class NetworkPhotoView extends PhotoView {
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
    }

    public NetworkPhotoView(Context context, AttributeSet attr, int defStyle) {
        super(context, attr, defStyle);
        init(context, attr);
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
