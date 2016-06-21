package com.framgia.photoalbum.ui.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
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

import com.framgia.photoalbum.Constant;
import com.framgia.photoalbum.R;
import com.framgia.photoalbum.asynctask.PreviewRenderThread;
import com.framgia.photoalbum.ui.adapter.ImagesPreviewAdapter;
import com.framgia.photoalbum.ui.custom.VerticalSpaceItemDecoration;
import com.framgia.photoalbum.ui.dialog.ChooseMusicDialog;
import com.framgia.photoalbum.ui.dialog.ShareDialog;
import com.framgia.photoalbum.util.CommonUtils;
import com.framgia.photoalbum.util.DimenUtils;
import com.framgia.photoalbum.util.FileUtils;
import com.framgia.photoalbum.util.VideoUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by HungNT on 6/14/16.
 */
public class ConfigVideoActivity extends AppCompatActivity implements PreviewRenderThread.OnPreviewListener {
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

    private MakeVideoTask makeVideoTask;
    private ProgressDialog progressDialog;

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

        showSuggestDialog();
    }

    private void showSuggestDialog() {
        SharedPreferences pref = getSharedPreferences(Constant.PREF_NAME, MODE_PRIVATE);
        if (pref.getBoolean(Constant.KEY_PREF_FIRST_TIME, false)) {
            return;
        }

        pref.edit().putBoolean(Constant.KEY_PREF_FIRST_TIME, true).apply();
        final Dialog dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.dialog_suggest);
        dialog.findViewById(R.id.btnOk).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    private void configureList() {
        mPreviewAdapter = new ImagesPreviewAdapter(this, mListPathImages);
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
                // if remove all image from list => finish config activity and back to choose image
                if (mListPathImages.isEmpty()) {
                    finish();
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(mRvListFeature);
    }


    @OnClick({R.id.btnPlayPreview, R.id.btnTransition, R.id.btnDuration, R.id.btnMusic})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnPlayPreview:
                view.setClickable(false);
                if (view.isSelected()) {
                    stopPreview();
                } else {
                    gotoPreview();
                }
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
        makeVideoTask = new MakeVideoTask();
        makeVideoTask.execute(mVideoUtils);
    }

    public void setToolbar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.create_video);
    }

    @Override
    public void onStartPreview() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setButtonStop(true);
                btnPlayPreview.setClickable(true);
            }
        });
    }

    @Override
    public void onFinishPreview() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setButtonStop(false);
                btnPlayPreview.setClickable(true);
                mSurfacePreview.setVisibility(View.GONE);
                mRvListFeature.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * Set button play or stop
     *
     * @param enable true in stop state, false in play state
     */
    public void setButtonStop(boolean enable) {
        btnPlayPreview.setSelected(enable);
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

    private class MakeVideoTask extends AsyncTask<VideoUtils, Void, String> implements VideoUtils.UpdateProgress {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ConfigVideoActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMessage("Exporting Video");
            progressDialog.setMax(100);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(true);
            progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    makeVideoTask.cancel(true);
                }
            });
            progressDialog.setProgress(0);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(VideoUtils... videoUtils) {
            /** test time, transition effect **/
            videoUtils[0].prepare(5, mListPathImages, true);
            String srcVideo = videoUtils[0].makeVideo(this);
            String muxedVideoPath = null;
            String audioFilePath = null;
            try {
                audioFilePath = FileUtils.copyAudioToDevice(getBaseContext(), R.raw.fade, "Fade.aac");
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                muxedVideoPath = videoUtils[0].addAudio(srcVideo, audioFilePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return muxedVideoPath;
        }

        @Override
        protected void onPostExecute(String outputPath) {
            super.onPostExecute(outputPath);
            progressDialog.dismiss();
            CommonUtils.invalidateGallery(getBaseContext(), new File(outputPath));
            showExportCompleteDialog(outputPath);
        }

        @Override
        public void update(int percent) {
            progressDialog.setProgress(percent);
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
            onBackPressed();
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopPreview();
    }

    private void stopPreview() {
        if (thread != null && thread.isAlive()) {
            thread.stopPlaying();
        }
    }

    private void showExportCompleteDialog(final String path) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.export_complete);

        builder.setPositiveButton(getString(R.string.share), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new ShareDialog(ConfigVideoActivity.this,
                        getListApps(), "video/mp4",
                        Uri.fromFile(new File(path)
                        )
                ).show();
            }
        });
        builder.setNegativeButton(getString(R.string.play), null);
        builder.setNeutralButton(getString(R.string.cancel), null);

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String videoPath = "file://" + path;
                Intent mVideoWatch = new Intent(Intent.ACTION_VIEW);
                mVideoWatch.setDataAndType(Uri.parse(videoPath), "video/mp4");
                startActivity(mVideoWatch);
            }
        });
    }

    private List<ResolveInfo> getListApps() {
        List<ResolveInfo> listApp;

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("video/mp4");
        PackageManager manager = getPackageManager();
        listApp = manager.queryIntentActivities(intent,
                PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);

        return listApp;
    }
}
