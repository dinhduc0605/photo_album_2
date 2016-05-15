package com.framgia.photoalbum.ui.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.framgia.photoalbum.R;
import com.framgia.photoalbum.crop.CropImageView;
import com.framgia.photoalbum.crop.HighlightView;
import com.framgia.photoalbum.crop.RotateBitmap;
import com.framgia.photoalbum.ui.activity.EditActivity;
import com.framgia.photoalbum.util.CommonUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by HungNT on 5/4/16.
 */
public class CropFragment extends EditFragment {

    @Bind(R.id.imageCrop)
    CropImageView mImageCrop;

    // Output image size
    private int maxX;
    private int maxY;
    private int exifRotation;

    private HighlightView mHighlightView;
    private boolean isSaving;
    private Bitmap mCropBitmap;

    public static CropFragment newInstance(String path) {
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_IMAGE_PATH, path);

        CropFragment fragment = new CropFragment();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crop, container, false);
        ButterKnife.bind(this, view);


        if (getArguments() != null) {
            String path = getArguments().getString(BUNDLE_IMAGE_PATH);
            mSourceUri = Uri.fromFile(new File(path));
        }

        Bitmap src = EditActivity.sSourceBitmap;
        mCropBitmap = src.copy(src.getConfig(), false);

        RotateBitmap rotateBitmap = new RotateBitmap(mCropBitmap, 0);

        mImageCrop.setImageBitmap(rotateBitmap);

        mHighlightView = mImageCrop.getHighlightView();
        loadInput();

        return view;
    }

    private void loadInput() {
        if (mSourceUri != null) {
            exifRotation = CommonUtils.getExifRotation(mSourceUri.getPath());
        }
    }

    /**
     * @param rect      highlight rect
     * @param outWidth  final width
     * @param outHeight final height
     * @return Image cropped inside the highlight view
     */
    private Bitmap decodeRegionCrop(Rect rect, int outWidth, int outHeight) {
        clearImageView();

        InputStream is;
        Bitmap croppedImage = null;
        try {
            is = getContext().getContentResolver().openInputStream(mSourceUri);
            BitmapRegionDecoder decoder = BitmapRegionDecoder.newInstance(is, false);
            final int width = decoder.getWidth();
            final int height = decoder.getHeight();

            if (exifRotation != 0) {
                // Adjust crop area to account for image rotation
                Matrix matrix = new Matrix();
                matrix.setRotate(-exifRotation);

                RectF adjusted = new RectF();
                matrix.mapRect(adjusted, new RectF(rect));

                // Adjust to account for origin at 0,0
                adjusted.offset(adjusted.left < 0 ? width : 0, adjusted.top < 0 ? height : 0);
                rect = new Rect((int) adjusted.left, (int) adjusted.top, (int) adjusted.right, (int) adjusted.bottom);
            }

            try {
                croppedImage = decoder.decodeRegion(rect, new BitmapFactory.Options());
                if (croppedImage != null && (rect.width() > outWidth || rect.height() > outHeight)) {
                    Matrix matrix = new Matrix();
                    matrix.postScale((float) outWidth / rect.width(), (float) outHeight / rect.height());
                    croppedImage = Bitmap.createBitmap(croppedImage, 0, 0, croppedImage.getWidth(), croppedImage.getHeight(), matrix, true);
                }
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Rectangle not in image", e);
            }

        } catch (IOException e) {
            Log.e("Error cropping image: ", e.getMessage());
        } catch (OutOfMemoryError e) {
            Log.e("OOM cropping image: ", e.getMessage());
        }

        return croppedImage;
    }

    public void onCropClick() {
        if (mHighlightView == null || isSaving) {
            return;
        }

        isSaving = true;
        Bitmap croppedImage;

        Rect r = mHighlightView.getScaledCropRect(1);
        int width = r.width();
        int height = r.height();

        int outWidth = width;
        int outHeight = height;
        if (maxX > 0 && maxY > 0 && (width > maxX || height > maxY)) {
            float ratio = (float) width / (float) height;
            if ((float) maxX / (float) maxY > ratio) {
                outHeight = maxY;
                outWidth = (int) ((float) maxY * ratio + .5f);
            } else {
                outWidth = maxX;
                outHeight = (int) ((float) maxX / ratio + .5f);
            }
        }

        croppedImage = decodeRegionCrop(r, outWidth, outHeight);


        if (croppedImage != null) {
            EditActivity.setResultBitmap(croppedImage);
        }
    }

    private void clearImageView() {
        // Clear reference
        mImageCrop.setBitmapDisplayed(null, 0);
        if (mCropBitmap != null) {
            mCropBitmap.recycle();
        }
        System.gc();
    }

    @Override
    public void apply() {
        onCropClick();

    }
}
