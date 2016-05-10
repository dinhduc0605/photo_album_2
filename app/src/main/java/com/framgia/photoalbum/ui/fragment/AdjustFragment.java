package com.framgia.photoalbum.ui.fragment;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.framgia.photoalbum.BuildConfig;
import com.framgia.photoalbum.R;
import com.framgia.photoalbum.data.model.AdjustItem;
import com.framgia.photoalbum.ui.activity.EditActivity;
import com.framgia.photoalbum.ui.adapter.AdjustFeatureAdapter;
import com.framgia.photoalbum.util.CommonUtils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdjustFragment extends EditFragment {
    public static final int SEEK_BAR_LIGHT = 0;
    public static final int SEEK_BAR_CONTRAST = 1;
    public static final int SEEK_BAR_HUE = 2;
    private static final String TAG = "AdjustFragment";
    @Bind(R.id.adjustImage)
    ImageView mImageView;
    @Bind(R.id.listAdjustFeature)
    RecyclerView adjustFeatureList;
    @Bind(R.id.adjustSeekBar)
    SeekBar adjustSeekBar;
    @Bind(R.id.btnClear)
    ImageView mButtonClear;
    @Bind(R.id.btnDone)
    ImageView mButtonDone;
    @Bind(R.id.layoutAdjust)
    LinearLayout layoutAdjust;
    @Bind(R.id.layoutAdjustBar)
    FrameLayout layoutAdjustBar;

    AdjustFeatureAdapter adapter;
    ArrayList<AdjustItem> mAdjustItems = new ArrayList<>();
    Bitmap mImageBitmapSrc, mImageBitmapDes;
    int mPosition;


    public AdjustFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_adjust, container, false);
        ButterKnife.bind(this, view);
        mImageBitmapSrc = EditActivity.imageBitmap;
        mImageBitmapDes = Bitmap.createBitmap(
                mImageBitmapSrc.getWidth(),
                mImageBitmapSrc.getHeight(),
                mImageBitmapSrc.getConfig()
        );
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
        mImageView.setImageBitmap(mImageBitmapSrc);
        adapter = new AdjustFeatureAdapter(getActivity(), mAdjustItems, new AdjustFeatureAdapter.OnEditListener() {
            @Override
            public void onEdit(int position) {
                mPosition = position;
                adjustFeatureList.setVisibility(View.INVISIBLE);
                layoutAdjust.setVisibility(View.VISIBLE);
                switch (position) {
                    case SEEK_BAR_LIGHT:
                        adjustSeekBar.setMax(510);
                        adjustSeekBar.setProgress(255);
                        break;
                    case SEEK_BAR_CONTRAST:
                        adjustSeekBar.setMax(100);
                        adjustSeekBar.setProgress(50);
                        break;
                    case SEEK_BAR_HUE:
                        adjustSeekBar.setMax(360);
                        adjustSeekBar.setProgress(180);
                        break;
                }
            }
        });

        adjustSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                switch (mPosition) {
                    case SEEK_BAR_LIGHT:
                        CommonUtils.changeBitmapContrastBrightness(mImageBitmapSrc, mImageBitmapDes, 1, i - 255);
                        mImageView.setImageBitmap(mImageBitmapDes);
                        break;
                    case SEEK_BAR_CONTRAST:
                        float progress;
                        if (i < 50) {
                            progress = i / 50f;
                        } else {
                            progress = 1 + (i - 50) / 50f * 9;
                        }
                        CommonUtils.changeBitmapContrastBrightness(mImageBitmapSrc, mImageBitmapDes, progress, 0);
                        mImageView.setImageBitmap(mImageBitmapDes);
                        break;
                    case SEEK_BAR_HUE:
                        if (BuildConfig.DEBUG) {
                            Log.w(TAG, "" + i);
                        }
                        CommonUtils.adjustHue(mImageBitmapSrc, mImageBitmapDes, i - 180);
                        mImageView.setImageBitmap(mImageBitmapDes);
                        break;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        adjustFeatureList.setLayoutManager(
                new LinearLayoutManager(getContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false));
        adjustFeatureList.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        if (getActivity() instanceof EditActivity) {
            ((EditActivity) getActivity()).clearFragment();
        }
    }

    @Override
    public void apply() {
        EditActivity.imageBitmap = mImageBitmapDes;
    }


    private void initData() {
        mAdjustItems.add(new AdjustItem(R.drawable.icon_adjust_light, getString(R.string.adjust_light)));
        mAdjustItems.add(new AdjustItem(R.drawable.icon_adjust_contrast, getString(R.string.adjust_contrast)));
        mAdjustItems.add(new AdjustItem(R.drawable.icon_adjust_hue, getString(R.string.adjust_hue)));
    }

    @OnClick({R.id.btnClear, R.id.btnDone})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnClear:
                mImageView.setImageBitmap(mImageBitmapSrc);
                adjustFeatureList.setVisibility(View.VISIBLE);
                layoutAdjust.setVisibility(View.INVISIBLE);
                break;
            case R.id.btnDone:
                adjustFeatureList.setVisibility(View.VISIBLE);
                layoutAdjust.setVisibility(View.INVISIBLE);
                break;
        }
    }
}
