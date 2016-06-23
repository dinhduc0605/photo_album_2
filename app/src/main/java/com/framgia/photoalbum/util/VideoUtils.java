package com.framgia.photoalbum.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.Container;
import com.framgia.photoalbum.BuildConfig;
import com.googlecode.mp4parser.FileDataSourceImpl;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AACTrackImpl;
import com.googlecode.mp4parser.authoring.tracks.CroppedTrack;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static com.framgia.photoalbum.util.FileUtils.APP_DIR;
import static com.framgia.photoalbum.util.FileUtils.VIDEO_TEMP_FILE_NAME;
import static com.framgia.photoalbum.util.FileUtils.createTempFile;

/**
 * Created by nguyendinhduc on 14/06/2016.
 */
public class VideoUtils {
    public static final int NONE_AUDIO = -1;
    public static final int FADE_TRANSITION = 0;
    public static final int TRANSLATE_TRANSITION = 1;
    public static final int ZOOM_TRANSITION = 2;
    public static final int ROTATE_TRANSITION = 3;
    private static final int ROTATE_NEW_TRANSITION = 4;
    public static final int RANDOM_TRANSITION = 5;
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
    private static final int FRAMES_PER_SECOND = 60;
    private static final int I_FRAME_INTERVAL = 5;
    private static final int VIDEO_WIDTH = 720;
    private static final int VIDEO_HEIGHT = 720;
    private final float SCALE_PREVIEW;

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
    private int mTotalFrame;
    private MediaPlayer mPreviewPlayer;
    private int previousImage = 0;
    private int[] mArrTransition;
    private int mAudioRes = NONE_AUDIO;

