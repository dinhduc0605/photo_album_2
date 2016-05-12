package com.framgia.photoalbum.effect;

import android.graphics.Bitmap;

import com.framgia.photoalbum.util.ConvolutionMatrix;

/**
 * Created by HungNT on 5/12/16.
 */
public class Sharpen extends EffectFilter {

    @Override
    public Bitmap applyEffect(Bitmap src) {
        double[][] sharpConfig = new double[][]{
                {0, -1, 0},
                {-1, 5, -1},
                {0, -1, 0}
        };

        ConvolutionMatrix matrix = new ConvolutionMatrix(3);
        matrix.applyConfig(sharpConfig);

        return ConvolutionMatrix.computeConvolution3x3(src, matrix);
    }
}
