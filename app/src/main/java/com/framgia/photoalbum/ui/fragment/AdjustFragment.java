package com.framgia.photoalbum.ui.fragment;


import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.framgia.photoalbum.R;
import com.framgia.photoalbum.data.model.AdjustItem;
import com.framgia.photoalbum.ui.activity.EditActivity;
import com.framgia.photoalbum.ui.adapter.AdjustFeatureAdapter;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.framgia.photoalbum.util.CommonUtils.editContrastHueLight;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdjustFragment extends EditFragment implements AdjustFeatureAdapter.OnEditListener {
    public static final int DEFAULT_PROGRESS = 50;

    public static final int SEEK_BAR_LIGHT = 0;
    public static final int SEEK_BAR_CONTRAST = 1;
    public static final int SEEK_BAR_HUE = 2;
    private static final String TAG = "AdjustFragment";
    private AdjustFeatureAdapter mAdapter;
    private ArrayList<AdjustItem> mAdjustItems = new ArrayList<>();
    private Bitmap mImageBitmapSrc, mImageBitmapDes;
    private int mPosition;
    private int mCurrentLight, mCurrentContrast, mCurrentHue;
    private AnimatorSet mSlideDownAnim;
    private AnimatorSet mSlideUpAnim;

    @Bind(R.id.adjustImage)
    ImageView mImageView;
    @Bind(R.id.listAdjustFeature)
    RecyclerView adjustFeatureList;
    @Bind(R.id.adjustSeekBar)
    SeekBar adjustSeekBar;
    @Bind(R.id.btnCancel)
    ImageView mButtonClear;
    @Bind(R.id.btnSave)
    ImageView mButtonDone;
    @Bind(R.id.layoutAdjust)
    LinearLayout layoutAdjust;

//    @Bind(R.id.layoutAdjustBar)
//    FrameLayout layoutAdjustBar;

    public AdjustFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_adjust, container, false);
        ButterKnife.bind(this, view);
        mImageBitmapSrc = EditActivity.sSourceBitmap;
        mImageBitmapDes = Bitmap.createBitmap(
                mImageBitmapSrc.getWidth(),
                mImageBitmapSrc.getHeight(),
                mImageBitmapSrc.getConfig()
        );
        mSlideDownAnim = (AnimatorSet) AnimatorInflater.loadAnimator(
                getContext(),
                R.animator.slide_down_animator
        );
        mSlideUpAnim = (AnimatorSet) AnimatorInflater.loadAnimator(
                getContext(),
                R.animator.slide_up_animator
        );
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
        mImageView.setImageBitmap(mImageBitmapSrc);
        mAdapter = new AdjustFeatureAdapter(getContext(), mAdjustItems, this);

        adjustSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                switch (mPosition) {
                    case SEEK_BAR_LIGHT:
                        mCurrentLight = progress;
                        break;
                    case SEEK_BAR_CONTRAST:
                        mCurrentContrast = progress;
                        break;
                    case SEEK_BAR_HUE:
                        mCurrentHue = progress;
                        break;
                }
                editContrastHueLight(
                        mImageBitmapSrc,
                        mImageBitmapDes,
                        mCurrentHue,
                        mCurrentLight,
                        mCurrentContrast
                );
                mImageView.setImageBitmap(mImageBitmapDes);
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
        adjustFeatureList.setAdapter(mAdapter);
    }

    @Override
    public void apply() {
        EditActivity.setResultBitmap(mImageBitmapDes);
    }


    @Override
    public void onEdit(int position) {
        mPosition = position;
        showSeekBar(true);
        switch (position) {
            case SEEK_BAR_LIGHT:
                adjustSeekBar.setProgress(mCurrentLight);
                break;
            case SEEK_BAR_CONTRAST:
                adjustSeekBar.setProgress(mCurrentContrast);
                break;
            case SEEK_BAR_HUE:
                adjustSeekBar.setProgress(mCurrentHue);
                break;
        }
    }

    @OnClick({R.id.btnCancel, R.id.btnSave})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCancel:
                showSeekBar(false);
                switch (mPosition) {
                    case SEEK_BAR_LIGHT:
                        mCurrentLight = DEFAULT_PROGRESS;
                        break;
                    case SEEK_BAR_CONTRAST:
                        mCurrentContrast = DEFAULT_PROGRESS;
                        break;
                    case SEEK_BAR_HUE:
                        mCurrentHue = DEFAULT_PROGRESS;
                        break;
                }
                adjustSeekBar.setProgress(mCurrentLight);
                break;
            case R.id.btnSave:
                showSeekBar(false);
                break;
        }
    }

    private void initData() {
        mAdjustItems.add(new AdjustItem(R.drawable.icon_adjust_light, getString(R.string.adjust_light)));
        mAdjustItems.add(new AdjustItem(R.drawable.icon_adjust_contrast, getString(R.string.adjust_contrast)));
        mAdjustItems.add(new AdjustItem(R.drawable.icon_adjust_hue, getString(R.string.adjust_hue)));
        mCurrentContrast = mCurrentHue = mCurrentLight = DEFAULT_PROGRESS;
    }

    private void showSeekBar(boolean isShow) {
        mSlideDownAnim.setTarget(isShow ? adjustFeatureList : layoutAdjust);
        mSlideDownAnim.start();
        mSlideUpAnim.setTarget(isShow ? layoutAdjust : adjustFeatureList);
        mSlideUpAnim.start();
    }

}
