package com.framgia.photoalbum.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.framgia.photoalbum.R;
import com.framgia.photoalbum.data.model.FeatureItem;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by dinhduc on 27/04/2016.
 */
public class ListFeatureAdapter extends RecyclerView.Adapter<ListFeatureAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<FeatureItem> mFeatureItems;
    private OnFeatureClicked mOnFeatureClicked;
    private int mLayoutItemRes;

    public ListFeatureAdapter(Context context, ArrayList<FeatureItem> featureItems,
                              OnFeatureClicked onFeatureClicked) {
        mContext = context;
        mFeatureItems = featureItems;
        mOnFeatureClicked = onFeatureClicked;
        mLayoutItemRes = R.layout.item_list_feature;
    }

    public ListFeatureAdapter(Context context, ArrayList<FeatureItem> featureItems,
                              OnFeatureClicked onFeatureClicked, int layoutItemRes) {
        mContext = context;
        mFeatureItems = featureItems;
        mOnFeatureClicked = onFeatureClicked;
        this.mLayoutItemRes = layoutItemRes;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(mLayoutItemRes, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        FeatureItem item = mFeatureItems.get(position);
        holder.imageView.setImageResource(item.getIconRes());
        holder.textView.setText(item.getFeatureName());

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnFeatureClicked.onClick(view, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFeatureItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_item_list_feature)
        ImageView imageView;
        @Bind(R.id.tv_item_list_feature)
        TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnFeatureClicked {
        void onClick(View v, int position);
    }
}
