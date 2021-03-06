package com.framgia.photoalbum.effect;

import android.graphics.Bitmap;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;

import com.framgia.photoalbum.ScriptC_GammaFilter;
import com.framgia.photoalbum.ui.activity.EditActivity;

/**
 * Created by HungNT on 5/11/16.
 */
public class Gamma extends EffectFilter {
    private float mGamma;

    public void setValue(float gamma) {
        this.mGamma = gamma;
    }

    @Override
    public Bitmap applyEffect(Bitmap src) {
        Bitmap bmOut = Bitmap.createBitmap(src.getWidth(), src.getHeight(), src.getConfig());

        RenderScript rs = RenderScript.create(EditActivity.sContext);

        Allocation allocationIn = Allocation.createFromBitmap(rs, src);
        Allocation allocationOut = Allocation.createTyped(rs, allocationIn.getType());

        ScriptC_GammaFilter gammaFilter = new ScriptC_GammaFilter(rs);
        gammaFilter.set_gGamma(mGamma);
        gammaFilter.set_gIn(allocationIn);
        gammaFilter.set_gOut(allocationOut);
        gammaFilter.set_gScript(gammaFilter);
        gammaFilter.invoke_filter();

        allocationOut.copyTo(bmOut);

        // destroy
        rs.destroy();
        allocationIn.destroy();
        allocationOut.destroy();
        gammaFilter.destroy();

        return bmOut;
    }
}
