package com.framgia.photoalbum.effect;

import android.graphics.Bitmap;

import com.framgia.photoalbum.util.ConvolutionMatrix;

/**
 * Created by HungNT on 5/12/16.
 */
public class Emboss extends EffectFilter {
    @Override
    public Bitmap applyEffect(Bitmap src) {
        double[][] config = new double[][]{
                {-2, -1, 0},
                {-1, 1, 1},
                {0, 1, 2}
        };

        ConvolutionMatrix matrix = new ConvolutionMatrix(3);
        matrix.applyConfig(config);

        return ConvolutionMatrix.computeConvolution3x3(src, matrix);
    }
}
