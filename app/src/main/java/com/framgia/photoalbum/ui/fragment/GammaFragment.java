package com.framgia.photoalbum.ui.fragment;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.framgia.photoalbum.R;
import com.framgia.photoalbum.asynctask.EffectApplyAsyncTask;
import com.framgia.photoalbum.effect.Gamma;
import com.framgia.photoalbum.ui.activity.EditActivity;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by HungNT on 5/5/16.
 */
public class GammaFragment extends EditFragment implements EffectApplyAsyncTask.OnApplyListener, SeekBar.OnSeekBarChangeListener {

    @Bind(R.id.imageEdit)
    ImageView imageDisplayed;
    @Bind(R.id.seekbarRed)
    SeekBar mSeekbarRed;

    private ProgressDialog mProcessDialog;
    private Gamma mGammaEffect = new Gamma();
    private EffectApplyAsyncTask mProgressTask;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gamma, container, false);
        ButterKnife.bind(this, view);

        initComponent();

        return view;
    }

    private void initComponent() {
        mEditBitmap = EditActivity.sSourceBitmap;
        imageDisplayed.setImageBitmap(mEditBitmap);

        mProcessDialog = new ProgressDialog(getContext());
        mProcessDialog.setMessage(getContext().getString(R.string.loading));
        mProcessDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProcessDialog.setIndeterminate(true);
        mProcessDialog.setCancelable(false);

        mSeekbarRed.setOnSeekBarChangeListener(this);

    }

    public static GammaFragment newInstance(String path) {
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_IMAGE_PATH, path);

        GammaFragment fragment = new GammaFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void apply() {
        // TODO apply effect selected and save in cache
    }


    public void applyEffect() {
        int val = 100 - mSeekbarRed.getProgress();
        mGammaEffect.setValue(val);
        mProgressTask = new EffectApplyAsyncTask(mEditBitmap, mGammaEffect, mProcessDialog, this);
        mProgressTask.execute();
    }

    @Override
    public void onResult(Bitmap bitmap) {
        EditActivity.setResultBitmap(bitmap);
        imageDisplayed.setImageBitmap(bitmap);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mProgressTask != null && !mProgressTask.isCancelled()) {
            mProgressTask.cancel(true);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        applyEffect();
    }
}
