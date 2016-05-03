package com.framgia.photoalbum.ui.custom;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;

/**
 * Created by dinhduc on 03/05/2016.
 */
public class HighLightDrawable extends BitmapDrawable {
    private Rect mHighLightArea;
    private Rect mMaskArea;

    public HighLightDrawable(Resources resources, Bitmap bitmap, Point centerPoint, int radius) {
        super(resources, bitmap);
        mMaskArea = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        createHighlightArea(centerPoint, radius);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.clipRect(mHighLightArea, Region.Op.DIFFERENCE);
        Paint maskPaint = new Paint();
        maskPaint.setColor(0x80000000);
        canvas.drawRect(mMaskArea, maskPaint);

    }

    public void createHighlightArea(Point centerPoint, int radius) {
        int left = centerPoint.x - radius;
        int top = centerPoint.y - radius;
        int right = centerPoint.x + radius;
        int bottom = centerPoint.y + radius;
        mHighLightArea = new Rect(left, top, right, bottom);
    }
}
