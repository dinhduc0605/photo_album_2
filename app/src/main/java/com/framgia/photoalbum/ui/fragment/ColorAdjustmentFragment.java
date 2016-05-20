package com.framgia.photoalbum.ui.fragment;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.framgia.photoalbum.R;
import com.framgia.photoalbum.data.model.FeatureItem;
import com.framgia.photoalbum.effect.ColorAdjustPerform;
import com.framgia.photoalbum.ui.activity.EditActivity;
import com.framgia.photoalbum.ui.adapter.ListFeatureAdapter;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by HungNT on 5/8/16.
 */
public class ColorAdjustmentFragment extends EditFragment implements
        ListFeatureAdapter.OnFeatureClicked {
    private static final int OFFSET = 127;

    @Bind(R.id.imageEdit)
    ImageView imageDisplayed;
    @Bind(R.id.listEffect)
    RecyclerView mRecyclerViewEffect;
    @Bind(R.id.seekbarValue)
    SeekBar mSeekbarValue;
    @Bind(R.id.layoutAdjust)
    View mLayoutAdjust;

    private ProgressDialog mProcessDialog;
    private ArrayList<FeatureItem> mFeatureItems = new ArrayList<>();
    private ListFeatureAdapter mAdapter;
    private ColorAdjustPerform mColorAdjustPerform;
    private AnimatorSet mSlideDownAnim;
    private AnimatorSet mSlideUpAnim;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_color, container, false);
        ButterKnife.bind(this, view);

        if (getArguments() != null) {
            String path = getArguments().getString(BUNDLE_IMAGE_PATH);
            mSourceUri = Uri.fromFile(new File(path));
        }

        initComponent();

        return view;
    }

    @Override
    public void apply() {
        // TODO apply the effect and save to cache
    }

    @Override
    public void onClick(View v, int position) {
        showSeekbar(true);
        mColorAdjustPerform.setType(position);
        mSeekbarValue.setProgress(mColorAdjustPerform.getValue(position) + OFFSET);
    }

    public static ColorAdjustmentFragment newInstance(String path) {
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_IMAGE_PATH, path);

        ColorAdjustmentFragment fragment = new ColorAdjustmentFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    public void showSeekbar(boolean isShow) {
        mSlideDownAnim.setTarget(isShow ? mRecyclerViewEffect : mLayoutAdjust);
        mSlideDownAnim.start();
        mSlideUpAnim.setTarget(isShow ? mLayoutAdjust : mRecyclerViewEffect);
        mSlideUpAnim.start();
    }

    @OnClick({R.id.btnClear, R.id.btnDone})
    public void onClick(View view) {
        if (view.getId() == R.id.btnClear) {
            Bitmap res = mColorAdjustPerform.applyEffect(mEditBitmap, 0);
            EditActivity.setResultBitmap(res);
            imageDisplayed.setImageBitmap(EditActivity.sResultBitmap);
        }
        showSeekbar(false);
    }

    private void initComponent() {
        mSlideDownAnim = (AnimatorSet) AnimatorInflater.loadAnimator(
                getContext(),
                R.animator.slide_down_animator
        );
        mSlideUpAnim = (AnimatorSet) AnimatorInflater.loadAnimator(
                getContext(),
                R.animator.slide_up_animator
        );

        imageDisplayed.setImageBitmap(mEditBitmap);

        // Config dialog
        mProcessDialog = new ProgressDialog(getContext());
        mProcessDialog.setMessage(getContext().getString(R.string.loading));
        mProcessDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProcessDialog.setIndeterminate(true);

        // Config adapter & RecyclerView
        mFeatureItems.add(new FeatureItem(R.drawable.ic_color_adjust_red,
                getContext().getString(R.string.red)));
        mFeatureItems.add(new FeatureItem(R.drawable.ic_color_adjust_blue,
                getContext().getString(R.string.blue)));
        mFeatureItems.add(new FeatureItem(R.drawable.ic_color_adjust_green,
                getContext().getString(R.string.green)));

        mAdapter = new ListFeatureAdapter(getContext(), mFeatureItems, this);
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        mRecyclerViewEffect.setLayoutManager(layoutManager);
        mRecyclerViewEffect.setAdapter(mAdapter);

        mColorAdjustPerform = new ColorAdjustPerform();

        // Config seekbar
        mSeekbarValue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Bitmap res = mColorAdjustPerform.applyEffect(mEditBitmap, seekBar.getProgress() - OFFSET);
                EditActivity.setResultBitmap(res);
                imageDisplayed.setImageBitmap(EditActivity.sResultBitmap);
            }
        });
    }
}
