package com.framgia.photoalbum.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.framgia.photoalbum.BuildConfig;
import com.framgia.photoalbum.R;
import com.framgia.photoalbum.data.model.FeatureItem;
import com.framgia.photoalbum.ui.adapter.ListFeatureAdapter;
import com.framgia.photoalbum.ui.fragment.AdjustFragment;
import com.framgia.photoalbum.ui.fragment.ColorAdjustmentFragment;
import com.framgia.photoalbum.ui.fragment.CropFragment;
import com.framgia.photoalbum.ui.fragment.EditFragment;
import com.framgia.photoalbum.ui.fragment.EffectFragment;
import com.framgia.photoalbum.ui.fragment.GammaFragment;
import com.framgia.photoalbum.ui.fragment.HighlightFragment;
import com.framgia.photoalbum.ui.fragment.OrientationFragment;
import com.framgia.photoalbum.util.CommonUtils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class EditActivity extends AppCompatActivity implements ListFeatureAdapter.OnFeatureClicked {
    private static final String TAG = "EditActivity";
    public static final int EFFECT_FEATURE = 0;
    public static final int COLOR_FEATURE = 1;
    public static final int ADJUST_FEATURE = 2;
    public static final int CROP_FEATURE = 3;
    public static final int HIGHLIGHT_FEATURE = 4;
    private static final int ORIENTATION_FEATURE = 5;
    private static final int GAMMA_FEATURE = 6;

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

    public static Bitmap sSourceBitmap;
    public static Bitmap sResultBitmap;
    public static Context sContext;

    LoadImageTask loadImageTask;

    private EditFragment mEditFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        mImagePath = getImagePath();

        loadImageTask = new LoadImageTask();
        loadImageTask.execute(mImagePath);

        sContext = getApplicationContext();
    }

    @Override
    public void onClick(View v, int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        switch (position) {
            case EFFECT_FEATURE:
                mActionBar.setTitle(getString(R.string.label_effect_fragment));
                mEditFragment = EffectFragment.newInstance(mImagePath);
                break;
            case COLOR_FEATURE:
                mActionBar.setTitle(getString(R.string.label_color_fragment));
                mEditFragment = ColorAdjustmentFragment.newInstance(mImagePath);
                break;
            case ADJUST_FEATURE:
                mActionBar.setTitle(getString(R.string.label_adjust_fragment));
                mEditFragment = new AdjustFragment();
                break;
            case CROP_FEATURE:
                mActionBar.setTitle(getString(R.string.label_crop_fragment));
                mEditFragment = CropFragment.newInstance(mImagePath);
                break;
            case HIGHLIGHT_FEATURE:
                mActionBar.setTitle(getString(R.string.label_highlight_fragment));
                mEditFragment = new HighlightFragment();
                break;
            case ORIENTATION_FEATURE:
                mActionBar.setTitle(getString(R.string.label_rotate_fragment));
                mEditFragment = OrientationFragment.newInstance(mImagePath);
                break;
            case GAMMA_FEATURE:
                mEditFragment = GammaFragment.newInstance(mImagePath);
                break;
        }
        fragmentTransaction
                .replace(R.id.container, mEditFragment)
                .addToBackStack(null)
                .commit();
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
                saveEffect();
                mEditImage.setImageBitmap(sSourceBitmap);
                mActionBar.setTitle(getString(R.string.label_edit_activity));
                onBackPressed();
            }
        }
        return true;
    }

    /**
     * Init data for feature bar at bottom
     */
    private void initData() {
        mFeatureItems.add(new FeatureItem(R.drawable.tab_effect_normal, getString(R.string.tab_effect)));
        mFeatureItems.add(new FeatureItem(R.drawable.tab_color_normal, getString(R.string.tab_color)));
        mFeatureItems.add(new FeatureItem(R.drawable.tab_adjust_normal, getString(R.string.tab_adjust)));
        mFeatureItems.add(new FeatureItem(R.drawable.tab_crop_normal, getString(R.string.tab_crop)));
        mFeatureItems.add(new FeatureItem(R.drawable.tab_high_light_normal, getString(R.string.tab_high_light)));
        mFeatureItems.add(new FeatureItem(R.drawable.tab_orientation, getString(R.string.orientation)));
        mFeatureItems.add(new FeatureItem(R.drawable.tab_gamma, getString(R.string.gamma)));
    }

    /**
     * get image's absolute path from intent
     *
     * @return image'path
     */
    private String getImagePath() {
        Intent intent = getIntent();
        return intent.getStringExtra(ChooseImageActivity.IMAGE_PATH);
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
        mAdapter = new ListFeatureAdapter(this, mFeatureItems, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
        );
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
                sSourceBitmap = bitmap;
                initView();
                bindViewControl();
                mEditImage.setImageBitmap(sSourceBitmap);
                if (BuildConfig.DEBUG) {
                    Log.w(TAG, "" + bitmap.getWidth() + " " + bitmap.getHeight());
                }

            }
        }
    }

    public void clearFragment() {
        mEditFragment = null;
    }

    public void saveEffect() {

        if (sResultBitmap != null && !sResultBitmap.isRecycled()) {
            CommonUtils.recycleBitmap(sSourceBitmap);
            sSourceBitmap = sResultBitmap.copy(sResultBitmap.getConfig(), true);
        }
    }

    public static void setResultBitmap(Bitmap bitmap) {
        CommonUtils.recycleBitmap(sResultBitmap);
        sResultBitmap = bitmap;
    }

    @Override
    protected void onDestroy() {
        CommonUtils.setImageViewBitmap(mEditImage, null);
        if (loadImageTask != null && loadImageTask.isCancelled()) {
            loadImageTask.cancel(true);
        }
        super.onDestroy();
    }
}
