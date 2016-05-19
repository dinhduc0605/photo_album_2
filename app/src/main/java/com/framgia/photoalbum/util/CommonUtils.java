package com.framgia.photoalbum.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.framgia.photoalbum.BuildConfig;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by HungNT on 4/27/16.
 */
public class CommonUtils {

    private static final String TAG = "CommonUtils";

    /**
     * Check whether camera not available
     *
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
     *
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
     *
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
            while ((haftHeight / inSampleSize) > reqHeight || (haftWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    /**
     * decode image file with calculated inSampleSize
     *
     * @param path
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Bitmap decodeSampledBitmapResource(String path, int reqWidth, int reqHeight) {
        float angle = 0;
        if (path == null) return null;
        Log.d(TAG, path);
        Bitmap photoBitmap = null;
        Bitmap rotatedBitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            if (BuildConfig.DEBUG) {
                Log.w(TAG, orientation+"");
            }
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    options.inSampleSize = calculateInSampleSize(options.outHeight, options.outWidth, reqWidth, reqHeight);
                    angle = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    options.inSampleSize = calculateInSampleSize(options.outWidth, options.outHeight, reqWidth, reqHeight);
                    angle = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    options.inSampleSize = calculateInSampleSize(options.outHeight, options.outWidth, reqWidth, reqHeight);
                    angle = 270;
                    break;
                default:
                    options.inSampleSize = calculateInSampleSize(options.outWidth, options.outHeight, reqWidth, reqHeight);
                    angle = 0;
                    break;
            }
            photoBitmap = BitmapFactory.decodeFile(path, options);
            rotatedBitmap = CommonUtils.rotateImage(photoBitmap, angle);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (BuildConfig.DEBUG) {
            Log.w(TAG, "" + options.inSampleSize);
            Log.w(TAG, rotatedBitmap.getWidth() + "-" + rotatedBitmap.getHeight());
        }
        return rotatedBitmap;
    }

    /**
     * rotate image depend on image's orientation
     *
     * @param source source bitmap
     * @param angle  angle that bitmap need to be rotated
     * @return rotated bitmap
     */
    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    /**
     * Change contrast and brightness of bitmap
     *
     * @param bmp                source bitmap
     * @param target             target bitmap
     * @param contrastProgress   contrast parameter 0..10, 1 is default
     * @param brightnessProgress brightness parameter -255..255, 0 is default
     */
    public static void editContrastBrightness(Bitmap bmp, Bitmap target, float contrastProgress, float brightnessProgress) {
        float contrast;
        if (contrastProgress < 50) {
            contrast = contrastProgress / 50f;
        } else {
            contrast = 1 + (contrastProgress - 50) / 50f * 9;
        }
        float brightness = (brightnessProgress - 50) * 255 / 50;
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        contrast, 0, 0, 0, brightness,
                        0, contrast, 0, 0, brightness,
                        0, 0, contrast, 0, brightness,
                        0, 0, 0, 1, 0
                });


        Canvas canvas = new Canvas(target);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);
    }

    public static void editHue(Bitmap srcBitmap, Bitmap desBitmap, float hueProgress) {
        float hue = (hueProgress - 50) * 180 / 100;
        hue = cleanValue(hue, 180f) / 180f * (float) Math.PI;
        if (BuildConfig.DEBUG) {
            Log.w(TAG, "" + hue);
        }
        float cosVal = (float) Math.cos(hue);
        float sinVal = (float) Math.sin(hue);
        float lumR = 0.213f;
        float lumG = 0.715f;
        float lumB = 0.072f;
        float[] mat = new float[]
                {
                        lumR + cosVal * (1 - lumR) + sinVal * (-lumR), lumG + cosVal * (-lumG) + sinVal * (-lumG), lumB + cosVal * (-lumB) + sinVal * (1 - lumB), 0, 0,
                        lumR + cosVal * (-lumR) + sinVal * (0.143f), lumG + cosVal * (1 - lumG) + sinVal * (0.140f), lumB + cosVal * (-lumB) + sinVal * (-0.283f), 0, 0,
                        lumR + cosVal * (-lumR) + sinVal * (-(1 - lumR)), lumG + cosVal * (-lumG) + sinVal * (lumG), lumB + cosVal * (1 - lumB) + sinVal * (lumB), 0, 0,
                        0f, 0f, 0f, 1f, 0f,
                        0f, 0f, 0f, 0f, 1f
                };
        ColorMatrix cm = new ColorMatrix(mat);
        Canvas canvas = new Canvas(desBitmap);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(srcBitmap, 0, 0, paint);
    }

    public static void editContrastHueLight(Bitmap srcBitmap, Bitmap desBitmap, float hue, float light, float contrast) {
        editContrastBrightness(srcBitmap, desBitmap, contrast, light);
        editHue(desBitmap, desBitmap, hue);
    }

    private static float cleanValue(float p_val, float p_limit) {
        return Math.min(p_limit, Math.max(-p_limit, p_val));
    }

    /**
     * Scale source bitmap to specific width and height
     *
     * @param bmp        source bitmap
     * @param viewWidth  required width of image
     * @param viewHeight required height of image
     * @return target bitmap
     */
    public static Bitmap centerBitmap(Bitmap bmp, float viewWidth, float viewHeight) {
        int bitmapWidth = bmp.getWidth();
        int bitmapHeight = bmp.getHeight();
        float ratioWidth = bitmapWidth / viewWidth;
        float ratioHeight = bitmapHeight / viewHeight;

        if (ratioWidth > ratioHeight) {
            bitmapWidth = (int) viewWidth;
            bitmapHeight = (int) (bitmapHeight / ratioWidth);
        } else {
            bitmapHeight = (int) viewHeight;
            bitmapWidth = (int) (bitmapWidth / ratioHeight);
        }
        return Bitmap.createScaledBitmap(bmp, bitmapWidth, bitmapHeight, true);
    }

    public static Bitmap matchBitmap(Bitmap bmp, float viewWidth, float viewHeight) {
        int bitmapWidth = bmp.getWidth();
        int bitmapHeight = bmp.getHeight();
        float ratioWidth = bitmapWidth / viewWidth;
        float ratioHeight = bitmapHeight / viewHeight;

        if (ratioWidth < ratioHeight) {
            bitmapWidth = (int) viewWidth;
            bitmapHeight = (int) (bitmapHeight / ratioWidth);
        } else {
            bitmapHeight = (int) viewHeight;
            bitmapWidth = (int) (bitmapWidth / ratioHeight);
        }
        return Bitmap.createScaledBitmap(bmp, bitmapWidth, bitmapHeight, true);
    }

    public static Bitmap getBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            bgDrawable.draw(canvas);
        } else {
            canvas.drawColor(Color.WHITE);
        }
        view.draw(canvas);
        return returnedBitmap;
    }

    public static void recycleBitmap(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
    }

    public static void setImageViewBitmap(ImageView imageView, Bitmap bitmap) {
        if (imageView == null) {
            return;
        }

        ((BitmapDrawable) imageView.getDrawable()).getBitmap().recycle();
        imageView.setImageBitmap(bitmap);
    }

    public static int getExifRotation(String path) {
        if (path == null || path.isEmpty()) return 0;
        try {
            ExifInterface exif = new ExifInterface(path);
            switch (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return 90;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return 180;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return 270;
                default:
                    return ExifInterface.ORIENTATION_UNDEFINED;
            }
        } catch (IOException e) {
            return 0;
        }
    }

    public static void invalidateGallery(Context context, File out) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Intent mediaScanIntent = new Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(out);
            mediaScanIntent.setData(contentUri);
            context.sendBroadcast(mediaScanIntent);
        } else {
            context.sendBroadcast(new Intent(
                    Intent.ACTION_MEDIA_MOUNTED,
                    Uri.parse("file://"
                            + Environment.getExternalStorageDirectory())));
        }
    }
}
