package com.framgia.photoalbum.ui.fragment;

import android.net.Uri;
import android.support.v4.app.Fragment;

import com.framgia.photoalbum.ui.activity.EditActivity;

import butterknife.ButterKnife;

/**
 * Created by HungNT on 5/5/16.
 */
public abstract class EditFragment extends Fragment {
    protected static final String BUNDLE_IMAGE_PATH = "BUNDLE_IMAGE_PATH";
    protected Uri mSourceUri;

    public abstract void apply();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        if (getActivity() instanceof EditActivity) {
            ((EditActivity) getActivity()).clearFragment();
        }
    }
}
