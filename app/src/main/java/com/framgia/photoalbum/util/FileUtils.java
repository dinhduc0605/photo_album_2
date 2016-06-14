package com.framgia.photoalbum.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.widget.Toast;

import com.framgia.photoalbum.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by HungNT on 4/27/16.
 */
public class FileUtils {
    public static final int VIDEO_TYPE = 1;
    public static final int IMAGE_TYPE = 0;
    private static final String IMG_TEMP_FILE_NAME = "img_temp.tmp";

    public static File createCacheFile() throws IOException {
        File cacheDir = Environment.getExternalStorageDirectory();
        cacheDir = new File(cacheDir.getAbsolutePath() + "/.PhotoAlbumCached/");

        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        File file = new File(cacheDir.getAbsolutePath(), IMG_TEMP_FILE_NAME);
        file.createNewFile();
        return file;
    }

    public static File createMediaFile(int type) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File cacheDir = Environment.getExternalStorageDirectory();

        cacheDir = new File(cacheDir.getAbsolutePath() + "/Photo Album/");

        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        if (type == IMAGE_TYPE) {
            return File.createTempFile(timeStamp, ".jpg", cacheDir);
        } else {
            return File.createTempFile(timeStamp, ".mp4", cacheDir);
        }
    }

    public static void saveEditedImage(Context context, Bitmap editedBitmap) {
        FileOutputStream outputStream = null;
        File file;

        try {
            file = FileUtils.createMediaFile(IMAGE_TYPE);
            outputStream = new FileOutputStream(file);
            editedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            CommonUtils.invalidateGallery(context, file);

            Toast.makeText(context,
                    context.getString(R.string.error_save_image_success) + file.getPath(),
                    Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(context,
                    R.string.error_cannot_save_image,
                    Toast.LENGTH_SHORT).show();
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.flush();
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
