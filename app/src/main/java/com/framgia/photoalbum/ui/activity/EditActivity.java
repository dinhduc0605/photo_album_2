package com.framgia.photoalbum.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.framgia.photoalbum.R;
import com.framgia.photoalbum.data.model.FeatureItem;
import com.framgia.photoalbum.ui.adapter.ListFeatureAdapter;
import com.framgia.photoalbum.util.CommonUtils;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class EditActivity extends AppCompatActivity {
    private static final String TAG = "EditActivity";
    @Bind(R.id.editImage)
    ImageView mEditImage;
    @Bind(R.id.listFeature)
    RecyclerView mListFeature;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    ArrayList<FeatureItem> mFeatureItems = new ArrayList<>();
    ListFeatureAdapter mAdapter;
    private int mViewWidth;
    private int mViewHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        initView();
        bindViewControl();
    }

    private void initData() {
        mFeatureItems.add(new FeatureItem(R.drawable.tab_effect_normal, getString(R.string.tab_effect)));
        mFeatureItems.add(new FeatureItem(R.drawable.tab_adjust_normal, getString(R.string.tab_adjust)));
        mFeatureItems.add(new FeatureItem(R.drawable.tab_crop_normal, getString(R.string.tab_crop)));
        mFeatureItems.add(new FeatureItem(R.drawable.tab_merge_normal, getString(R.string.tab_merge)));
        mFeatureItems.add(new FeatureItem(R.drawable.tab_high_light_normal, getString(R.string.tab_high_light)));
    }

    private void initView() {
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initData();
        ButterKnife.bind(this);
        mAdapter = new ListFeatureAdapter(this, mFeatureItems);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mListFeature.setLayoutManager(layoutManager);
        mListFeature.setAdapter(mAdapter);

        ViewTreeObserver viewTreeObserver = mEditImage.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mEditImage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    mViewWidth = mEditImage.getWidth();
                    mViewHeight = mEditImage.getHeight();
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        String imagePath = intent.getStringExtra(ChooseImageActivity.IMAGE_PATH);
        LoadImageTask loadImageTask = new LoadImageTask();
        loadImageTask.execute(imagePath);
    }

    private void bindViewControl() {

    }

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
                mEditImage.setImageBitmap(bitmap);
            }
        }
    }
}
