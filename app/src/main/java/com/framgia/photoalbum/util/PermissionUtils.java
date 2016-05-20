package com.framgia.photoalbum.util;

import android.Manifest;
import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.View;

import com.framgia.photoalbum.R;

/**
 * Created by dinhduc on 20/05/2016.
 */
public class PermissionUtils {
    public static final int REQUEST_WRITE_EXTERNAL_STORAGE = 0;

    public static void requestWriteStoragePermission(final Activity activity, int layoutId) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        )) {
            Snackbar.make(
                    activity.findViewById(layoutId),
                    R.string.storage_permission_explanation,
                    Snackbar.LENGTH_INDEFINITE
            ).setAction(activity.getResources().getString(R.string.ok), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityCompat.requestPermissions(
                            activity,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_WRITE_EXTERNAL_STORAGE
                    );
                }
            }).show();
        } else {
            ActivityCompat.requestPermissions(
                    activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_EXTERNAL_STORAGE
            );
        }
    }
}
