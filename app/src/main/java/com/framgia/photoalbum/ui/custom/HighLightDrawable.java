package com.framgia.photoalbum.ui.custom;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import com.framgia.photoalbum.BuildConfig;

/**
 * Created by dinhduc on 03/05/2016.
 */
public class HighLightDrawable extends BitmapDrawable {
    private static final String TAG = "HighLightDrawable";
    private Rect mMaskArea;
    private Point mCenterPoint;
    private float mRadius;

    public HighLightDrawable(Resources resources, Bitmap bitmap, Point centerPoint, float radius) {
        super(resources, bitmap);
        mMaskArea = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        setHighlightArea(centerPoint, radius);
        if (BuildConfig.DEBUG) {
            Log.w(TAG, "" + bitmap.getWidth() + "-" + bitmap.getHeight());
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        //draw gradient circle
        RadialGradient radialGradient = new RadialGradient(mCenterPoint.x, mCenterPoint.y, mRadius, 0x00000000, 0xE6000000, Shader.TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setDither(true);
        paint.setShader(radialGradient);
        canvas.drawCircle(mCenterPoint.x, mCenterPoint.y, mRadius, paint);

        //clip an circle area
        Path mHighLightArea = new Path();
        mHighLightArea.addCircle(mCenterPoint.x, mCenterPoint.y, mRadius, Path.Direction.CCW);
        canvas.clipPath(mHighLightArea, Region.Op.DIFFERENCE);

        //create a mask on image
        Paint maskPaint = new Paint();
        maskPaint.setColor(0xE6000000);
        canvas.drawRect(mMaskArea, maskPaint);

    }

    /**
     * create highlight area on the image
     *
     * @param centerPoint center point of the highlight
     * @param radius      radius of the highlight
     */
    public void setHighlightArea(Point centerPoint, float radius) {
        mCenterPoint = centerPoint;
        mRadius = radius;
    }
}
