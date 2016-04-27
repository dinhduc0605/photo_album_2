package com.framgia.photoalbum.ui.activity;

import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.framgia.photoalbum.BuildConfig;
import com.framgia.photoalbum.R;
import com.framgia.photoalbum.data.model.ImageItem;
import com.framgia.photoalbum.ui.adapter.ImageGridAdapter;
import com.framgia.photoalbum.util.CommonUtils;
import com.framgia.photoalbum.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChooseImageActivity extends AppCompatActivity {

    private static final int REQUEST_CAPTURE_IMAGE = 1001;

    private RecyclerView mImageGrid;
    private ArrayList<ImageItem> mImageItems = new ArrayList<>();
    private ImageGridAdapter mAdapter;
    private Toolbar mToolbar;

    private Uri mPhotoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_image);
        ButterKnife.bind(this);
        initView();
        bindViewControl();
    }

    /**
     * Init view in layout
     */
    private void initView() {
        //set mToolbar as actionbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //init view
        mImageGrid = (RecyclerView) findViewById(R.id.imageGrid);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 3);
        mImageGrid.setLayoutManager(layoutManager);

        //get cursor point to Image table
        CursorLoader cursorLoader = new CursorLoader(this,
                MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA},
                null,
                null,
                MediaStore.Images.Thumbnails.IMAGE_ID);
        Cursor imageCursor = cursorLoader.loadInBackground();
        if (imageCursor.moveToFirst()) {
            do {
                String path = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                int id = imageCursor.getInt(imageCursor.getColumnIndex(MediaStore.Images.Media._ID));

                ImageItem imageItem = new ImageItem(path, id);
                mImageItems.add(imageItem);
            } while (imageCursor.moveToNext());
        }

        //set up image grid
        mAdapter = new ImageGridAdapter(this, mImageItems);
        mImageGrid.setAdapter(mAdapter);
    }

    /**
     * bind handle to each view in layout
     */
    private void bindViewControl() {

    }


    @OnClick(R.id.btnCamera)
    public void captureImage() {
        try {
            File photo = FileUtils.createImageFile();
            mPhotoUri = Uri.fromFile(photo);
            startCapture(mPhotoUri);
        } catch (IOException e) {
            Toast.makeText(this,
                    getResources().getString(R.string.error_create_file_failed),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Handle image captured callback
        if (requestCode == REQUEST_CAPTURE_IMAGE && resultCode == RESULT_OK) {
            String photoPath = FileUtils.getPathFromUri(mPhotoUri, this);
            startEditorActivity(photoPath);

            if (BuildConfig.DEBUG)
                Toast.makeText(ChooseImageActivity.this, "path = " + photoPath, Toast.LENGTH_SHORT).show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void startEditorActivity(String photoPath) {
        // TODO start editor activity with path of photo which captured or picked from album

    }

    private void startCapture(Uri path) {
        Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, path);

        // Check whether camera not available
        if (!CommonUtils.isAvailable(this, takeIntent)) {
            Toast.makeText(this,
                    getResources().getString(R.string.error_camera_not_available),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (takeIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeIntent, REQUEST_CAPTURE_IMAGE);
        }
    }
}
