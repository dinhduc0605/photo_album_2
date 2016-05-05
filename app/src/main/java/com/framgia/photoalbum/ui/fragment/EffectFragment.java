package com.framgia.photoalbum.ui.fragment;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.framgia.photoalbum.R;
import com.framgia.photoalbum.effect.EffectFilter;
import com.framgia.photoalbum.effect.Negative;
import com.framgia.photoalbum.ui.activity.EditActivity;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by HungNT on 5/5/16.
 */
public class EffectFragment extends EditFragment {

    @Bind(R.id.editImage)
    ImageView imageDisplayed;

    private Bitmap mEditBitmap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_effect, container, false);

        ButterKnife.bind(this, view);

        if (getArguments() != null) {
            String path = getArguments().getString(BUNDLE_IMAGE_PATH);
            mSourceUri = Uri.fromFile(new File(path));
        }

        mEditBitmap = EditActivity.imageBitmap;
        imageDisplayed.setImageBitmap(mEditBitmap);

        return view;
    }

    public static EffectFragment newInstance(String path) {
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_IMAGE_PATH, path);

        EffectFragment fragment = new EffectFragment();
        fragment.setArguments(bundle);

        return fragment;
    }


    @Override
    public void apply() {
        applyEffect(new Negative());
    }

    public void applyEffect(EffectFilter effect) {
        Bitmap result = effect.applyEffect(mEditBitmap);
        imageDisplayed.setImageBitmap(result);
    }

}
