package com.framgia.photoalbum.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
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

    public static File createEditedImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File cacheDir = Environment.getExternalStorageDirectory();

        cacheDir = new File(cacheDir.getAbsolutePath() + "/Photo Album/");

        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }

        return File.createTempFile(timeStamp, ".jpg", cacheDir);
    }

    /**
     * get real file path from uri
     */
    public static String getPathFromUri(Uri uri, Context context) {
        String[] dataColumn = {MediaStore.Images.Media.DATA};
        String filePath;

        Cursor cursor = context.getContentResolver().query(uri, dataColumn, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(dataColumn[0]);
            filePath = cursor.getString(columnIndex);
            cursor.close();
        } else {
            filePath = uri.getPath();
        }

        return filePath;
    }

    public static void saveEditedImage(Context context, Bitmap editedBitmap) {
        FileOutputStream outputStream = null;
        File file;

        try {
            file = FileUtils.createEditedImageFile();
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
