package com.framgia.photoalbum.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;

import com.framgia.photoalbum.BuildConfig;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by nguyendinhduc on 14/06/2016.
 */
public class VideoUtils {
    public static final int FADE_TRANSITION = 0;
    public static final int SLIDE_TRANSITION = 1;
    public static final int ZOOM_TRANSITION = 2;
    public static final int ROTATE_TRANSITION = 3;
    private final long NANO_SECOND = 1000000000;
    private static final String TAG = "VideoUtils";

    /**
     * Set output video's type
     */
    private static final String MIME_TYPE = "video/avc";
    /**
     * video's bitrate
     */
    private static final int BIT_RATE = 2000000;
    /**
     * video's fps
     */
    private static final int FRAMES_PER_SECOND = 30;
    private static final int I_FRAME_INTERVAL = 5;
    private static final int VIDEO_WIDTH = 640;
    private static final int VIDEO_HEIGHT = 480;

    private MediaCodec.BufferInfo mBufferInfo;
    private MediaCodec mEncoder;
    private MediaMuxer mMuxer;
    private Surface mSurface;
    private int mTrackIndex;
    private boolean mIsMuxerStarted;
    private long mPresentationTime;

    private Context mContext;
    private File mOutputVideo;
    private Bitmap mImageBmp, mBackgroundBmp;
    private Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
    private int mTransitionType;
    /**
     * the number frames of video
     */
    private int mNumFramePerImage;
    private int mDurationPerImage;
    private int mNumImage;
    private ArrayList<String> mChosenImages;
    private boolean mIsTransitionRandom;

