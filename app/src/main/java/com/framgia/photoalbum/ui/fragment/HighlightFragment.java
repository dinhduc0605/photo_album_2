package com.framgia.photoalbum.ui.fragment;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.framgia.photoalbum.R;
import com.framgia.photoalbum.ui.activity.EditActivity;
import com.framgia.photoalbum.ui.custom.HighLightDrawable;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HighlightFragment extends EditFragment {
    @Bind(R.id.imageHighlight)
    ImageView mImageHighlight;

    private HighLightDrawable mHighLightDrawable;
    Point centerPoint = new Point(100, 100);
    int mRadius = 100;

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
        View view = inflater.inflate(R.layout.fragment_highlight, container, false);
        ButterKnife.bind(this, view);
        Bitmap mImageBitmap = EditActivity.imageBitmap;
        mImageHighlight.setImageBitmap(mImageBitmap);

        mHighLightDrawable = new HighLightDrawable(getResources(), mImageBitmap, centerPoint, mRadius);
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

        //Last touch coordinate
        final Point lastTouch = new Point();
        mImageHighlight.setImageDrawable(mHighLightDrawable);
        mImageHighlight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastTouch.set((int) motionEvent.getX(), (int) motionEvent.getY());
                        break;
                    case MotionEvent.ACTION_MOVE:
                        centerPoint.x += (int) motionEvent.getX() - lastTouch.x;
                        centerPoint.y += (int) motionEvent.getY() - lastTouch.y;
                        mHighLightDrawable.createHighlightArea(centerPoint, 100);
                        mHighLightDrawable.invalidateSelf();
                        lastTouch.set((int) motionEvent.getX(), (int) motionEvent.getY());
                        break;
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
}
