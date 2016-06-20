package com.framgia.photoalbum.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.framgia.photoalbum.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ImagesPreviewAdapter extends RecyclerView.Adapter<ImagesPreviewAdapter.ViewHolder> {
    private final int IMAGE_WIDTH = 720;
    private final int IMAGE_HEIGHT = 405;

    private Context mContext;
    private ArrayList<String> mListImagePaths;
    private OnItemClicked mOnFeatureClicked;


    public ImagesPreviewAdapter(Context context, ArrayList<String> featureItems,
                                OnItemClicked onFeatureClicked) {
        mContext = context;
        mListImagePaths = featureItems;
        mOnFeatureClicked = onFeatureClicked;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_preview_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        String item = mListImagePaths.get(position);
        Picasso.with(mContext).load("file://" + item).resize(IMAGE_WIDTH, IMAGE_HEIGHT).centerCrop().into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mListImagePaths.size();
    }

    public void remove(int position) {
        mListImagePaths.remove(position);
        notifyItemRemoved(position);
    }

    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mListImagePaths, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mListImagePaths, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.imagePreview)
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnFeatureClicked.onClick(v, getAdapterPosition());
                }
            });
        }
    }

    public interface OnItemClicked {
        void onClick(View v, int position);
    }
}
