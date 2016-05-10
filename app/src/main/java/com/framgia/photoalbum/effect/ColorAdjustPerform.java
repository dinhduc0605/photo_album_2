package com.framgia.photoalbum.effect;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

/**
 * Created by HungNT on 5/8/16.
 */
public class ColorAdjustPerform extends EffectFilter {
    public static final int RED = 0;
    public static final int BLUE = 1;
    public static final int GREEN = 2;

    private int mGreenValue;
    private int mRedValue;
    private int mBlueValue;
    private int mType;

    @Override
    public Bitmap applyEffect(Bitmap src) {
        return applyEffect(src, RED);
    }

    public Bitmap applyEffect(Bitmap src, int value) {
        int w = src.getWidth();
        int h = src.getHeight();

        mGreenValue = mType == GREEN ? value : mGreenValue;
        mRedValue = mType == RED ? value : mRedValue;
        mBlueValue = mType == BLUE ? value : mBlueValue;

        ColorMatrix cm = new ColorMatrix(new float[]{
                1, 0, 0, 0, mRedValue,
                0, 1, 0, 0, mGreenValue,
                0, 0, 1, 0, mBlueValue,
                0, 0, 0, 1, 0
        });

        Bitmap res = Bitmap.createBitmap(w, h, src.getConfig());

        Canvas canvas = new Canvas(res);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(src, 0, 0, paint);
        return res;
    }

    public void setType(int type) {
        this.mType = type;
    }

    /**
     *
     * @param type one of @RED, @BLUE, @GREEN
     * @return the current value mType
     */
    public int getValue(int type){
        return type == RED ? mRedValue : type == GREEN ? mGreenValue : mBlueValue;
    }
}
