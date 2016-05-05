package com.framgia.photoalbum.ui.custom;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;

/**
 * Created by dinhduc on 03/05/2016.
 */
public class HighLightDrawable extends BitmapDrawable {
    private Path mHighLightArea;
    private Rect mMaskArea;

    public HighLightDrawable(Resources resources, Bitmap bitmap, Point centerPoint, float radius) {
        super(resources, bitmap);
        mMaskArea = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        createHighlightArea(centerPoint, radius);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.clipPath(mHighLightArea, Region.Op.DIFFERENCE);
        Paint maskPaint = new Paint();
        maskPaint.setColor(0x80000000);
        canvas.drawRect(mMaskArea, maskPaint);

    }

    /**
     * create highlight area on the image
     * @param centerPoint center point of the highlight
     * @param radius radius of the highlight
     */
    public void createHighlightArea(Point centerPoint, float radius) {
        mHighLightArea = new Path();
        mHighLightArea.addCircle(centerPoint.x, centerPoint.y, radius, Path.Direction.CCW);
    }
}
