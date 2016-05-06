package com.framgia.photoalbum.asynctask;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.framgia.photoalbum.effect.EffectFilter;

/**
 * Created by HungNT on 5/6/16.
 */
public class EffectApplyAsyncTask extends AsyncTask<Void, Void, Void> {

    private Bitmap mSrc;
    private Bitmap mResult;
    private OnApplyListener mApplyListener;
    private ProgressDialog mProgressDialog;
    private EffectFilter mEffectFilter;

    public EffectApplyAsyncTask(Bitmap src, EffectFilter effect,
                                ProgressDialog progressDialog,
                                OnApplyListener listener) {
        mSrc = src;
        mProgressDialog = progressDialog;
        mApplyListener = listener;
        mEffectFilter = effect;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (mProgressDialog != null) {
            mProgressDialog.show();
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        mResult = mEffectFilter.applyEffect(mSrc);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }

        mApplyListener.onResult(mResult);
    }

    public interface OnApplyListener {
        void onResult(Bitmap bitmap);
    }

}
