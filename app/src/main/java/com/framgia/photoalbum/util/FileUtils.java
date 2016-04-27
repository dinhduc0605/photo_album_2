package com.framgia.photoalbum.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by HungNT on 4/27/16.
 */
public class FileUtils {

    public static File createTemporaryFile(String part, String ext) throws IOException {
        File cacheDir = Environment.getExternalStorageDirectory();

        cacheDir = new File(cacheDir.getAbsolutePath() + "/.PhotoAlbumCached/");

        if (!cacheDir.exists()) cacheDir.mkdirs();

        return File.createTempFile(part, ext, cacheDir);
    }

    public static File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        File photo = createTemporaryFile(timeStamp, ".jpg");
        return photo;
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
}
