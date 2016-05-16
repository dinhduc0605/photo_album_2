package com.framgia.photoalbum.effect;

import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;

import com.framgia.photoalbum.ScriptC_NoiseFilter;
import com.framgia.photoalbum.ui.activity.EditActivity;

/**
 * Created by HungNT on 5/5/16.
 */
public class Noise extends EffectFilter {

    @Override
    public Bitmap applyEffect(Bitmap src) {

        Bitmap res = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());

        RenderScript rs = RenderScript.create(EditActivity.sContext);

        Allocation allocationIn = Allocation.createFromBitmap(rs, src);
        Allocation allocationOut = Allocation.createTyped(rs, allocationIn.getType());

        ScriptC_NoiseFilter script = new ScriptC_NoiseFilter(rs);

        script.set_gIn(allocationIn);
        script.set_gOut(allocationOut);
        script.set_gScript(script);

        script.invoke_filter();

        allocationOut.copyTo(res);

        return res;
    }
}
