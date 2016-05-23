package com.framgia.photoalbum.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.framgia.photoalbum.BuildConfig;
import com.framgia.photoalbum.R;
import com.framgia.photoalbum.ui.activity.CollageActivity;
import com.framgia.photoalbum.ui.custom.PartImageView;

/**
 * Created by dinhduc on 13/05/2016.
 */
public class CollageUtils {
    private static final String TAG = "CollageUtils";
    private static RelativeLayout sRootView;
    private static Activity sActivity;
    private static PartImageView[] sPartImageViews;
    private static int mMargin;
    private static PickImageToMergeListener sListener;

    private static void setUp(RelativeLayout rootView, Activity activity, PartImageView[] partImageViews) {
        sRootView = rootView;
        sActivity = activity;
        sPartImageViews = partImageViews;
        mMargin = (int) sActivity.getResources().getDimension(R.dimen.activity_horizontal_margin) / 2;
        if (sActivity instanceof PickImageToMergeListener) {
            sListener = (PickImageToMergeListener) sActivity;
        }
    }

    private static void initPartImageView(final int position) {
        sPartImageViews[position] = new PartImageView(sActivity);
        sPartImageViews[position].setScaleType(ImageView.ScaleType.MATRIX);
//        sPartImageViews[position].setImageResource(R.drawable.collage_hint);
        sPartImageViews[position].post(new Runnable() {
            @Override
            public void run() {
                if (sActivity instanceof CollageActivity) {
                    sPartImageViews[position].setImageBitmap(((CollageActivity) sActivity).mImageBitmaps[position]);
                }
            }
        });
        sPartImageViews[position].setId(position + 1);
    }

