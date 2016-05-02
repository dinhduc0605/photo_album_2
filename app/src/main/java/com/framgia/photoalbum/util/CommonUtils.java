package com.framgia.photoalbum.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.media.ExifInterface;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.IOException;
import java.util.List;

/**
 * Created by HungNT on 4/27/16.
 */
public class CommonUtils {

    private static final String TAG = "CommonUtils";

    /**
     * Check whether camera not available
     * @param ctx
     * @param intent
     * @return
     */
    public static boolean isAvailable(Context ctx, Intent intent) {
        final PackageManager mgr = ctx.getPackageManager();
        List<ResolveInfo> list = mgr.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    /**
     * get screen size
     * @param activity
     * @return screen size
     */
    public static Point getDisplaySize(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return new Point(metrics.widthPixels, metrics.heightPixels);
    }

    /**
     * get inSampleSize to match bitmap to image
     * @param width
     * @param height
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(int width, int height, int reqWidth, int reqHeight) {
        int inSampleSize = 1;
        if (reqHeight < height || reqWidth < width) {
            final int haftHeight = height / 2;
            final int haftWidth = width / 2;
            while ((haftHeight / inSampleSize) >= reqHeight && (haftWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    /**
     * decode image file with calculated inSampleSize
     * @param path
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Bitmap decodeSampledBitmapResource(String path, int reqWidth, int reqHeight) {
        Bitmap photoBitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    options.inSampleSize = calculateInSampleSize(options.outHeight, options.outWidth, reqWidth, reqHeight);
                    options.inJustDecodeBounds = false;
                    photoBitmap = BitmapFactory.decodeFile(path, options);
                    photoBitmap = CommonUtils.rotateImage(photoBitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    options.inSampleSize = calculateInSampleSize(options.outWidth, options.outHeight, reqWidth, reqHeight);
                    options.inJustDecodeBounds = false;
                    photoBitmap = BitmapFactory.decodeFile(path, options);
                    photoBitmap = CommonUtils.rotateImage(photoBitmap, 180);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return photoBitmap;
    }

    /**
     * rotate image depend on image's orientation
     * @param source
     * @param angle
     * @return
     */
    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
}
