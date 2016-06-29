package com.framgia.photoalbum.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.framgia.photoalbum.BuildConfig;
import com.framgia.photoalbum.R;
import com.framgia.photoalbum.data.model.ImageItem;
import com.framgia.photoalbum.util.FileUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by dinhduc on 26/04/2016.
 */
public class ChooseMultipleImageAdapter extends RecyclerView.Adapter<ChooseMultipleImageAdapter.ViewHolder> {
    private static final String TAG = "ChooseMultipleAdapter";
    private ArrayList<ImageItem> mImageItems;
    private Context mContext;
    private ArrayList<String> mChosenImages = new ArrayList<>();

    public ChooseMultipleImageAdapter(Context context, ArrayList<ImageItem> imageItems) {
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
        int imageViewWidth = width / 3;

        //inflate layout
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.item_choose_multiple_images, null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(imageViewWidth, imageViewWidth);
        layoutParams.setMargins(5, 5, 0, 0);
        view.setLayoutParams(layoutParams);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        ImageItem image = mImageItems.get(position);
        final String imagePath = mImageItems.get(position).getImagePath();
        Log.d("hung", imagePath + " ---" + FileUtils.isPhotoValid(imagePath));
        holder.mCheckBox.setChecked(mChosenImages.contains(imagePath));
        if (BuildConfig.DEBUG) {
            Log.w(TAG, imagePath);
        }
        Picasso.with(mContext)
                .load("file://" + imagePath)
                .config(Bitmap.Config.RGB_565)
                .resize(150, 150)
                .centerCrop()
                .placeholder(R.drawable.ic_camera)
                .into(holder.mImageView);
        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.mCheckBox.isChecked()) {
                    holder.mCheckBox.setChecked(false);
                    mChosenImages.remove(imagePath);
                } else {
                    holder.mCheckBox.setChecked(true);
                    if (!mChosenImages.contains(imagePath)) {
                        mChosenImages.add(imagePath);
                    }
                }
            }
        });
        holder.mCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!holder.mCheckBox.isChecked()) {
                    mChosenImages.remove(imagePath);
                } else {
                    if (!mChosenImages.contains(imagePath)) {
                        mChosenImages.add(imagePath);
                    }
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mImageItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.image_item)
        ImageView mImageView;
        @Bind(R.id.check_box)
        CheckBox mCheckBox;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public ArrayList<String> getChosenImages() {
        return mChosenImages;
    }
}
