package com.framgia.photoalbum.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.framgia.photoalbum.R;
import com.framgia.photoalbum.data.model.ImageItem;
import com.framgia.photoalbum.ui.adapter.ChooseMultipleImageAdapter;
import com.framgia.photoalbum.util.CommonUtils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ChooseMultipleImagesActivity extends AppCompatActivity {
    private static final String TAG = "ChooseMultipleActivity";
    protected static final String KEY_IMAGE_PATHS = "KEY_IMAGE_PATHS";

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
            if (chosenImages.size() == 0) {
                Toast.makeText(this, "Please choose images to create video", Toast.LENGTH_SHORT).show();
            }else {
                startConfigVideoActivity(chosenImages);
            }
        } else if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    private void startConfigVideoActivity(ArrayList<String> listPaths) {
        Intent intent = new Intent(this, ConfigVideoActivity.class);
        intent.putStringArrayListExtra(KEY_IMAGE_PATHS, listPaths);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();
    }
}
