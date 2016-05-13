package com.framgia.photoalbum.ui.fragment;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.framgia.photoalbum.R;
import com.framgia.photoalbum.effect.Orientation;
import com.framgia.photoalbum.ui.activity.EditActivity;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by HungNT on 5/8/16.
 */
public class OrientationFragment extends EditFragment {

    @Bind(R.id.imageEdit)
    ImageView imageDisplayed;

    private Bitmap mEditBitmap;
    private Bitmap mResultBitmap;
    private ProgressDialog mProcessDialog;
    private Orientation mOrientation = new Orientation();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orientation, container, false);
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

    public static OrientationFragment newInstance(String path) {
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_IMAGE_PATH, path);

        OrientationFragment fragment = new OrientationFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    private void initComponent() {
        mEditBitmap = EditActivity.imageBitmap;
        mResultBitmap = mEditBitmap.copy(mEditBitmap.getConfig(), false);
        imageDisplayed.setImageBitmap(mEditBitmap);

        // Config dialog
        mProcessDialog = new ProgressDialog(getContext());
        mProcessDialog.setMessage(getContext().getString(R.string.loading));
        mProcessDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProcessDialog.setIndeterminate(true);
    }

    @OnClick({R.id.btnFlipHorz, R.id.btnFlipVert, R.id.btnRotateCCW, R.id.btnRotateCW})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnFlipHorz:
                mResultBitmap = mOrientation.flip(mResultBitmap, Orientation.FLIP_HORIZONTAL);
                break;
            case R.id.btnFlipVert:
                mResultBitmap = mOrientation.flip(mResultBitmap, Orientation.FLIP_VERTICAL);
                break;
            case R.id.btnRotateCCW:
                mResultBitmap = mOrientation.rotate(mResultBitmap, -90);
                break;
            case R.id.btnRotateCW:
                mResultBitmap = mOrientation.rotate(mResultBitmap, 90);
                break;
        }
        imageDisplayed.setImageBitmap(mResultBitmap);
    }
}
