package com.framgia.photoalbum.effect;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

/**
 * Created by HungNT on 5/6/16.
 */
public class GrayScale extends EffectFilter {
    @Override
    public Bitmap applyEffect(Bitmap src) {

        final float GS_RED = 0.299f;
        final float GS_GREEN = 0.587f;
        final float GS_BLUE = 0.114f;

        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        GS_RED, GS_GREEN, GS_BLUE, 0, 0,
                        GS_RED, GS_GREEN, GS_BLUE, 0, 0,
                        GS_RED, GS_GREEN, GS_BLUE, 0, 0,
                        0, 0, 0, 1, 0
                });
        Bitmap res = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());

        Canvas canvas = new Canvas(res);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(src, 0, 0, paint);
        return res;
    }
}