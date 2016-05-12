package com.framgia.photoalbum.effect;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.util.Random;

/**
 * Created by HungNT on 5/5/16.
 */
public class Noise extends EffectFilter {

    @Override
    public Bitmap applyEffect(Bitmap src) {
        final int COLOR_MAX = 0xFF;

        int width = src.getWidth();
        int height = src.getHeight();
        int[] pixels = new int[width * height];
        Random random = new Random();

        src.getPixels(pixels, 0, width, 0, 0, width, height);

        int index;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                index = y * width + x;
                // get random color
                int randColor = Color.rgb(random.nextInt(COLOR_MAX),
                        random.nextInt(COLOR_MAX), random.nextInt(COLOR_MAX));

                pixels[index] |= randColor;
            }
        }

        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
        bmOut.setPixels(pixels, 0, width, 0, 0, width, height);

        return bmOut;
    }
}
