package com.framgia.photoalbum.ui.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.Toast;

import com.framgia.photoalbum.R;
import com.framgia.photoalbum.ui.adapter.ImagesPreviewAdapter;
import com.framgia.photoalbum.util.VideoUtils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by HungNT on 6/14/16.
 */
public class ConfigVideoActivity extends AppCompatActivity implements ImagesPreviewAdapter.OnItemClicked {
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.listFeature)
    RecyclerView mRvListFeature;

    private ArrayList<String> mListPathImages;
    private ImagesPreviewAdapter mPreviewAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_video);
        ButterKnife.bind(this);
        setToolbar();

        mListPathImages = getIntent().
                getStringArrayListExtra(ChooseMultipleImagesActivity.KEY_IMAGE_PATHS);

        mPreviewAdapter = new ImagesPreviewAdapter(this, mListPathImages, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false);
        mRvListFeature.setLayoutManager(layoutManager);
        mRvListFeature.setAdapter(mPreviewAdapter);

        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                mPreviewAdapter.remove(viewHolder.getAdapterPosition());
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(mRvListFeature);

    }

    @OnClick({R.id.btnPlayPreview, R.id.btnTransition, R.id.btnDuration, R.id.btnMusic})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnPlayPreview:
                makeVideo();
                break;
            case R.id.btnTransition:
                break;
            case R.id.btnDuration:
                break;
            case R.id.btnMusic:
                break;
        }
    }

    private void makeVideo() {
        new MakeVideoTask().execute(new VideoUtils(this));
    }

    @Override
    public void onClick(View v, int position) {
        // TODO select image
    }

    public void setToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private class MakeVideoTask extends AsyncTask<VideoUtils, Void, String> {
        @Override
        protected String doInBackground(VideoUtils... videoUtils) {
            videoUtils[0].prepare(5, mListPathImages);
            return videoUtils[0].makeVideo();
        }

        @Override
        protected void onPostExecute(String outputPath) {
            super.onPostExecute(outputPath);
            Toast.makeText(getBaseContext(), "Your video is saved to " + outputPath, Toast.LENGTH_SHORT).show();
        }
    }

}
