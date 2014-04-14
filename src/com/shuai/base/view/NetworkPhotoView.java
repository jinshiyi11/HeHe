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
    private float mRadius=30;
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
        mRadius=a.getDimension(R.styleable.NetworkPhotoView_progressRadius, mRadius);
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
        if (mLoadingProgress > 0 && mLoadingProgress < 100) {
            float sweepAngle = (float) (mLoadingProgress / 100.0 * 360);
            
            float centerX=getWidth()/2;
            float centerY=getHeight()/2;
            RectF rect=new RectF(centerX-mRadius, centerY-mRadius, centerX+mRadius, centerY+mRadius);
            mProgressPaint.setStyle(Style.STROKE);
            canvas.drawCircle(centerX, centerY, mRadius, mProgressPaint);
            mProgressPaint.setStyle(Style.FILL);
            canvas.drawArc(rect, -90, sweepAngle, true, mProgressPaint);
        }
    }

    public void setProgress(int progress) {
        mLoadingProgress = progress;
        invalidate();
    }

}
