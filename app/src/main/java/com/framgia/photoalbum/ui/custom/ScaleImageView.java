package com.framgia.photoalbum.ui.custom;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
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

/**
 * Created by dinhduc on 17/05/2016.
 */
public class ScaleImageView extends ImageView {
    private static final String TAG = "ScaleImageView";
    private static final int MAX_SCALE = 3;
    private static final int MIN_SCALE = 1;
    private float mScaleFactor = 1, mScaleFactorTotal = 1;
    private ScaleGestureDetector mScaleGestureDetector;
    private GestureDetector mScrollGestureListener;
    private Matrix mMatrix = new Matrix();

    public ScaleImageView(Context context) {
        super(context);
    }

    public ScaleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScaleGestureDetector = new ScaleGestureDetector(context, new ScaleImageDetector());
        mScrollGestureListener = new GestureDetector(context, new ScrollGestureListener());
    }

    public ScaleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScaleGestureDetector = new ScaleGestureDetector(context, new ScaleImageDetector());
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ScaleImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleGestureDetector.onTouchEvent(event);
        mScrollGestureListener.onTouchEvent(event);
        return true;
    }

    @Override
    public void setImageBitmap(final Bitmap bm) {
        if (bm != null) {
            post(new Runnable() {
                @Override
                public void run() {
                    RectF drawableRectF = new RectF(0, 0, bm.getWidth(), bm.getHeight());
                    RectF viewRectF = new RectF(0, 0, getWidth(), getHeight());
                    mMatrix.setRectToRect(drawableRectF, viewRectF, Matrix.ScaleToFit.CENTER);
                    setImageMatrix(mMatrix);
                }
            });
        }
        super.setImageBitmap(bm);

    }

    /**
     * Helper method that maps the supplied Matrix to the current Drawable
     *
     * @param matrix - Matrix to map Drawable against
     * @return RectF - Displayed Rectangle
     */
    private RectF getDisplayRect(Matrix matrix) {
        Drawable d = getDrawable();
        if (null != d) {
            RectF mDisplayRect = new RectF();
            mDisplayRect.set(0, 0, d.getIntrinsicWidth(),
                    d.getIntrinsicHeight());
            matrix.mapRect(mDisplayRect);
            return mDisplayRect;
        }
        return null;
    }

    private class ScaleImageDetector extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor = detector.getScaleFactor();
            if (BuildConfig.DEBUG) {
                Log.w(TAG, "" + mScaleFactorTotal);
            }
            if (mScaleFactorTotal * mScaleFactor < MIN_SCALE || mScaleFactorTotal * mScaleFactor > MAX_SCALE) {
                return true;
            }
            mScaleFactorTotal *= mScaleFactor;
            mMatrix.postScale(mScaleFactor, mScaleFactor, getWidth() / 2, getHeight() / 2);
            RectF rectF = getDisplayRect(mMatrix);
            if (mScaleFactor < 1) {
                float deltaX = (float) getWidth() / 2 - rectF.centerX();
                float deltaY = (float) getHeight() / 2 - rectF.centerY();
                mMatrix.postTranslate(deltaX / mScaleFactorTotal, deltaY / mScaleFactorTotal);
            }
            setImageMatrix(mMatrix);
            return true;
        }

    }

    private class ScrollGestureListener implements GestureDetector.OnGestureListener {

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
            if (!mScaleGestureDetector.isInProgress() && mScaleFactorTotal > 1.1) {
                mMatrix.postTranslate(-v, -v1);
                setImageMatrix(mMatrix);
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
