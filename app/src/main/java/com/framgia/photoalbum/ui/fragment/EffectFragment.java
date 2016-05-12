package com.framgia.photoalbum.ui.fragment;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.framgia.photoalbum.R;
import com.framgia.photoalbum.asynctask.EffectApplyAsyncTask;
import com.framgia.photoalbum.data.model.FeatureItem;
import com.framgia.photoalbum.effect.EffectFilter;
import com.framgia.photoalbum.effect.Emboss;
import com.framgia.photoalbum.effect.GrayScale;
import com.framgia.photoalbum.effect.Negative;
import com.framgia.photoalbum.effect.Noise;
import com.framgia.photoalbum.effect.Sharpen;
import com.framgia.photoalbum.effect.Sketch;
import com.framgia.photoalbum.ui.activity.EditActivity;
import com.framgia.photoalbum.ui.adapter.ListFeatureAdapter;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by HungNT on 5/5/16.
 */
public class EffectFragment extends EditFragment implements EffectApplyAsyncTask.OnApplyListener,
        ListFeatureAdapter.OnFeatureClicked {

    private static final int EFFECT_GRAY_SCALE = 0;
    private static final int EFFECT_NEGATIVE = 1;
    private static final int EFFECT_SHARPEN = 2;
    private static final int EFFECT_EMBOSS = 3;
    private static final int EFFECT_SKETCH = 4;
    private static final int EFFECT_NOISE = 5;

    @Bind(R.id.imageEdit)
    ImageView imageDisplayed;

    @Bind(R.id.listEffect)
    RecyclerView mRecyclerViewEffect;

    private Bitmap mEditBitmap;
    private ProgressDialog mProcessDialog;
    private ArrayList<FeatureItem> mFeatureItems = new ArrayList<>();
    private ListFeatureAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_effect, container, false);
        ButterKnife.bind(this, view);

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
        mProcessDialog.setCancelable(false);

        mFeatureItems.add(new FeatureItem(R.drawable.tab_effect_normal,
                getContext().getString(R.string.gray_scale)));
        mFeatureItems.add(new FeatureItem(R.drawable.tab_adjust_normal,
                getContext().getString(R.string.negative)));
        mFeatureItems.add(new FeatureItem(R.drawable.tab_adjust_normal,
                getContext().getString(R.string.sharpen)));
        mFeatureItems.add(new FeatureItem(R.drawable.tab_adjust_normal,
                getContext().getString(R.string.emboss)));
        mFeatureItems.add(new FeatureItem(R.drawable.tab_adjust_normal,
                getContext().getString(R.string.sketch)));
        mFeatureItems.add(new FeatureItem(R.drawable.tab_adjust_normal,
                getContext().getString(R.string.noise)));

        mAdapter = new ListFeatureAdapter(getContext(), mFeatureItems, this);
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        mRecyclerViewEffect.setLayoutManager(layoutManager);
        mRecyclerViewEffect.setAdapter(mAdapter);
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
        // TODO apply effect selected and save in cache
    }


    public void applyEffect(EffectFilter effect) {
        EffectApplyAsyncTask mEffectApplyTask =
                new EffectApplyAsyncTask(mEditBitmap, effect, mProcessDialog, this);
        mEffectApplyTask.execute();
    }

    @Override
    public void onResult(Bitmap bitmap) {
        imageDisplayed.setImageBitmap(bitmap);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onClick(View v, int position) {
        switch (position) {
            case EFFECT_GRAY_SCALE:
                applyEffect(new GrayScale());
                break;
            case EFFECT_NEGATIVE:
                applyEffect(new Negative());
                break;
            case EFFECT_SHARPEN:
                applyEffect(new Sharpen());
                break;
            case EFFECT_EMBOSS:
                applyEffect(new Emboss());
                break;
            case EFFECT_SKETCH:
                applyEffect(new Sketch());
                break;
            case EFFECT_NOISE:
                applyEffect(new Noise());
                break;
        }
    }
}
