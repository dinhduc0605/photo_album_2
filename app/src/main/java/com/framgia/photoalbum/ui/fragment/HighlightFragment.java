package com.framgia.photoalbum.ui.fragment;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.framgia.photoalbum.BuildConfig;
import com.framgia.photoalbum.R;
import com.framgia.photoalbum.ui.activity.EditActivity;
import com.framgia.photoalbum.ui.custom.HighLightDrawable;
import com.framgia.photoalbum.util.CommonUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HighlightFragment extends EditFragment {
    private static final String TAG = "HighlightFragment";
    @Bind(R.id.imageHighlight)
    ImageView mImageHighlight;
    private HighLightDrawable mHighLightDrawable;
    Point centerPoint = new Point(100, 100);
    float mRadius = 100;
    private Bitmap mImageBitmap;
    private ScaleGestureDetector mScaleGestureDetector;
    private PointF lastTouch;
    int mActivePointerId;

    public HighlightFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mScaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleListener());
        View view = inflater.inflate(R.layout.fragment_highlight, container, false);
        ButterKnife.bind(this, view);
        mImageBitmap = EditActivity.imageBitmap;

        mImageHighlight.post(new Runnable() {
            @Override
            public void run() {
                Log.w(TAG, "" + mImageHighlight.getWidth());
                mImageBitmap = CommonUtils.scaleBitmap(mImageBitmap, mImageHighlight.getWidth(), mImageHighlight.getHeight());
                mImageHighlight.getLayoutParams().height = mImageBitmap.getHeight();
                mImageHighlight.getLayoutParams().width = mImageBitmap.getWidth();
                mImageHighlight.requestLayout();
                mHighLightDrawable = new HighLightDrawable(getResources(), mImageBitmap, centerPoint, mRadius);
                //set callback when drawable is invalidated
                mHighLightDrawable.setCallback(new Drawable.Callback() {
                    @Override
                    public void invalidateDrawable(Drawable drawable) {
                        mImageHighlight.setImageDrawable(drawable);
                    }

                    @Override
                    public void scheduleDrawable(Drawable drawable, Runnable runnable, long l) {

                    }

                    @Override
                    public void unscheduleDrawable(Drawable drawable, Runnable runnable) {

                    }
                });

                mImageHighlight.setImageDrawable(mHighLightDrawable);
            }
        });

        mImageHighlight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mScaleGestureDetector.onTouchEvent(motionEvent);

                switch (MotionEventCompat.getActionMasked(motionEvent)) {
                    case MotionEvent.ACTION_DOWN: {
                        //Last touch coordinate
                        lastTouch = new PointF();
                        int pointerIndex = MotionEventCompat.getActionIndex(motionEvent);
                        lastTouch.x = MotionEventCompat.getX(motionEvent, pointerIndex);
                        lastTouch.y = MotionEventCompat.getY(motionEvent, pointerIndex);
                        mActivePointerId = MotionEventCompat.getPointerId(motionEvent, 0);
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        int pointerIndex = MotionEventCompat.findPointerIndex(motionEvent, mActivePointerId);
                        // when highlight is scaling, it can't to be moved
                        if (!mScaleGestureDetector.isInProgress()) {
                            centerPoint.x += MotionEventCompat.getX(motionEvent, pointerIndex) - lastTouch.x;
                            centerPoint.y += MotionEventCompat.getY(motionEvent, pointerIndex) - lastTouch.y;
                            mHighLightDrawable.setHighlightArea(centerPoint, mRadius);
                            mHighLightDrawable.invalidateSelf();
                        }
                        lastTouch.x = MotionEventCompat.getX(motionEvent, pointerIndex);
                        lastTouch.y = MotionEventCompat.getY(motionEvent, pointerIndex);
                        break;
                    }
                    case MotionEvent.ACTION_POINTER_UP: {
                        int pointerIndex = MotionEventCompat.getActionIndex(motionEvent);
                        int pointerId = MotionEventCompat.getPointerId(motionEvent, pointerIndex);
                        if (pointerId == mActivePointerId) {
                            int newPointerIndex = (pointerIndex == 0) ? 1 : 0;
                            lastTouch.x = MotionEventCompat.getX(motionEvent, newPointerIndex);
                            lastTouch.y = MotionEventCompat.getY(motionEvent, newPointerIndex);
                            mActivePointerId = MotionEventCompat.getPointerId(motionEvent, newPointerIndex);
                        }
                    }

                }
                return true;
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void apply() {
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float mScaleFactor = detector.getScaleFactor();
            mRadius *= mScaleFactor;
            mRadius = Math.max(50f, Math.min(mRadius, 200f));
            mHighLightDrawable.setHighlightArea(centerPoint, mRadius);
            mHighLightDrawable.invalidateSelf();
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "onScale: " + mScaleFactor);
            }
            return true;
        }
    }
}
