package com.framgia.photoalbum.ui.activty;

import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.framgia.photoalbum.R;
import com.framgia.photoalbum.data.model.ImageItem;
import com.framgia.photoalbum.ui.adapter.ImageGridAdapter;

import java.io.File;
import java.util.ArrayList;

public class ChooseImageActivity extends AppCompatActivity {
    RecyclerView imageGrid;
    ArrayList<ImageItem> imageItems = new ArrayList<>();
    ImageGridAdapter adapter;
    FloatingActionButton cameraBtn;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_image);
        initView();
        bindViewControl();
    }

    /**
     * Init view in layout
     */
    private void initView() {
        //set toolbar as actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //init view
        cameraBtn = (FloatingActionButton) findViewById(R.id.camera);
        imageGrid = (RecyclerView) findViewById(R.id.imageGrid);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 3);
        imageGrid.setLayoutManager(layoutManager);

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
                imageItems.add(imageItem);
            } while (imageCursor.moveToNext());
        }

        //set up image grid
        adapter = new ImageGridAdapter(this, imageItems);
        imageGrid.setAdapter(adapter);
    }

    /**
     * bind handle to each view in layout
     */
    private void bindViewControl() {

    }
}
