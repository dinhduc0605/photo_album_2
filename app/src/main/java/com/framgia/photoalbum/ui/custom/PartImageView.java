package com.framgia.photoalbum.ui.custom;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.framgia.photoalbum.BuildConfig;
import com.framgia.photoalbum.util.CommonUtils;

/**
 * Created by dinhduc on 11/05/2016.
 */
public class PartImageView extends ImageView {
    private static final String TAG = "PartImageView";
    Bitmap mImageBitmap;
    Paint paint = new Paint();
    PointF firstPointDraw = new PointF(0, 0);
    PointF lastTouch;
    boolean scaled = false, isMove = false;


    public PartImageView(Context context) {
        super(context);
        if (BuildConfig.DEBUG) {
            Log.w(TAG, getWidth() + "-" + getHeight());
        }
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
        if (!scaled && mImageBitmap != null) {
            mImageBitmap = CommonUtils.matchBitmap(mImageBitmap, getWidth(), getHeight());
            canvas.drawBitmap(mImageBitmap, firstPointDraw.x, firstPointDraw.y, paint);
        }
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        mImageBitmap = bm;
        invalidate();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastTouch = new PointF(event.getX(), event.getY());
                isMove = false;
                Log.d(TAG, "onTouchEvent: down ");
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = event.getX() - lastTouch.x;
                float dy = event.getY() - lastTouch.y;
                firstPointDraw.x += dx;
                firstPointDraw.y += dy;
                lastTouch.set(event.getX(), event.getY());
                invalidate();
                if (dx > 2 || dy > 2) {
                    isMove = true;
                }
                Log.d(TAG, "onTouchEvent: move");
                break;
            case MotionEvent.ACTION_UP:
                if (isMove) {
                    return true;
                }
        }
        return super.onTouchEvent(event);

    }

}
