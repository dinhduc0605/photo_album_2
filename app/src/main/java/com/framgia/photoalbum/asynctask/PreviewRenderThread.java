package com.framgia.photoalbum.asynctask;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

import com.framgia.photoalbum.BuildConfig;
import com.framgia.photoalbum.util.VideoUtils;

public class PreviewRenderThread extends Thread {

    private final int FPS = 30;
    private final long OPTIMAL_TIME = 1000000000 / FPS;

    private int frame;
    private int maxFrame;
    private VideoUtils mVideoUtils;
    private final SurfaceHolder holder;
    private long lastUpdateTime;
    private long lastFpsTime;
    private int fps;

    private OnPreviewListener mOnPreviewListener;

    public PreviewRenderThread(VideoUtils videoUtils, SurfaceHolder holder) {
        this.mVideoUtils = videoUtils;
        this.holder = holder;
        this.maxFrame = mVideoUtils.getTotalFrame();
    }

    public void setPreviewFinishListener(OnPreviewListener listener) {
        this.mOnPreviewListener = listener;
    }

    @Override
    public void run() {
        long startTime = System.nanoTime();
        long updateLength;
        mOnPreviewListener.onStartPreview();
        mVideoUtils.playPreviewMusic();

        while (frame < maxFrame) {
            Canvas canvas = null;
            long now = System.nanoTime();

            try {
                canvas = holder.lockCanvas();
                synchronized (holder) {

                    if (BuildConfig.DEBUG) {
                        // update  FPS counter if a second has passed since last recorded
                        updateLength = now - lastUpdateTime;
                        lastUpdateTime = now;
                        lastFpsTime += updateLength;
                        fps++;

                        if (lastFpsTime >= 1000000000) {
                            Log.d("FPS", "(FPS: " + fps + ")");
                            lastFpsTime = 0;
                            fps = 0;
                        }
                    }

                    frame = (int) ((now - startTime) / OPTIMAL_TIME);
                    mVideoUtils.generateFramePreview(canvas, frame);
                }
            } finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas);
                }
            }
        }

        mVideoUtils.stopPreviewMusic();
        mOnPreviewListener.onFinishPreview();
    }

    public synchronized void stopPlaying() {
        frame = maxFrame;
    }

    public interface OnPreviewListener {
        void onStartPreview();
        void onFinishPreview();
    }
}