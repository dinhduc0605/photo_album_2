package com.framgia.photoalbum.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.framgia.photoalbum.BuildConfig;
import com.framgia.photoalbum.R;
import com.framgia.photoalbum.data.model.ImageItem;
import com.framgia.photoalbum.ui.adapter.ImageGridAdapter;
import com.framgia.photoalbum.util.CommonUtils;
import com.framgia.photoalbum.util.FileUtils;
import com.framgia.photoalbum.util.PermissionUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChooseImageActivity extends AppCompatActivity implements ImageGridAdapter.OnItemClickListener {
    public static final String IMAGE_PATH = "image_path";
    private static final int REQUEST_CAPTURE_IMAGE = 1001;
    private static final String TAG = "ChooseImageActivity";
    private static final String CAMERA_IMAGE_PATH = "CAMERA_IMAGE_PATH";
    private ArrayList<ImageItem> mImageItems = new ArrayList<>();
    private ImageGridAdapter mAdapter;
    private String mImagePath;
    private String mTempImagePath;

    @Bind(R.id.imageGrid)
    RecyclerView mImageGrid;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_image);
        if (BuildConfig.DEBUG) {
            Log.w(TAG, "onCreate ");
        }
        ButterKnife.bind(this);
        initView();
        bindViewControl();
    }

    /**
     * Init view in layout
     */
    private void initView() {
        //set mToolbar as actionbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //init view
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 3);
        mImageGrid.setLayoutManager(layoutManager);
        mImageItems = CommonUtils.getImageList(this);

        //set up image grid
        mAdapter = new ImageGridAdapter(this, mImageItems, this);
        mImageGrid.setAdapter(mAdapter);
    }

    /**
     * bind handle to each view in layout
     */
    private void bindViewControl() {

    }

    @OnClick(R.id.btnCamera)
    public void onClick(View view) {
        startCapture();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Handle image captured callback
        if (requestCode == REQUEST_CAPTURE_IMAGE && resultCode == RESULT_OK) {
            if (getIntent().getBooleanExtra(CollageActivity.KEY_COLLAGE, false)) {
                returnResultToCollage(mTempImagePath);
            } else {
                startEditorActivity(mTempImagePath);
            }
            if (BuildConfig.DEBUG)
                Log.d(TAG, "IMAGE SAVED: " + mTempImagePath);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void startEditorActivity(String photoPath) {
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra(IMAGE_PATH, photoPath);
        startActivity(intent);
    }

    private void returnResultToCollage(String photoPath) {
        Intent intent = new Intent();
        intent.putExtra(IMAGE_PATH, photoPath);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void startCapture() {
        if (!hasIntent(this, MediaStore.ACTION_IMAGE_CAPTURE)) {
            Toast.makeText(this, getResources().getString(R.string.error_camera_not_available),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File outPut = FileUtils.createImageFile("IMG_",
                FileUtils.getCacheDirectory(), ".JPEG");
        if (outPut == null) {
            Toast.makeText(
                    this,
                    getResources()
                            .getString(R.string.error_create_file_failed),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        mTempImagePath = outPut.getAbsolutePath();
        takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outPut));
        takeIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        if (takeIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeIntent, REQUEST_CAPTURE_IMAGE);
        }
    }

    @Override
    public void onItemClick(int position) {
        if (BuildConfig.DEBUG) {
            Log.w(TAG, mImageItems.get(position).getImagePath());
        }
        mImagePath = mImageItems.get(position).getImagePath();
        if (getIntent().getBooleanExtra(CollageActivity.KEY_COLLAGE, false)) {
            returnResultToCollage(mImagePath);
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                PermissionUtils.requestWriteStoragePermission(this, R.id.rootView);
            } else {
                startEditorActivity(mImagePath);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PermissionUtils.REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (permissions.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startEditorActivity(mImagePath);
            } else {
                Toast.makeText(this, getString(R.string.write_permission_not_granted), Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        if (mTempImagePath != null) {
            outState.putString(CAMERA_IMAGE_PATH, mTempImagePath);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey(CAMERA_IMAGE_PATH)) {
            mTempImagePath = savedInstanceState.getString(CAMERA_IMAGE_PATH, "");
        }
    }

    private boolean hasIntent(Context context, String action) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent it = new Intent(action);
        List<ResolveInfo> lst = packageManager.queryIntentActivities(it, PackageManager.MATCH_DEFAULT_ONLY);
        return lst.size() > 0;
    }
}
