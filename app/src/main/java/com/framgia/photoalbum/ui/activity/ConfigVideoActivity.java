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
import android.support.v4.content.ContextCompat;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.framgia.photoalbum.Constant;
import com.framgia.photoalbum.R;
import com.framgia.photoalbum.asynctask.PreviewRenderThread;
import com.framgia.photoalbum.data.model.Song;
import com.framgia.photoalbum.ui.adapter.ImagesPreviewAdapter;
import com.framgia.photoalbum.ui.custom.VerticalSpaceItemDecoration;
import com.framgia.photoalbum.ui.dialog.ChooseMusicDialog;
import com.framgia.photoalbum.ui.dialog.ShareDialog;
import com.framgia.photoalbum.util.CommonUtils;
import com.framgia.photoalbum.util.DimenUtils;
import com.framgia.photoalbum.util.VideoUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.framgia.photoalbum.util.FileUtils.VIDEO_TYPE;
import static com.framgia.photoalbum.util.FileUtils.copyAudioToDevice;
import static com.framgia.photoalbum.util.FileUtils.copyFileToOther;
import static com.framgia.photoalbum.util.FileUtils.createMediaFile;
import static com.framgia.photoalbum.util.VideoUtils.FADE_TRANSITION;
import static com.framgia.photoalbum.util.VideoUtils.NONE_AUDIO;
import static com.framgia.photoalbum.util.VideoUtils.RANDOM_TRANSITION;
import static com.framgia.photoalbum.util.VideoUtils.ROTATE_TRANSITION;
import static com.framgia.photoalbum.util.VideoUtils.TRANSLATE_TRANSITION;
import static com.framgia.photoalbum.util.VideoUtils.ZOOM_TRANSITION;

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
    @Bind(R.id.radioDuration)
    RadioGroup mRadioDurationGroup;
    @Bind({
            R.id.random_transition,
            R.id.fade_transition,
            R.id.translate_transition,
            R.id.rotate_transition,
            R.id.zoom_transition
    })
    LinearLayout effectLayouts[];
    private static final float VIDEO_ASPECT_RATIO = 1.0f / 1.0f;

    private ArrayList<String> mListPathImages;
    private ImagesPreviewAdapter mPreviewAdapter;
    private VideoUtils mVideoUtils;
    private PreviewRenderThread mThread;

    private MakeVideoTask mMakeVideoTask;
    private ProgressDialog mProgressDialog;
    private int mDurationPerImage = 3;
    private int mTransitionType = RANDOM_TRANSITION;
    private Song mSong;
    private ChooseMusicDialog mChooseMusicDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_video);
        ButterKnife.bind(this);
        setToolbar();

        effectLayouts[0].setBackgroundColor(
                ContextCompat.getColor(this, R.color.colorPrimaryDark));
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
        mRadioDurationGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) findViewById(checkedId);
                mDurationPerImage = Integer.parseInt(radioButton.getText().toString().substring(0, 1));
            }
        });
        mChooseMusicDialog = new ChooseMusicDialog(this, new ChooseMusicDialog.OnAudioSelected() {
            @Override
            public void onSelected(Song song) {
                mSong = song;
                mVideoUtils.setAudio(mSong.getId());
            }
        });

        showSuggestDialog();
    }

    /**
     * Show dialog guide to how delete and swap image position in list preview images
     */
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


    /**
     * Create configure bar at bottom
     */
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


    @OnClick({
            R.id.btnPlayPreview,
            R.id.btnTransition,
            R.id.btnDuration,
            R.id.btnMusic,
            R.id.random_transition,
            R.id.fade_transition,
            R.id.translate_transition,
            R.id.rotate_transition,
            R.id.zoom_transition,
            R.id.radioDuration
    })
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
                if (mThread != null && mThread.isAlive()) {
                    mThread.stopPlaying();
                }
                break;
            case R.id.btnMusic:
                mChooseMusicDialog.show();
                break;
            case R.id.random_transition:
                mTransitionType = RANDOM_TRANSITION;
                selectTransitionEffect(0);
                break;
            case R.id.fade_transition:
                mTransitionType = FADE_TRANSITION;
                selectTransitionEffect(1);
                break;
            case R.id.translate_transition:
                mTransitionType = TRANSLATE_TRANSITION;
                selectTransitionEffect(2);
                break;
            case R.id.rotate_transition:
                mTransitionType = ROTATE_TRANSITION;
                selectTransitionEffect(3);
                break;
            case R.id.zoom_transition:
                mTransitionType = ZOOM_TRANSITION;
                selectTransitionEffect(4);
                break;
        }
    }

    /**
     * highlight the chosen effect
     * @param position chosen effect's position
     */
    public void selectTransitionEffect(int position) {
        for (LinearLayout effectLayout : effectLayouts) {
            effectLayout.setBackgroundColor(
                    ContextCompat.getColor(this, R.color.colorPrimary));
        }
        effectLayouts[position].setBackgroundColor(
                ContextCompat.getColor(this, R.color.colorPrimaryDark));
    }

    private void makeVideo() {
        mMakeVideoTask = new MakeVideoTask();
        mMakeVideoTask.execute(mVideoUtils);
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

    /**
     * Play preview
     */
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
                mVideoUtils.preparePreview(mDurationPerImage, mListPathImages, mTransitionType);
                mThread = new PreviewRenderThread(mVideoUtils, mSurfacePreview.getHolder());
                mThread.setPreviewFinishListener(ConfigVideoActivity.this);
                mThread.start();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mSurfacePreview.startAnimation(fadeIn);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    private class MakeVideoTask extends AsyncTask<VideoUtils, Void, String> implements VideoUtils.UpdateProgress {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(ConfigVideoActivity.this);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setMessage("Exporting Video");
            mProgressDialog.setMax(100);
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.setCancelable(true);
            mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    mMakeVideoTask.cancel(true);
                }
            });
            mProgressDialog.setProgress(0);
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(VideoUtils... videoUtils) {
            /** test time, transition effect **/
            videoUtils[0].setUp(mDurationPerImage, mListPathImages, mTransitionType);
            String srcVideo = videoUtils[0].makeVideo(this);
            String muxedVideoPath = null;
            String audioFilePath;
            try {
                if (mSong != null && mSong.getId() != NONE_AUDIO) {
                    audioFilePath = copyAudioToDevice(getBaseContext(), mSong.getId(), mSong.getName() + ".aac");
                    muxedVideoPath = videoUtils[0].addAudio(srcVideo, audioFilePath);
                } else {
                    File output = createMediaFile(VIDEO_TYPE);
                    muxedVideoPath = copyFileToOther(srcVideo, output.getAbsolutePath());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return muxedVideoPath;
        }

        @Override
        protected void onPostExecute(String outputPath) {
            super.onPostExecute(outputPath);
            mProgressDialog.dismiss();
            CommonUtils.invalidateGallery(getBaseContext(), new File(outputPath));
            showExportCompleteDialog(outputPath);
        }

        @Override
        public void update(int percent) {
            if (!isCancelled()) {
                mProgressDialog.setProgress(percent);
            }
        }
    }

    /**
     * show and hide configure bar at bottom
     * @param view
     */
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
            stopPreview();
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
        if (mThread != null && mThread.isAlive()) {
            mThread.stopPlaying();
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

    /**
     * Get list app can play video
     * @return list app
     */
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
