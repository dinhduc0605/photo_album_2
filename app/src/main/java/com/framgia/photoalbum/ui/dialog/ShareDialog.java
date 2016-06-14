package com.framgia.photoalbum.ui.dialog;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.framgia.photoalbum.R;
import com.framgia.photoalbum.ui.activity.EditActivity;
import com.framgia.photoalbum.ui.adapter.ShareGridAdapter;
import com.framgia.photoalbum.util.CommonUtils;
import com.framgia.photoalbum.util.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by HungNT on 5/17/16.
 */
public class ShareDialog extends Dialog {

    @Bind(R.id.gridViewApps)
    GridView listShare;

    private List<ResolveInfo> mListApps;
    private Uri mImageUri;

    public ShareDialog(Context context, List<ResolveInfo> listApps) {
        super(context);
        this.mListApps = listApps;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_share);
        ButterKnife.bind(this);

        listShare.setAdapter(new ShareGridAdapter(getContext(), mListApps));
        listShare.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ResolveInfo resolveInfo = mListApps.get(position);
                try {
                    saveImage();
                    shareIntent(resolveInfo);
                } catch (IOException e) {
                    Toast.makeText(getContext(),
                            getContext().getString(R.string.error_cannot_save_image),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }

    private void saveImage() throws IOException {
        File file = FileUtils.createMediaFile(FileUtils.IMAGE_TYPE);
        FileOutputStream outputStream = new FileOutputStream(file);
        EditActivity.sSourceBitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream);
        CommonUtils.invalidateGallery(getContext(), file);

        mImageUri = Uri.fromFile(file);

        outputStream.flush();
        outputStream.close();
    }

    private void shareIntent(ResolveInfo item) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/jpeg");
        shareIntent.putExtra(Intent.EXTRA_STREAM, mImageUri);
        shareIntent.setComponent(new ComponentName(
                item.activityInfo.packageName,
                item.activityInfo.name
        ));
        getContext().startActivity(shareIntent);
        ShareDialog.this.dismiss();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        dismiss();
    }
}
