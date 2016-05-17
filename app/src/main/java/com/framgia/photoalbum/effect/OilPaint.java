package com.framgia.photoalbum.effect;

import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicConvolve3x3;

import com.framgia.photoalbum.ScriptC_GammaFilter;
import com.framgia.photoalbum.ScriptC_OilPaintFilter;
import com.framgia.photoalbum.ui.activity.EditActivity;

/**
 * Created by HungNT on 5/12/16.
 */
public class OilPaint extends EffectFilter {

    @Override
    public Bitmap applyEffect(Bitmap src) {
        Bitmap bmOut = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());

        RenderScript rs = RenderScript.create(EditActivity.sContext);

        Allocation allocationIn = Allocation.createFromBitmap(rs, src);
        Allocation allocationOut = Allocation.createTyped(rs, allocationIn.getType());

        ScriptC_OilPaintFilter filter = new ScriptC_OilPaintFilter(rs);
        filter.set_gIn(allocationIn);
        filter.set_gOut(allocationOut);
        filter.set_gScript(filter);
        filter.invoke_filter();

        allocationOut.copyTo(bmOut);

        // destroy
        rs.destroy();
        allocationIn.destroy();
        allocationOut.destroy();
        filter.destroy();

        return bmOut;
    }
}
