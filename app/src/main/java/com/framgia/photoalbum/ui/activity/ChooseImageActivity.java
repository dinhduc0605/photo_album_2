package com.framgia.photoalbum.ui.activity;

import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChooseImageActivity extends AppCompatActivity {
    public static final String IMAGE_PATH = "image_path";
    private static final int REQUEST_CAPTURE_IMAGE = 1001;
    private static final String TAG = "ChooseImageActivity";
    private Uri mPhotoUri;
    private ArrayList<ImageItem> mImageItems = new ArrayList<>();
    private ImageGridAdapter mAdapter;

    @Bind(R.id.imageGrid)
    RecyclerView mImageGrid;
    @Bind(R.id.btnCamera)
    FloatingActionButton mCameraBtn;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

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
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //init view
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 3);
        mImageGrid.setLayoutManager(layoutManager);
        mImageItems = getImageList();

        //set up image grid
        mAdapter = new ImageGridAdapter(this, mImageItems);
        mImageGrid.setAdapter(mAdapter);
    }

    /**
     * Get image list in device
     *
     * @return image list
     */
    private ArrayList<ImageItem> getImageList() {
        ArrayList<ImageItem> imageItems = new ArrayList<>();
        //get cursor loader of Thumbnail table
        CursorLoader imageLoader = new CursorLoader(this,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                //get image id & thumbnail path
                new String[]{MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA},
                null,
                null,
                MediaStore.Images.Media._ID);
        //get cursor point to thumbnail table
        Cursor imageCursor = imageLoader.loadInBackground();
        if (imageCursor.moveToLast()) {
            do {
                //get image path
                String imagePath = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                //get image id
                int id = imageCursor.getInt(imageCursor.getColumnIndex(MediaStore.Images.Media._ID));

                ImageItem imageItem = new ImageItem(imagePath, id);
                imageItems.add(imageItem);
            } while (imageCursor.moveToPrevious());
        }
        imageCursor.close();
        return imageItems;
    }

    /**
     * bind handle to each view in layout
     */
    private void bindViewControl() {

    }

    @OnClick(R.id.btnCamera)
    public void onClick(View view) {
        try {
            File photo = FileUtils.createCacheFile();
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
//                Toast.makeText(ChooseImageActivity.this, "path = " + photoPath, Toast.LENGTH_SHORT).show();
                Log.d(TAG, photoPath);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void startEditorActivity(String photoPath) {
        // TODO start editor activity with path of photo which captured or picked from album
        Intent intent = new Intent(this, EditActivity.class);
        intent.putExtra(IMAGE_PATH, photoPath);
        startActivity(intent);
    }

    private void startCapture(Uri path) {
        Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, path);

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
