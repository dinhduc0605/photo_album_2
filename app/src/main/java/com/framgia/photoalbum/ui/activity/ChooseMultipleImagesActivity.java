package com.framgia.photoalbum.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.framgia.photoalbum.R;
import com.framgia.photoalbum.data.model.ImageItem;
import com.framgia.photoalbum.ui.adapter.ChooseMultipleImageAdapter;
import com.framgia.photoalbum.util.CommonUtils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ChooseMultipleImagesActivity extends AppCompatActivity {
    private static final String TAG = "ChooseMultipleActivity";
    @Bind(R.id.multiple_image_grid)
    RecyclerView mImageGrid;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    private ArrayList<ImageItem> mImageItems;
    private ChooseMultipleImageAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_multiple_images);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mImageItems = CommonUtils.getImageList(this);
        mAdapter = new ChooseMultipleImageAdapter(this, mImageItems);
        mImageGrid.setLayoutManager(new GridLayoutManager(this, 3));
        mImageGrid.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_choose_multiple_image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_done) {
            ArrayList<String> chosenImages = mAdapter.getChosenImages();
            Log.d(TAG, chosenImages + "");
        } else if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();
    }
}
