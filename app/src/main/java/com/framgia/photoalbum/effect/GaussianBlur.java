package com.framgia.photoalbum.effect;

import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;

import com.framgia.photoalbum.ui.activity.EditActivity;

/**
 * Created by HungNT on 5/12/16.
 */
public class GaussianBlur extends EffectFilter {

    private int mRadius = 20;

    public void setRadius(int radius) {
        this.mRadius = radius;
    }

    @Override
    public Bitmap applyEffect(Bitmap src) {
        Bitmap bitmap = Bitmap.createBitmap(
                src.getWidth(), src.getHeight(),
                Bitmap.Config.ARGB_8888);

        RenderScript rs = RenderScript.create(EditActivity.sContext);

        Allocation allocIn = Allocation.createFromBitmap(rs, src);
        Allocation allocOut = Allocation.createFromBitmap(rs, bitmap);

        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        blur.setRadius(mRadius);
        blur.setInput(allocIn);
        blur.forEach(allocOut);

        allocOut.copyTo(bitmap);
        rs.destroy();

        return bitmap;
    }
}
