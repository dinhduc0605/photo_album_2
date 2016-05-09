package com.framgia.photoalbum.ui.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.framgia.photoalbum.data.model.ImageItem;
import com.framgia.photoalbum.ui.activity.ChooseImageActivity;
import com.framgia.photoalbum.ui.activity.EditActivity;

import java.util.ArrayList;

/**
 * Created by dinhduc on 26/04/2016.
 */
public class ImageGridAdapter extends RecyclerView.Adapter<ImageGridAdapter.ViewHolder> {
    ArrayList<ImageItem> mImageItems;
    Activity mActivity;

    public ImageGridAdapter(Activity activity, ArrayList<ImageItem> imageItems) {
        mImageItems = imageItems;
        mActivity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //get with, height of device screen
        DisplayMetrics metrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        int imageViewWidth = (width - 30) / 3;

        //create image view
        ImageView imageView = new ImageView(mActivity);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(imageViewWidth, imageViewWidth);
        layoutParams.setMargins(5, 5, 5, 5);
        imageView.setLayoutParams(layoutParams);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        return new ViewHolder(imageView);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ImageItem image = mImageItems.get(position);
        Glide.with(mActivity)
                .load(image.getImagePath())
                .centerCrop()
                .skipMemoryCache(false)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, EditActivity.class);
                intent.putExtra(ChooseImageActivity.IMAGE_PATH, image.getImagePath());
                mActivity.startActivity(intent);
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
