package com.framgia.photoalbum.effect;

import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;

import com.framgia.photoalbum.ScriptC_NegativeFilter;
import com.framgia.photoalbum.ui.activity.EditActivity;

/**
 * Created by HungNT on 5/5/16.
 */
public class Negative extends EffectFilter {
    @Override
    public Bitmap applyEffect(Bitmap src) {

        Bitmap res = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());

        RenderScript rs = RenderScript.create(EditActivity.sContext);

        Allocation allocationIn = Allocation.createFromBitmap(rs, src);
        Allocation allocationOut = Allocation.createTyped(rs, allocationIn.getType());

        ScriptC_NegativeFilter grayScale = new ScriptC_NegativeFilter(rs);
        grayScale.forEach_invert(allocationIn, allocationOut);

        allocationOut.copyTo(res);

        allocationIn.destroy();
        allocationOut.destroy();
        rs.destroy();
        grayScale.destroy();

        return res;
    }
}
