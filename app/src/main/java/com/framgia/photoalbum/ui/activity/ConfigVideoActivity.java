package com.framgia.photoalbum.ui.activity;

import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.framgia.photoalbum.R;
import com.framgia.photoalbum.asynctask.PreviewRenderThread;
import com.framgia.photoalbum.ui.adapter.ImagesPreviewAdapter;
import com.framgia.photoalbum.ui.custom.VerticalSpaceItemDecoration;
import com.framgia.photoalbum.ui.dialog.ChooseMusicDialog;
import com.framgia.photoalbum.util.DimenUtils;
import com.framgia.photoalbum.util.VideoUtils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by HungNT on 6/14/16.
 */
public class ConfigVideoActivity extends AppCompatActivity implements ImagesPreviewAdapter.OnItemClicked,
        PreviewRenderThread.OnPreviewListener {
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.listFeature)
    RecyclerView mRvListFeature;
    @Bind(R.id.surfacePreview)
    SurfaceView mSurfacePreview;
    @Bind(R.id.layoutFeature)
    LinearLayout layoutFeature;
    @Bind(R.id.layoutDuration)
    LinearLayout layoutDuration;
    @Bind(R.id.layoutTransition)
    View layoutTransition;
    @Bind(R.id.btnPlayPreview)
    View btnPlayPreview;

    private static final float VIDEO_ASPECT_RATIO = 16.0f / 9.0f;

    private ArrayList<String> mListPathImages;
    private ImagesPreviewAdapter mPreviewAdapter;
    private VideoUtils mVideoUtils;
    private PreviewRenderThread thread;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_video);
        ButterKnife.bind(this);
        setToolbar();

        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        mListPathImages = getIntent().
                getStringArrayListExtra(ChooseMultipleImagesActivity.KEY_IMAGE_PATHS);
        configureList();
        int scrW = DimenUtils.getDisplayMetrics(getApplicationContext()).widthPixels;

        mVideoUtils = new VideoUtils(this);
        mSurfacePreview.setLayoutParams(
                new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        (int) (scrW / VIDEO_ASPECT_RATIO),
                        Gravity.CENTER
                )
        );
    }


    private void configureList() {
        mPreviewAdapter = new ImagesPreviewAdapter(this, mListPathImages, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(
                this,
                LinearLayoutManager.VERTICAL,
                false);

        mRvListFeature.setLayoutManager(layoutManager);
        mRvListFeature.setAdapter(mPreviewAdapter);
        mRvListFeature.addItemDecoration(
                new VerticalSpaceItemDecoration(
                        (int) DimenUtils.dpToPx(
                                this,
                                getResources().getDimension(R.dimen.image_preview_divider)
                        )
                )
        );

        // Handle Swipe and Drag Gesture
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                mPreviewAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                return true;
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
                setButtonEnable(btnPlayPreview, false);
                gotoPreview();
                break;
            case R.id.btnTransition:
                toggleEffectBar(layoutTransition);
                break;
            case R.id.btnDuration:
                toggleEffectBar(layoutDuration);
                if (thread != null && thread.isAlive()) {
                    thread.stopPlaying();
                }
                break;
            case R.id.btnMusic:
                new ChooseMusicDialog(this).show();
                break;
        }
    }

    private void makeVideo() {
        new MakeVideoTask().execute(mVideoUtils);
    }

    @Override
    public void onClick(View v, int position) {
        // TODO select image
    }

    public void setToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.create_video);
    }

    @Override
    public void onStartPreview() {
    }

    @Override
    public void onFinishPreview() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setButtonEnable(btnPlayPreview, true);
                mSurfacePreview.setVisibility(View.GONE);
                mRvListFeature.setVisibility(View.VISIBLE);
            }
        });
    }

    public void setButtonEnable(View view, boolean enable) {
        if (enable) {
            view.setAlpha(1);
        } else {
            view.setAlpha(0.5f);
        }
        view.setClickable(enable);
    }

    private void gotoPreview() {
        mRvListFeature.setVisibility(View.GONE);
        mSurfacePreview.setVisibility(View.VISIBLE);
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mVideoUtils.preparePreview(2, mListPathImages, true);
                thread = new PreviewRenderThread(mVideoUtils, mSurfacePreview.getHolder());
                thread.setPreviewFinishListener(ConfigVideoActivity.this);
                thread.start();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mSurfacePreview.startAnimation(fadeIn);
    }

    private class MakeVideoTask extends AsyncTask<VideoUtils, Void, String> {
        @Override
        protected String doInBackground(VideoUtils... videoUtils) {
            /** test time, transition effect **/
            videoUtils[0].prepare(5, mListPathImages, true);
            return videoUtils[0].makeVideo();
        }

        @Override
        protected void onPostExecute(String outputPath) {
            super.onPostExecute(outputPath);
            Toast.makeText(getBaseContext(), "Your video is saved to " + outputPath, Toast.LENGTH_SHORT).show();
        }
    }

    private void toggleEffectBar(View view) {
        if (layoutFeature.getVisibility() == View.GONE) {
            layoutFeature.setVisibility(View.VISIBLE);
            layoutDuration.setVisibility(View.GONE);
            layoutTransition.setVisibility(View.GONE);
        } else {
            layoutFeature.setVisibility(View.GONE);
            view.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        if (layoutFeature.getVisibility() == View.GONE) {
            toggleEffectBar(null);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_configure_video, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.exportVideo) {
            makeVideo();
        } else if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (thread != null && thread.isAlive()) {
            thread.stopPlaying();
        }
    }
}