    public static void createLayout_2_1(RelativeLayout rootView, Activity activity, PartImageView[] partImageViews) {
        setUp(rootView, activity, partImageViews);
        rootView.post(new Runnable() {
            @Override
            public void run() {
                int mViewWidth = sRootView.getWidth();
                int mViewHeight = sRootView.getHeight();
                if (BuildConfig.DEBUG) {
                    Log.w(TAG, mViewWidth + "-" + mViewHeight);
                }
                for (int i = 0; i < sPartImageViews.length; i++) {
                    initPartImageView(i);
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                            (mViewWidth - (sPartImageViews.length + 1) * mMargin) / sPartImageViews.length,
                            mViewHeight
                    );
                    if (i > 0) {
                        layoutParams.addRule(RelativeLayout.RIGHT_OF, i);
                        layoutParams.setMargins(mMargin, mMargin, mMargin, mMargin);
                    } else {
                        layoutParams.setMargins(mMargin, mMargin, 0, mMargin);
                    }

                    sPartImageViews[i].setLayoutParams(layoutParams);
                    sPartImageViews[i].setBackgroundColor(
                            ContextCompat.getColor(
                                    sActivity,
                                    R.color.colorBackground
                            )
                    );
                    sRootView.addView(sPartImageViews[i]);

                    final int finalI = i;
                    sPartImageViews[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            sListener.onPick(finalI);
                        }
                    });
                }
            }
        });
    }

    public static void createLayout_2_2(RelativeLayout rootView, Activity activity, PartImageView[] partImageViews) {
        setUp(rootView, activity, partImageViews);
        sRootView.post(new Runnable() {
            @Override
            public void run() {
                int mViewWidth = sRootView.getWidth();
                int mViewHeight = sRootView.getHeight();
                if (BuildConfig.DEBUG) {
                    Log.w(TAG, mViewWidth + "-" + mViewHeight);
                }
                for (int i = 0; i < sPartImageViews.length; i++) {
                    initPartImageView(i);

                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                            mViewWidth,
                            (mViewHeight - (sPartImageViews.length + 1) * mMargin) / sPartImageViews.length
                    );
                    if (i > 0) {
                        layoutParams.addRule(RelativeLayout.BELOW, i);
                        layoutParams.setMargins(mMargin, mMargin, mMargin, mMargin);
                    } else {
                        layoutParams.setMargins(mMargin, mMargin, mMargin, 0);
                    }

                    sPartImageViews[i].setLayoutParams(layoutParams);
                    sPartImageViews[i].setBackgroundColor(
                            ContextCompat.getColor(
                                    sActivity,
                                    R.color.colorBackground
                            )
                    );
                    sRootView.addView(sPartImageViews[i]);

                    final int finalI = i;
                    sPartImageViews[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            sListener.onPick(finalI);
                        }
                    });
                }
            }
        });
    }

    public static void createLayout_2_3(RelativeLayout rootView, Activity activity, PartImageView[] partImageViews) {
        setUp(rootView, activity, partImageViews);
        sRootView.post(new Runnable() {
            @Override
            public void run() {
                int mViewWidth = sRootView.getWidth();
                int mViewHeight = sRootView.getHeight();
                if (BuildConfig.DEBUG) {
                    Log.w(TAG, mViewWidth + "-" + mViewHeight);
                }
                for (int i = 0; i < sPartImageViews.length; i++) {
                    initPartImageView(i);

                    RelativeLayout.LayoutParams layoutParams;
                    if (i > 0) {
                        layoutParams = new RelativeLayout.LayoutParams(
                                mViewWidth,
                                (mViewHeight - (sPartImageViews.length + 1) * mMargin) / 4 * 3
                        );
                        layoutParams.addRule(RelativeLayout.BELOW, i);
                        layoutParams.setMargins(mMargin, mMargin, mMargin, mMargin);
                    } else {
                        layoutParams = new RelativeLayout.LayoutParams(
                                mViewWidth,
                                (mViewHeight - (sPartImageViews.length + 1) * mMargin) / 4
                        );
                        layoutParams.setMargins(mMargin, mMargin, mMargin, 0);
                    }

                    sPartImageViews[i].setLayoutParams(layoutParams);
                    sPartImageViews[i].setBackgroundColor(
                            ContextCompat.getColor(
                                    sActivity,
                                    R.color.colorBackground
                            )
                    );
                    sRootView.addView(sPartImageViews[i]);

                    final int finalI = i;
                    sPartImageViews[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            sListener.onPick(finalI);
                        }
                    });
                }
            }
        });

    }

    public static void createLayout_3_1(RelativeLayout rootView, Activity activity, PartImageView[] partImageViews) {
        setUp(rootView, activity, partImageViews);
        sRootView.post(new Runnable() {
            @Override
            public void run() {
                int mViewWidth = sRootView.getWidth();
                int mViewHeight = sRootView.getHeight();
                if (BuildConfig.DEBUG) {
                    Log.w(TAG, mViewWidth + "-" + mViewHeight);
                }
                for (int i = 0; i < sPartImageViews.length; i++) {
                    initPartImageView(i);
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                            mViewWidth,
                            (mViewHeight - mMargin * (sPartImageViews.length + 1)) / sPartImageViews.length
                    );
                    if (i > 0) {
                        layoutParams.addRule(RelativeLayout.BELOW, i);
                        layoutParams.setMargins(mMargin, 0, mMargin, mMargin);
                    } else {
                        layoutParams.setMargins(mMargin, mMargin, mMargin, mMargin);
                    }

                    sPartImageViews[i].setLayoutParams(layoutParams);
                    sPartImageViews[i].setBackgroundColor(
                            ContextCompat.getColor(
                                    sActivity,
                                    R.color.colorBackground
                            )
                    );
                    sRootView.addView(sPartImageViews[i]);

                    final int finalI = i;
                    sPartImageViews[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            sListener.onPick(finalI);
                        }
                    });
                }
            }
        });
    }

    public static void createLayout_3_2(RelativeLayout rootView, Activity activity, PartImageView[] partImageViews) {
        setUp(rootView, activity, partImageViews);
        sRootView.post(new Runnable() {
            @Override
            public void run() {
                int mViewWidth = sRootView.getWidth();
                int mViewHeight = sRootView.getHeight();
                if (BuildConfig.DEBUG) {
                    Log.w(TAG, mViewWidth + "-" + mViewHeight);
                }
                for (int i = 0; i < sPartImageViews.length; i++) {
                    initPartImageView(i);
                    RelativeLayout.LayoutParams layoutParams;
                    switch (i) {
                        case 0:
                            layoutParams = new RelativeLayout.LayoutParams(
                                    (mViewWidth - 3 * mMargin) / 2,
                                    (mViewHeight - 3 * mMargin) / 4 * 3
                            );
                            layoutParams.setMargins(mMargin, mMargin, 0, 0);
                            break;
                        case 1:
                            layoutParams = new RelativeLayout.LayoutParams(
                                    (mViewWidth - 3 * mMargin) / 2,
                                    (mViewHeight - 3 * mMargin) / 4
                            );
                            layoutParams.addRule(RelativeLayout.BELOW, 1);
                            layoutParams.setMargins(mMargin, mMargin, 0, mMargin);
                            break;
                        case 2:
                            layoutParams = new RelativeLayout.LayoutParams(
                                    (mViewWidth - 3 * mMargin) / 2,
                                    mViewHeight
                            );
                            layoutParams.addRule(RelativeLayout.RIGHT_OF, 2);
                            layoutParams.setMargins(mMargin, mMargin, mMargin, mMargin);
                            break;
                        default:
                            layoutParams = new RelativeLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                            );
                    }
                    sPartImageViews[i].setLayoutParams(layoutParams);
                    sPartImageViews[i].setBackgroundColor(
                            ContextCompat.getColor(
                                    sActivity,
                                    R.color.colorBackground
                            )
                    );
                    sRootView.addView(sPartImageViews[i]);

                    final int finalI = i;
                    sPartImageViews[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            sListener.onPick(finalI);
                        }
                    });
                }
            }
        });
    }

    public static void createLayout_4_1(RelativeLayout rootView, Activity activity, PartImageView[] partImageViews) {
        setUp(rootView, activity, partImageViews);
        sRootView.post(new Runnable() {
            @Override
            public void run() {
                int mViewWidth = sRootView.getWidth();
                int mViewHeight = sRootView.getHeight();
                if (BuildConfig.DEBUG) {
                    Log.w(TAG, mViewWidth + "-" + mViewHeight);
                }
                for (int i = 0; i < sPartImageViews.length; i++) {
                    initPartImageView(i);
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                            (mViewWidth - 3 * mMargin) / 2,
                            (mViewHeight - 3 * mMargin) / 2
                    );
                    switch (i) {
                        case 1:
                            layoutParams.addRule(RelativeLayout.BELOW, 1);
                            layoutParams.setMargins(mMargin, mMargin, 0, mMargin);
                            break;
                        case 2:
                            layoutParams.addRule(RelativeLayout.RIGHT_OF, 2);
                            layoutParams.setMargins(mMargin, mMargin, mMargin, 0);
                            break;
                        case 3:
                            layoutParams.addRule(RelativeLayout.BELOW, 3);
                            layoutParams.addRule(RelativeLayout.RIGHT_OF, 2);
                            layoutParams.setMargins(mMargin, mMargin, mMargin, mMargin);
                            break;
                        default:
                            layoutParams.setMargins(mMargin, mMargin, 0, 0);
                    }
                    sPartImageViews[i].setLayoutParams(layoutParams);
                    sPartImageViews[i].setBackgroundColor(
                            ContextCompat.getColor(
                                    sActivity,
                                    R.color.colorBackground
                            )
                    );
                    sRootView.addView(sPartImageViews[i]);

                    final int finalI = i;
                    sPartImageViews[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            sListener.onPick(finalI);
                        }
                    });
                }
            }
        });
    }

    public interface PickImageToMergeListener {
        void onPick(int position);
    }

    public static void releaseMemory(Bitmap[] bitmaps) {
        for (Bitmap bitmap : bitmaps) {
            if (bitmap != null) {
                bitmap.recycle();
            }
        }
    }

    public static boolean isAllChose(Bitmap[] bitmaps, int numberView) {
        for (int i = 0; i < numberView; i++) {
            if (bitmaps[i] == null) {
                return false;
            }
        }
        return true;
    }
}
