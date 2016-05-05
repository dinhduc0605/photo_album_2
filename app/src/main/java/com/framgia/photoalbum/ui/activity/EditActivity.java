package com.framgia.photoalbum.ui.activity;

import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.framgia.photoalbum.R;
import com.framgia.photoalbum.data.model.FeatureItem;
import com.framgia.photoalbum.ui.adapter.ListFeatureAdapter;
import com.framgia.photoalbum.ui.fragment.CropFragment;
import com.framgia.photoalbum.ui.fragment.EditFragment;
import com.framgia.photoalbum.ui.fragment.EffectFragment;
import com.framgia.photoalbum.ui.fragment.HighlightFragment;
import com.framgia.photoalbum.util.CommonUtils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class EditActivity extends AppCompatActivity implements ListFeatureAdapter.OnFeatureClicked {
    private static final String TAG = "EditActivity";
    public static final int EFFECT_FEATURE = 0;
    public static final int ADJUST_FEATURE = 1;
    public static final int CROP_FEATURE = 2;
    public static final int MERGE_FEATURE = 3;
    public static final int HIGHLIGHT_FEATURE = 4;

    @Bind(R.id.editImage)
    ImageView mEditImage;
    @Bind(R.id.listFeature)
    RecyclerView mListFeature;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    private String mImagePath;

    ArrayList<FeatureItem> mFeatureItems = new ArrayList<>();
    ListFeatureAdapter mAdapter;
    ActionBar mActionBar;

    public static Bitmap imageBitmap;
    private EditFragment mEditFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        mImagePath = getImagePath();

        LoadImageTask loadImageTask = new LoadImageTask();
        loadImageTask.execute(mImagePath);
    }

    @Override
    public void onClick(View v, int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        switch (position) {
            case EFFECT_FEATURE:
                mEditFragment = EffectFragment.newInstance(mImagePath);
                fragmentTransaction
                        .replace(R.id.container, mEditFragment)
                        .addToBackStack(null)
                        .commit();
                break;
            case ADJUST_FEATURE:
                break;
            case CROP_FEATURE:
                mEditFragment = CropFragment.newInstance(mImagePath);
                fragmentTransaction
                        .replace(R.id.container, mEditFragment)
                        .addToBackStack(null)
                        .commit();
                break;
            case MERGE_FEATURE:
                break;
            case HIGHLIGHT_FEATURE:
                mActionBar.setTitle(getString(R.string.label_highlight_fragment));
                mEditFragment = new HighlightFragment();
                fragmentTransaction
                        .replace(R.id.container, mEditFragment)
                        .addToBackStack(getString(R.string.label_highlight_fragment))
                        .commit();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mActionBar.setTitle(getString(R.string.label_edit_activity));
            onBackPressed();
        } else if (item.getItemId() == R.id.action_done) {
            if (mEditFragment != null) {
                mEditFragment.apply();
            }
        }
        return true;
    }

    /**
     * Init data for feature bar at bottom
     */
    private void initData() {
        mFeatureItems.add(new FeatureItem(R.drawable.tab_effect_normal, getString(R.string.tab_effect)));
        mFeatureItems.add(new FeatureItem(R.drawable.tab_adjust_normal, getString(R.string.tab_adjust)));
        mFeatureItems.add(new FeatureItem(R.drawable.tab_crop_normal, getString(R.string.tab_crop)));
        mFeatureItems.add(new FeatureItem(R.drawable.tab_merge_normal, getString(R.string.tab_merge)));
        mFeatureItems.add(new FeatureItem(R.drawable.tab_high_light_normal, getString(R.string.tab_high_light)));
    }

    /**
     * get image's absolute path from intent
     *
     * @return image'path
     */
    private String getImagePath() {
        Intent intent = getIntent();
        int imageId = intent.getIntExtra(ChooseImageActivity.IMAGE_ID, 0);
        //get cursor loader of image table
        CursorLoader imageLoader = new CursorLoader(
                this,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media.DATA},
                imageId + " = " + MediaStore.Images.Media._ID, null, null);
        //get cursor point to image table
        Cursor imageCursor = imageLoader.loadInBackground();
        String imagePath = null;
        if (imageCursor.moveToFirst()) {
            // get image path
            imagePath = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA));
        }
        imageCursor.close();
        return imagePath;
    }

    /**
     * Init views in layout
     */
    private void initView() {
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        initData();
        mAdapter = new ListFeatureAdapter(this, mFeatureItems);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mListFeature.setLayoutManager(layoutManager);
        mListFeature.setAdapter(mAdapter);

    }

    /**
     * Bind control of each view
     */
    private void bindViewControl() {

    }

    /**
     * Load image in worker thread
     */
    private class LoadImageTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... paths) {
            Point screenSize = CommonUtils.getDisplaySize(EditActivity.this);
            return CommonUtils.decodeSampledBitmapResource(paths[0], screenSize.x, screenSize.y);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null) {
                imageBitmap = bitmap;
                initView();
                bindViewControl();
                mEditImage.setImageBitmap(imageBitmap);

            }
        }
    }

    public void clearFragment() {
        mEditFragment = null;
    }
}
