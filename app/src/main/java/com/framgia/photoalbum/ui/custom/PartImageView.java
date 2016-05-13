package com.framgia.photoalbum.ui.custom;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

import com.framgia.photoalbum.BuildConfig;
import com.framgia.photoalbum.util.CommonUtils;

/**
 * Created by dinhduc on 11/05/2016.
 */
public class PartImageView extends ImageView {
    private static final String TAG = "PartImageView";
    private Bitmap mImageBitmap;
    private Paint mPaint = new Paint();
    private PointF mFirstTouch;
    private boolean mScaled = false;
    private Matrix mDrawMatrix = new Matrix();
    private ScaleGestureDetector mScaleImageDetector;
    private GestureDetector mScrollImageDetector;


    public PartImageView(Context context) {
        super(context);
        if (BuildConfig.DEBUG) {
            Log.w(TAG, getWidth() + "-" + getHeight());
        }
        mScaleImageDetector = new ScaleGestureDetector(context, new ScaleImageDetector());
        mScrollImageDetector = new GestureDetector(context, new ScrollImageDetector());
    }

    public PartImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (BuildConfig.DEBUG) {
            Log.w(TAG, getWidth() + "-" + getHeight());
        }
    }

    public PartImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (BuildConfig.DEBUG) {
            Log.w(TAG, getWidth() + "-" + getHeight());
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PartImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //when bitmap haven't be mScaled and not null
        if (!mScaled && mImageBitmap != null) {
            mDrawMatrix.postTranslate(0, 0);
            mImageBitmap = CommonUtils.matchBitmap(mImageBitmap, getWidth(), getHeight());
            canvas.drawBitmap(mImageBitmap, mDrawMatrix, mPaint);
        }
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        mImageBitmap = bm;
        invalidate();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleImageDetector.onTouchEvent(event);
        mScrollImageDetector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mFirstTouch = new PointF(event.getX(), event.getY());
                break;
            case MotionEvent.ACTION_UP:
                //if moved distance > 3px then image is moving -> not trigger onclick
                if (Math.abs(event.getX() - mFirstTouch.x) > 3 || Math.abs(event.getY() - mFirstTouch.y) > 3) {
                    return true;
                }
        }
        return super.onTouchEvent(event);

    }

    private class ScaleImageDetector extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mDrawMatrix.postScale(detector.getScaleFactor(), detector.getScaleFactor());
            invalidate();
            return true;
        }
    }

    private class ScrollImageDetector implements GestureDetector.OnGestureListener {

        @Override
        public boolean onDown(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            if (!mScaleImageDetector.isInProgress()) {
                mDrawMatrix.postTranslate(-v, -v1);
                invalidate();
            }
            return true;
        }

        @Override
        public void onLongPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return false;
        }
    }

}
