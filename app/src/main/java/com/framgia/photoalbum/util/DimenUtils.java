package com.framgia.photoalbum.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Created by HungNT on 5/2/16.
 */
public class DimenUtils {

    public static float dpToPx(Context context, float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }
    public static DisplayMetrics getDisplayMetrics(Context context){
        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay()
                .getMetrics(metrics);
        return metrics;
    }

}
