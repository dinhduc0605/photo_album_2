package com.framgia.photoalbum.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.widget.Toast;

import com.framgia.photoalbum.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by HungNT on 4/27/16.
 */
public class FileUtils {
    public static final int VIDEO_TYPE = 1;
    public static final int IMAGE_TYPE = 0;
    public static final String IMG_TEMP_FILE_NAME = "img_temp.tmp";
    public static final String VIDEO_TEMP_FILE_NAME = "vid_temp.mp4";
    public static final String CACHED_FOLDER = "PhotoAlbumCached";
    public static final String APP_DIR = Environment.getExternalStorageDirectory() + File.separator + "Photo Album";
    public static final String CACHED_DIR = Environment.getExternalStorageDirectory() + File.separator + CACHED_FOLDER;

    /**
     * Create a temp file to store video or image temporarily
     *
     * @param directory
     * @param filename
     * @return
     * @throws IOException
     */
    public static File createTempFile(String directory, String filename) throws IOException {
        File cacheDir = new File(directory);

        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        File file = new File(cacheDir.getAbsolutePath(), filename);
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();
        return file;
    }

    /**
     * Create a file video or image with the name is timestamp
     *
     * @param type type video or image
     * @return
     * @throws IOException
     */
    public static File createMediaFile(int type) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File appDir = new File(APP_DIR);

        if (!appDir.exists()) {
            appDir.mkdirs();
        }
        if (type == IMAGE_TYPE) {
            return File.createTempFile(timeStamp, ".jpg", appDir);
        } else {
            return File.createTempFile(timeStamp, ".mp4", appDir);
        }
    }

    /**
     * Save the image after it's edited
     *
     * @param context
     * @param editedBitmap
     */
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

    /**
     * Copy audio file from raw folder to sdcard
     *
     * @param context
     * @param audioRes
     * @param filename
     * @return
     * @throws IOException
     */
    public static String copyAudioToDevice(Context context, int audioRes, String filename) throws IOException {
        File musicDir = new File(FileUtils.APP_DIR + File.separator + "Audio");
        if (!musicDir.exists()) {
            musicDir.mkdirs();
        }
        String outputPath = musicDir.getAbsolutePath() + "/" + filename;
        if (new File(outputPath).exists()) {
            return outputPath;
        }
        InputStream inputStream = context.getResources().openRawResource(audioRes);
        FileOutputStream fileOutputStream = new FileOutputStream(outputPath);
        int read = 0;
        byte buff[] = new byte[1024];
        while ((read = inputStream.read(buff)) > 0) {
            fileOutputStream.write(buff, 0, read);
        }
        inputStream.close();
        fileOutputStream.close();
        return outputPath;
    }

    /**
     * Copy from a file to another file
     *
     * @param input  input file's path
     * @param output output file's path
     * @return output file's path
     * @throws IOException
     */
    public static String copyFileToOther(String input, String output) throws IOException {
        File inputFile = new File(input);
        if (!inputFile.exists()) {
            return null;
        }
        FileInputStream inputStream = new FileInputStream(inputFile);
        FileOutputStream fileOutputStream = new FileOutputStream(output);
        int read = 0;
        byte buff[] = new byte[1024];
        while ((read = inputStream.read(buff)) > 0) {
            fileOutputStream.write(buff, 0, read);
        }
        inputStream.close();
        fileOutputStream.close();
        return output;
    }

    public static boolean isPhotoValid(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        return options.outWidth != 0 && options.outWidth != -1;
    }
}
