package com.framgia.photoalbum.ui.fragment;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.Uri;
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

import java.io.File;

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
    @Bind(R.id.seekbarGreen)
    SeekBar mSeekbarGreen;
    @Bind(R.id.seekbarBlue)
    SeekBar mSeekbarBlue;

    private Bitmap mEditBitmap;
    private ProgressDialog mProcessDialog;
    private Gamma mGammaEffect = new Gamma();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gamma, container, false);
        ButterKnife.bind(this, view);

        if (getArguments() != null) {
            String path = getArguments().getString(BUNDLE_IMAGE_PATH);
            mSourceUri = Uri.fromFile(new File(path));
        }

        initComponent();

        return view;
    }

    private void initComponent() {
        mEditBitmap = EditActivity.imageBitmap;
        imageDisplayed.setImageBitmap(mEditBitmap);

        mProcessDialog = new ProgressDialog(getContext());
        mProcessDialog.setMessage(getContext().getString(R.string.loading));
        mProcessDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProcessDialog.setIndeterminate(true);

        mSeekbarBlue.setOnSeekBarChangeListener(this);
        mSeekbarGreen.setOnSeekBarChangeListener(this);
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
        mGammaEffect.setValue(mSeekbarRed.getProgress(), mSeekbarBlue.getProgress(), mSeekbarGreen.getProgress());
        EffectApplyAsyncTask mEffectApplyTask =
                new EffectApplyAsyncTask(mEditBitmap, mGammaEffect, mProcessDialog, this);
        mEffectApplyTask.execute();
    }

    @Override
    public void onResult(Bitmap bitmap) {
        imageDisplayed.setImageBitmap(bitmap);
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
