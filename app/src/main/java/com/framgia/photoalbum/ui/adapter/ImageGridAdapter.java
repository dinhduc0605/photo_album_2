package com.framgia.photoalbum.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.framgia.photoalbum.BuildConfig;
import com.framgia.photoalbum.R;
import com.framgia.photoalbum.data.model.ImageItem;
import com.framgia.photoalbum.ui.activity.ChooseImageActivity;
import com.framgia.photoalbum.ui.activity.EditActivity;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by dinhduc on 26/04/2016.
 */
public class ImageGridAdapter extends RecyclerView.Adapter<ImageGridAdapter.ViewHolder> {
    private static final String TAG = "ImageGridAdapter";
    private ArrayList<ImageItem> mImageItems;
    private Context mContext;

    public ImageGridAdapter(Context context, ArrayList<ImageItem> imageItems) {
        mImageItems = imageItems;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //get with, height of device screen
        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager) mContext
                .getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay()
                .getMetrics(metrics);

        int width = metrics.widthPixels;
        int imageViewWidth = (width - 30) / 3;

        //create image view
        ImageView imageView = new ImageView(mContext);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(imageViewWidth, imageViewWidth);
        layoutParams.setMargins(5, 5, 5, 5);
        imageView.setLayoutParams(layoutParams);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return new ViewHolder(imageView);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ImageItem image = mImageItems.get(position);
        if (BuildConfig.DEBUG) {
            Log.w(TAG, image.getImagePath());
        }
        Picasso.with(mContext)
                .load("file://" + image.getImagePath())
                .config(Bitmap.Config.RGB_565)
                .resize(150,150)
                .centerCrop()
                .placeholder(R.drawable.ic_camera)
                .into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (BuildConfig.DEBUG) {
                    Log.w(TAG, image.getImagePath());
                }
                Intent intent = new Intent(mContext, EditActivity.class);
                intent.putExtra(ChooseImageActivity.IMAGE_PATH, image.getImagePath());
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mImageItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView;
        }
    }

}
