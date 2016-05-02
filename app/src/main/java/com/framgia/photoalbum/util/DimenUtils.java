package com.framgia.photoalbum.util;

import android.content.Context;

/**
 * Created by HungNT on 5/2/16.
 */
public class DimenUtils {

    public static float dpToPx(Context context, float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

}
