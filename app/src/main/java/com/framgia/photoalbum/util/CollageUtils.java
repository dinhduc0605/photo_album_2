package com.framgia.photoalbum.util;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.framgia.photoalbum.BuildConfig;
import com.framgia.photoalbum.R;
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

    private static void init(RelativeLayout rootView, Activity activity, PartImageView[] partImageViews) {
        sRootView = rootView;
        sActivity = activity;
        sPartImageViews = partImageViews;
        mMargin = (int) sActivity.getResources().getDimension(R.dimen.activity_horizontal_margin) / 2;
        if (sActivity instanceof PickImageToMergeListener) {
            sListener = (PickImageToMergeListener) sActivity;
        }
    }

    public static void createLayout_2_1(RelativeLayout rootView, Activity activity, PartImageView[] partImageViews) {
        init(rootView, activity, partImageViews);
        rootView.post(new Runnable() {
            @Override
            public void run() {
                int mViewWidth = sRootView.getWidth();
                int mViewHeight = sRootView.getHeight();
                if (BuildConfig.DEBUG) {
                    Log.w(TAG, mViewWidth + "-" + mViewHeight);
                }
                for (int i = 0; i < sPartImageViews.length; i++) {
                    sPartImageViews[i] = new PartImageView(sActivity);
                    sPartImageViews[i].setId(i + 1);

                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                            mViewWidth / sPartImageViews.length - sPartImageViews.length * mMargin,
                            mViewHeight
                    );
                    if (i > 0) {
                        layoutParams.addRule(RelativeLayout.RIGHT_OF, i);
                    }
                    layoutParams.setMargins(mMargin, mMargin, mMargin, mMargin);
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
        init(rootView, activity, partImageViews);
        sRootView.post(new Runnable() {
            @Override
            public void run() {
                int mViewWidth = sRootView.getWidth();
                int mViewHeight = sRootView.getHeight();
                if (BuildConfig.DEBUG) {
                    Log.w(TAG, mViewWidth + "-" + mViewHeight);
                }
                for (int i = 0; i < sPartImageViews.length; i++) {
                    sPartImageViews[i] = new PartImageView(sActivity);
                    sPartImageViews[i].setId(i + 1);

                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                            mViewWidth,
                            mViewHeight / sPartImageViews.length - sPartImageViews.length * mMargin
                    );
                    if (i > 0) {
                        layoutParams.addRule(RelativeLayout.BELOW, i);
                    }
                    layoutParams.setMargins(mMargin, mMargin, mMargin, mMargin);
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
        init(rootView, activity, partImageViews);
        sRootView.post(new Runnable() {
            @Override
            public void run() {
                int mViewWidth = sRootView.getWidth();
                int mViewHeight = sRootView.getHeight();
                if (BuildConfig.DEBUG) {
                    Log.w(TAG, mViewWidth + "-" + mViewHeight);
                }
                for (int i = 0; i < sPartImageViews.length; i++) {
                    sPartImageViews[i] = new PartImageView(sActivity);
                    sPartImageViews[i].setId(i + 1);

                    RelativeLayout.LayoutParams layoutParams;
                    if (i > 0) {
                        layoutParams = new RelativeLayout.LayoutParams(
                                mViewWidth,
                                mViewHeight / 4 * 3 - sPartImageViews.length * mMargin
                        );
                        layoutParams.addRule(RelativeLayout.BELOW, i);
                    } else {
                        layoutParams = new RelativeLayout.LayoutParams(
                                mViewWidth,
                                mViewHeight / 4 - sPartImageViews.length * mMargin
                        );
                    }
                    layoutParams.setMargins(mMargin, mMargin, mMargin, mMargin);
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
        init(rootView, activity, partImageViews);
        sRootView.post(new Runnable() {
            @Override
            public void run() {
                int mViewWidth = sRootView.getWidth();
                int mViewHeight = sRootView.getHeight();
                if (BuildConfig.DEBUG) {
                    Log.w(TAG, mViewWidth + "-" + mViewHeight);
                }
                for (int i = 0; i < sPartImageViews.length; i++) {
                    sPartImageViews[i] = new PartImageView(sActivity);
                    sPartImageViews[i].setId(i + 1);
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                            mViewWidth,
                            mViewHeight / sPartImageViews.length - mMargin * 2
                    );
                    if (i > 0) {
                        layoutParams.addRule(RelativeLayout.BELOW, i);
                    }
                    layoutParams.setMargins(mMargin, mMargin, mMargin, mMargin);
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
        init(rootView, activity, partImageViews);
        sRootView.post(new Runnable() {
            @Override
            public void run() {
                int mViewWidth = sRootView.getWidth();
                int mViewHeight = sRootView.getHeight();
                if (BuildConfig.DEBUG) {
                    Log.w(TAG, mViewWidth + "-" + mViewHeight);
                }
                sPartImageViews = new PartImageView[3];
                for (int i = 0; i < sPartImageViews.length; i++) {
                    sPartImageViews[i] = new PartImageView(sActivity);
                    sPartImageViews[i].setId(i + 1);
                    RelativeLayout.LayoutParams layoutParams;
                    switch (i) {
                        case 0:
                            layoutParams = new RelativeLayout.LayoutParams(
                                    mViewWidth / 2 - mMargin * 2,
                                    mViewHeight / 4 * 3 - mMargin * 2
                            );
                            break;
                        case 1:
                            layoutParams = new RelativeLayout.LayoutParams(
                                    mViewWidth / 2 - mMargin * 2,
                                    mViewHeight / 4 - mMargin * 2
                            );
                            layoutParams.addRule(RelativeLayout.BELOW, 1);
                            break;
                        case 2:
                            layoutParams = new RelativeLayout.LayoutParams(
                                    mViewWidth / 2 - mMargin * 2,
                                    mViewHeight
                            );
                            layoutParams.addRule(RelativeLayout.RIGHT_OF, 2);
                            break;
                        default:
                            layoutParams = new RelativeLayout.LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT
                            );
                    }
                    layoutParams.setMargins(mMargin, mMargin, mMargin, mMargin);
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
        init(rootView, activity, partImageViews);
        sRootView.post(new Runnable() {
            @Override
            public void run() {
                int mViewWidth = sRootView.getWidth();
                int mViewHeight = sRootView.getHeight();
                if (BuildConfig.DEBUG) {
                    Log.w(TAG, mViewWidth + "-" + mViewHeight);
                }
                for (int i = 0; i < sPartImageViews.length; i++) {
                    sPartImageViews[i] = new PartImageView(sActivity);
                    sPartImageViews[i].setId(i + 1);
                    RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                            mViewWidth / 2 - mMargin * 2,
                            mViewHeight / 2 - mMargin * 2
                    );
                    switch (i) {
                        case 1:
                            layoutParams.addRule(RelativeLayout.BELOW, 1);
                            break;
                        case 2:
                            layoutParams.addRule(RelativeLayout.RIGHT_OF, 2);
                            break;
                        case 3:
                            layoutParams.addRule(RelativeLayout.BELOW, 3);
                            layoutParams.addRule(RelativeLayout.RIGHT_OF, 2);
                            break;
                    }
                    layoutParams.setMargins(mMargin, mMargin, mMargin, mMargin);
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
}
