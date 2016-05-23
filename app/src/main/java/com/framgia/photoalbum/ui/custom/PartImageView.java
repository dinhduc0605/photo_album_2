package com.framgia.photoalbum.ui.custom;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

import com.framgia.photoalbum.BuildConfig;
import com.framgia.photoalbum.util.CommonUtils;

import static com.framgia.photoalbum.util.CommonUtils.getDisplayRect;
import static com.framgia.photoalbum.util.CommonUtils.getFixTrans;

/**
 * Created by dinhduc on 11/05/2016.
 */
public class PartImageView extends ImageView {
    private static final String TAG = "PartImageView";
    public static final float MAX_SCALE = 3;
    public static final float MIN_SCALE = 0.5f;
    private PointF mFirstTouch;
    private Matrix mDrawMatrix = new Matrix();
    private ScaleGestureDetector mScaleImageDetector;
    private GestureDetector mScrollImageDetector;
    private float mScaleFactorTotal = 1;


    public PartImageView(Context context) {
        super(context);
        mScaleImageDetector = new ScaleGestureDetector(context, new ScaleImageDetector());
        mScrollImageDetector = new GestureDetector(context, new ScrollImageDetector());
    }

    public PartImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PartImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PartImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        Log.d(TAG, "setImageBitmap: ");
//        mImageBitmap = bm;
        if (bm != null) {
            bm = CommonUtils.matchBitmap(bm, getWidth(), getHeight());
            setImageMatrix(mDrawMatrix);
        }
        super.setImageBitmap(bm);
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

    /**
     * fix image with returned fix translation coordinate
     */
    private void fixTrans(Matrix matrix, Drawable drawable) {
        float[] m = new float[9];
        matrix.getValues(m);
        float transX = m[Matrix.MTRANS_X];
        float transY = m[Matrix.MTRANS_Y];
        RectF rectF = getDisplayRect(matrix, drawable);
        float fixTransX = getFixTrans(transX, getWidth(), rectF.width());
        float fixTransY = getFixTrans(transY, getHeight(), rectF.height());

        if (fixTransX != 0 || fixTransY != 0) {
            mDrawMatrix.postTranslate(fixTransX, fixTransY);
        }
    }

    private class ScaleImageDetector extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float mScaleFactor = detector.getScaleFactor();
            if (mScaleFactorTotal * mScaleFactor < MIN_SCALE || mScaleFactorTotal * mScaleFactor > MAX_SCALE) {
                return false;
            }
            mScaleFactorTotal *= mScaleFactor;
            mDrawMatrix.postScale(mScaleFactor, mScaleFactor, getWidth() / 2, getHeight() / 2);
            setImageMatrix(mDrawMatrix);
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
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float translateX, float translateY) {
            if (!mScaleImageDetector.isInProgress()) {
                mDrawMatrix.postTranslate(-translateX, -translateY);
                fixTrans(mDrawMatrix, getDrawable());
                setImageMatrix(mDrawMatrix);
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