    public VideoUtils(Context context) {
        mContext = context;
        SCALE_PREVIEW = (float) DimenUtils.getDisplayMetrics(mContext).widthPixels / VIDEO_WIDTH;
        try {
            mOutputVideo = createTempFile(APP_DIR, VIDEO_TEMP_FILE_NAME);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getTotalFrame() {
        return mTotalFrame;
    }

    /**
     * Apply user's choice
     *
     * @param duration       duration time per image
     * @param chosenImages   images which user chose
     * @param transitionType transition type
     */
    public void setUp(int duration, ArrayList<String> chosenImages, int transitionType) {
        mChosenImages = chosenImages;
        mDurationPerImage = duration;
        mNumImage = chosenImages.size();
        if (transitionType == RANDOM_TRANSITION) {
            mIsTransitionRandom = true;
        } else {
            mIsTransitionRandom = false;
            mTransitionType = transitionType;
        }

    }

    public void setAudio(int mAudio) {
        mAudioRes = mAudio;
    }

    public void preparePreview(int duration, ArrayList<String> chosenImages, int transitionType) {
        setUp(duration, chosenImages, transitionType);
        mNumFramePerImage = mDurationPerImage * FRAMES_PER_SECOND;
        mTotalFrame = mNumImage * mDurationPerImage * FRAMES_PER_SECOND;
        previousImage = 0;
        CommonUtils.recycleBitmap(mBackgroundBmp);
        CommonUtils.recycleBitmap(mImageBmp);
        if (mIsTransitionRandom) {
            generateRandTransition();
            mTransitionType = mArrTransition[0];
        }

        mImageBmp = CommonUtils.decodeSampledBitmapResource(mChosenImages.get(0), VIDEO_WIDTH, VIDEO_HEIGHT);
        mImageBmp = CommonUtils.centerCropImage(mImageBmp, VIDEO_WIDTH, VIDEO_HEIGHT);
    }

    /**
     * @return output video's path
     */
    public String makeVideo(AsyncTask asyncTask) {
        UpdateProgress updateProgress = (UpdateProgress) asyncTask;
        try {
            prepareEncoder(mOutputVideo);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mNumFramePerImage = mDurationPerImage * FRAMES_PER_SECOND;
        if (mIsTransitionRandom && mArrTransition == null) {
            generateRandTransition();
        }

        for (int i = 0; i < mNumImage; i++) {
            if (i > 2) {
                mBackgroundBmp.recycle();
            }
            /** save previous image to present background image **/
            mBackgroundBmp = mImageBmp;
            /** Load image into imageBmp **/

            mImageBmp = CommonUtils.decodeSampledBitmapResource(mChosenImages.get(i), VIDEO_WIDTH, VIDEO_HEIGHT);
            mImageBmp = CommonUtils.centerCropImage(mImageBmp, VIDEO_WIDTH, VIDEO_HEIGHT);
            if (mIsTransitionRandom) {
                mTransitionType = mArrTransition[i];
            }
            for (int j = 1; j <= mNumFramePerImage; j++) {
                if (asyncTask.isCancelled()) {
                    return null;
                }
                drainEncoder(false);
                generateFrame(j);
                updateProgress.update((int) ((float) (i * mNumFramePerImage + j) / (mNumImage * mNumFramePerImage) * 100));
            }
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
                    mPresentationTime += 1000000f / FRAMES_PER_SECOND;
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
            canvas.drawBitmap(mBackgroundBmp, new Matrix(), null);
        } else {
            canvas.drawColor(Color.BLACK);
        }

        Matrix matrix = createTransitionEffect(framePos);
        canvas.drawBitmap(mImageBmp, matrix, paint);
        mSurface.unlockCanvasAndPost(canvas);
    }

    public void generateFramePreview(Canvas canvas, int framePos) {
        if (framePos >= mTotalFrame) {
            mImageBmp.eraseColor(Color.TRANSPARENT);
            canvas.drawBitmap(mImageBmp, 0, 0, paint);
            return;
        }

        int imagePos = framePos / mNumFramePerImage;
        if (imagePos > previousImage) {
            previousImage = imagePos;
            if (imagePos > 2) {
                mBackgroundBmp.recycle();
            }
            /** save previous image to present background image **/
            mBackgroundBmp = mImageBmp;
            /** Load image into imageBmp **/
            mImageBmp = CommonUtils.decodeSampledBitmapResource(mChosenImages.get(imagePos), VIDEO_WIDTH, VIDEO_HEIGHT);
            mImageBmp = CommonUtils.centerCropImage(mImageBmp, VIDEO_WIDTH, VIDEO_HEIGHT);

            if (mIsTransitionRandom) {
                mTransitionType = mArrTransition[imagePos];
            }
        }

        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        canvas.scale(SCALE_PREVIEW, SCALE_PREVIEW);

        if (mBackgroundBmp != null && !mBackgroundBmp.isRecycled()) {
            canvas.drawBitmap(mBackgroundBmp, 0, 0, paint);
        }

        Matrix matrix = createTransitionEffect(framePos % mNumFramePerImage + 1);
        canvas.drawBitmap(mImageBmp, matrix, paint);
    }


    /**
     * @param framePos frame position of each image
     * @return effect matrix
     */
    private Matrix createTransitionEffect(int framePos) {
        Matrix matrix = new Matrix();
        int totalFramePerImage = FRAMES_PER_SECOND * mDurationPerImage;
        if (framePos <= totalFramePerImage / 2) {
            DisplayMetrics displayMetrics = DimenUtils.getDisplayMetrics(mContext);
            switch (mTransitionType) {
                case ZOOM_TRANSITION:
                    float currentZoom = 0.1f + 2 * framePos * (1f - 0.1f) / totalFramePerImage;
                    matrix.setScale(currentZoom, currentZoom);
                    paint.setAlpha(255);
                    break;
                case TRANSLATE_TRANSITION:
                    float currentTranslate = -displayMetrics.widthPixels + (float) 2 * framePos * displayMetrics.widthPixels / totalFramePerImage;
                    matrix.setTranslate(currentTranslate, 0);
                    paint.setAlpha(255);
                    break;
                case FADE_TRANSITION:
                    float currentAlpha = (float) 2 * framePos * 255 / totalFramePerImage;
                    paint.setAlpha((int) currentAlpha);
                    break;
                case ROTATE_TRANSITION:
                    float currentAngle = -90 + (float) 2 * framePos * 90 / totalFramePerImage;
                    matrix.setRotate(currentAngle);
                    paint.setAlpha(255);
                    break;
                case ROTATE_NEW_TRANSITION:
                    float currentAngle1 = -90 + (float) 2 * framePos * 720 / totalFramePerImage;
                    float currentZoom1 = (float) framePos / totalFramePerImage;
                    matrix.postScale(currentZoom1, currentZoom1);
                    matrix.postRotate(currentAngle1, VIDEO_WIDTH / 2, VIDEO_HEIGHT / 2);
                    paint.setAlpha(255);
                    break;
            }
        }
        return matrix;
    }

    public void playPreviewMusic() {
        if (mAudioRes != NONE_AUDIO) {
            mPreviewPlayer = MediaPlayer.create(mContext, mAudioRes);
            mPreviewPlayer.setLooping(true);
            mPreviewPlayer.start();
        }
    }

    public void stopPreviewMusic() {
        if (mPreviewPlayer != null && mPreviewPlayer.isPlaying()) {
            mPreviewPlayer.stop();
        }
    }

    /**
     * Mix audio and video
     *
     * @param videoSource video from chosen image
     * @param audioSource chosen audio
     * @return
     * @throws IOException
     */
    public String addAudio(String videoSource, String audioSource) throws IOException {
        File output = FileUtils.createMediaFile(FileUtils.VIDEO_TYPE);
        Movie mp4Vid = MovieCreator.build(videoSource);
        /** get duration of video **/
        IsoFile isoFile = new IsoFile(videoSource);
        double lengthInSeconds = (double)
                isoFile.getMovieBox().getMovieHeaderBox().getDuration() /
                isoFile.getMovieBox().getMovieHeaderBox().getTimescale();
        Track mp4Track = mp4Vid.getTracks().get(0);
        Track audioTrack = new AACTrackImpl(new FileDataSourceImpl(audioSource));

        double startTime1 = 0;
        double endTime1 = lengthInSeconds;

        if (audioTrack.getSyncSamples() != null && audioTrack.getSyncSamples().length > 0) {
            startTime1 = correctTimeToSyncSample(audioTrack, startTime1, false);
            endTime1 = correctTimeToSyncSample(audioTrack, endTime1, true);
        }

        long currentSample = 0;
        double currentTime = 0;
        double lastTime = -1;
        long startSample1 = -1;
        long endSample1 = -1;


        for (int i = 0; i < audioTrack.getSampleDurations().length; i++) {
            long delta = audioTrack.getSampleDurations()[i];


            if (currentTime > lastTime && currentTime <= startTime1) {
                /** current sample is still before the new start time **/
                startSample1 = currentSample;
            }
            if (currentTime > lastTime && currentTime <= endTime1) {
                /** current sample is after the new start time and still before the new end time **/
                endSample1 = currentSample;
            }

            lastTime = currentTime;
            currentTime += (double) delta / (double) audioTrack.getTrackMetaData().getTimescale();
            currentSample++;
        }

        CroppedTrack cropperAacTrack = new CroppedTrack(audioTrack, startSample1, endSample1);

        Movie movie = new Movie();

        movie.addTrack(mp4Track);
        movie.addTrack(cropperAacTrack);

        Container mp4file = new DefaultMp4Builder().build(movie);

        FileChannel fc = new FileOutputStream(output).getChannel();
        mp4file.writeContainer(fc);
        fc.close();

        return output.getAbsolutePath();
    }

    /**
     * find the sample of audio according to time
     *
     * @param track   audio track
     * @param cutHere time to find sample
     * @param next    check if get the previous sample or present sample
     * @return time to cut
     */
    private static double correctTimeToSyncSample(Track track, double cutHere, boolean next) {
        double[] timeOfSyncSamples = new double[track.getSyncSamples().length];
        long currentSample = 0;
        double currentTime = 0;
        for (int i = 0; i < track.getSampleDurations().length; i++) {
            long delta = track.getSampleDurations()[i];
            int samplePosition = Arrays.binarySearch(track.getSyncSamples(), currentSample + 1);
            if (samplePosition >= 0) {
                /** samples always start with 1 but we start with zero therefore +1 **/
                timeOfSyncSamples[samplePosition] = currentTime;
            }
            currentTime += (double) delta / (double) track.getTrackMetaData().getTimescale();
            currentSample++;

        }
        double previous = 0;
        for (double timeOfSyncSample : timeOfSyncSamples) {
            if (timeOfSyncSample > cutHere) {
                if (next) {
                    return timeOfSyncSample;
                } else {
                    return previous;
                }
            }
            previous = timeOfSyncSample;
        }
        return timeOfSyncSamples[timeOfSyncSamples.length - 1];
    }

    public interface UpdateProgress {
        void update(int percent);
    }

    public void generateRandTransition() {
        mArrTransition = new int[mNumImage];
        Random random = new Random();
        for (int i = 0; i < mArrTransition.length; i++) {
            mArrTransition[i] = random.nextInt(5);
        }
    }
}
