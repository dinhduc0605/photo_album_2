package com.framgia.photoalbum.effect;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * Created by HungNT on 5/10/16.
 */
public class Orientation extends EffectFilter {
    public static final int FLIP_HORIZONTAL = 0;
    public static final int FLIP_VERTICAL = 1;

    @Override
    public Bitmap applyEffect(Bitmap src) {
        return rotate(src, 90);
    }

    public Bitmap rotate(Bitmap src, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);

        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }

    /**
     * @param src  bitmap input
     * @param type 2 type @FLIP_HORIZONTAL & @FLIP_VERTICAL
     * @return bitmap flipped
     */
    public Bitmap flip(Bitmap src, int type) {
        Matrix matrix = new Matrix();

        if (type == FLIP_HORIZONTAL) {
            matrix.postScale(-1, 1);
            matrix.postTranslate(src.getWidth(), 0);
        } else if (type == FLIP_VERTICAL) {
            matrix.postScale(1, -1);
            matrix.postTranslate(0, src.getHeight());
        }

        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }
}
