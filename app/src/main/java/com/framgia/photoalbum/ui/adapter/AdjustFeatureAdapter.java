package com.framgia.photoalbum.ui.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.framgia.photoalbum.R;
import com.framgia.photoalbum.data.model.AdjustItem;
import com.framgia.photoalbum.util.DimenUtils;

import java.util.ArrayList;

/**
 * Created by dinhduc on 10/05/2016.
 */
public class AdjustFeatureAdapter extends RecyclerView.Adapter<AdjustFeatureAdapter.ViewHolder> {
    private Activity mActivity;
    private ArrayList<AdjustItem> mAdjustItems;
    private OnEditListener mOnEditListener;

    public AdjustFeatureAdapter(Activity activity, ArrayList<AdjustItem> adjustItems, OnEditListener onEditListener) {
        mActivity = activity;
        mAdjustItems = adjustItems;
        mOnEditListener = onEditListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        DisplayMetrics metrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int layoutWidth = metrics.widthPixels / getItemCount();
        int layoutHeight = (int) DimenUtils.dpToPx(mActivity, 100);
        View view = LayoutInflater.from(mActivity).inflate(R.layout.item_list_feature, parent, false);
        RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(layoutWidth, layoutHeight);
        view.setLayoutParams(rlParams);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        AdjustItem adjustItem = mAdjustItems.get(position);
        holder.imageView.setImageResource(adjustItem.getIconRes());
        holder.textView.setText(adjustItem.getFeatureName());
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnEditListener.onEdit(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mAdjustItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv_item_list_feature);
            textView = (TextView) itemView.findViewById(R.id.tv_item_list_feature);
        }
    }

    public interface OnEditListener {
        void onEdit(int position);
    }
}