    public VideoUtils(Context context) {
        mContext = context;
        try {
            mOutputVideo = FileUtils.createMediaFile(FileUtils.VIDEO_TYPE);
            prepareEncoder(mOutputVideo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void prepare(int duration, ArrayList<String> chosenImages, boolean isRandom) {
        mChosenImages = chosenImages;
        mDurationPerImage = duration;
        mNumImage = chosenImages.size();
        mIsTransitionRandom = isRandom;
    }

    /**
     * @return output video's path
     */
    public String makeVideo() {
        mNumFramePerImage = mDurationPerImage * FRAMES_PER_SECOND;
        for (int i = 0; i < mNumImage; i++) {
            if (i > 2) {
                mBackgroundBmp.recycle();
            }
            /** save previous image to present background image **/
            mBackgroundBmp = mImageBmp;
            /** Load image into imageBmp **/
            mImageBmp = BitmapFactory.decodeFile(mChosenImages.get(i));
            mImageBmp = Bitmap.createScaledBitmap(mImageBmp, VIDEO_WIDTH, VIDEO_HEIGHT, true);

            Random random = new Random();
            if (mIsTransitionRandom) {
                mTransitionType = random.nextInt(4);
            }
            for (int j = 1; j <= mNumFramePerImage; j++) {
                drainEncoder(false);
                generateFrame(j);
            }
//            float percent = 100f * i / mNumFramePerImage;
        }
        drainEncoder(true);
        releaseEncoder();
        return mOutputVideo.getAbsolutePath();
    }

    private void prepareEncoder(File outputVideo) throws IOException {
        mBufferInfo = new MediaCodec.BufferInfo();
        /** set up media format **/
        MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE, VIDEO_WIDTH, VIDEO_HEIGHT);
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        format.setInteger(MediaFormat.KEY_BIT_RATE, BIT_RATE);
        format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAMES_PER_SECOND);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, I_FRAME_INTERVAL);
        if (BuildConfig.DEBUG) {
            Log.w(TAG, "prepareEncoder: " + format);
        }
        /** create encoder **/
        mEncoder = MediaCodec.createEncoderByType(MIME_TYPE);
        mEncoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mSurface = mEncoder.createInputSurface();
        mEncoder.start();

        /** create muxer **/
        mMuxer = new MediaMuxer(outputVideo.getAbsolutePath(),
                MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        mTrackIndex = -1;
        mIsMuxerStarted = false;
    }

    private void releaseEncoder() {
        if (mEncoder != null) {
            mEncoder.stop();
            mEncoder.release();
            mEncoder = null;
        }
        if (mSurface != null) {
            mSurface.release();
            mSurface = null;
        }
        if (mMuxer != null) {
            mMuxer.stop();
            mMuxer.release();
            mMuxer = null;
        }
        if (mImageBmp != null) {
            mImageBmp.recycle();
            mImageBmp = null;
        }
        if (mBackgroundBmp != null) {
            mBackgroundBmp.recycle();
            mBackgroundBmp = null;
        }
    }

    /**
     * get output buffer from encoder for muxer write to output file
     *
     * @param endOfStream
     */
    private void drainEncoder(boolean endOfStream) {
        /** time between 2 frame (microsecond) **/
        final int TIMEOUT = 3000;
        if (endOfStream) {
            mEncoder.signalEndOfInputStream();
        }
        ByteBuffer[] encoderOutputBuffers = mEncoder.getOutputBuffers();
        while (true) {
            /** get a block output buffer with timeout microsecond **/
            int encoderStatus = mEncoder.dequeueOutputBuffer(mBufferInfo, TIMEOUT);
            if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                if (!endOfStream) {
                    break;
                } else {
                    Log.d(TAG, "no output available");
                }
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                /** output buffer change **/
                encoderOutputBuffers = mEncoder.getOutputBuffers();
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                /** format only change once before receive buffer **/
                if (mIsMuxerStarted) {
                    throw new RuntimeException("format changed twice");
                }
                MediaFormat newFormat = mEncoder.getOutputFormat();
                mTrackIndex = mMuxer.addTrack(newFormat);
                mMuxer.start();
                mIsMuxerStarted = true;
            } else if (encoderStatus < 0) {
                Log.d(TAG, encoderStatus + "");
            } else {
                ByteBuffer encodedData = encoderOutputBuffers[encoderStatus];
                if (encodedData == null) {
                    throw new RuntimeException("encoderOutputBuffer " + encoderStatus + " was null");
                }
                if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                    /** ignore this flag **/
                    mBufferInfo.size = 0;
                }
                if (mBufferInfo.size != 0) {
                    if (!mIsMuxerStarted) {
                        throw new RuntimeException("muxer hasn't started");
                    }
                    encodedData.position(mBufferInfo.offset);
                    encodedData.limit(mBufferInfo.offset + mBufferInfo.size);
                    mBufferInfo.presentationTimeUs = mPresentationTime;
                    mPresentationTime += 1000000L / FRAMES_PER_SECOND;
                    mMuxer.writeSampleData(mTrackIndex, encodedData, mBufferInfo);
                }
                mEncoder.releaseOutputBuffer(encoderStatus, false);
                if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    if (!endOfStream) {
                        Log.d(TAG, "unexpectedly reach end of stream");
                    } else {
                        Log.d(TAG, "reach end of stream");
                    }
                    break;
                }
            }
        }
    }

    /**
     * generate each frame according to it's position
     *
     * @param framePos frame position of total frames
     */
    private void generateFrame(int framePos) {
        Canvas canvas = mSurface.lockCanvas(null);
        if (mBackgroundBmp != null) {
            canvas.drawBitmap(mBackgroundBmp, new Matrix(), paint);
        } else {
            canvas.drawColor(Color.BLACK);
        }

        Matrix matrix = createTransitionEffect(framePos);
        canvas.drawBitmap(mImageBmp, matrix, paint);
        mSurface.unlockCanvasAndPost(canvas);
    }

    /**
     * @param framePos frame position of each image
     * @return presentation time of frame according to frame position
     */
    private long computePresentationTime(int framePos) {
        return framePos * NANO_SECOND / FRAMES_PER_SECOND;
    }

    /**
     * @param framePos frame position of each image
     * @return effect matrix
     */
    private Matrix createTransitionEffect(int framePos) {
        Matrix matrix = new Matrix();
        long currentDuration = computePresentationTime(framePos);
        DisplayMetrics displayMetrics = DimenUtils.getDisplayMetrics(mContext);
        switch (mTransitionType) {
            case ZOOM_TRANSITION:
                float currentZoom = 0.1f + currentDuration * (1f - 0.1f) / NANO_SECOND / mDurationPerImage;
                matrix.setScale(currentZoom, currentZoom);
                paint.setAlpha(255);
                break;
            case SLIDE_TRANSITION:
                float currentTranslate = -displayMetrics.widthPixels + (float) currentDuration * displayMetrics.widthPixels / NANO_SECOND / mDurationPerImage;
                matrix.setTranslate(currentTranslate, 0);
                paint.setAlpha(255);
                break;
            case FADE_TRANSITION:
                float currentAlpha = (float) currentDuration * 255 / NANO_SECOND / mDurationPerImage;
                paint.setAlpha((int) currentAlpha);
                break;
            case ROTATE_TRANSITION:
                float currentAngle = -90 + (float) currentDuration * 90 / NANO_SECOND / mDurationPerImage;
                matrix.setRotate(currentAngle);
                paint.setAlpha(255);
                break;
        }
        return matrix;
    }
}
