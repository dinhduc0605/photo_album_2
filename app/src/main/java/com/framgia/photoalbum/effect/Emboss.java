package com.framgia.photoalbum.effect;

import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicConvolve3x3;

import com.framgia.photoalbum.ui.activity.EditActivity;

/**
 * Created by HungNT on 5/12/16.
 */
public class Emboss extends EffectFilter {
    @Override
    public Bitmap applyEffect(Bitmap src) {
        float[] config =
                {
                        -2.0f, -1.0f, 0.0f,
                        -1.0f, 1.0f, 1.0f,
                        0.0f, 1.0f, 2.0f
                };

        Bitmap bitmap = Bitmap.createBitmap(
                src.getWidth(), src.getHeight(),
                Bitmap.Config.ARGB_8888);

        RenderScript rs = RenderScript.create(EditActivity.sContext);

        Allocation allocIn = Allocation.createFromBitmap(rs, src);
        Allocation allocOut = Allocation.createFromBitmap(rs, bitmap);

        ScriptIntrinsicConvolve3x3 convolution
                = ScriptIntrinsicConvolve3x3.create(rs, Element.U8_4(rs));
        convolution.setInput(allocIn);
        convolution.setCoefficients(config);
        convolution.forEach(allocOut);

        allocOut.copyTo(bitmap);
        rs.destroy();

        return bitmap;
    }
}
