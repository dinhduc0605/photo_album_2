package com.framgia.photoalbum.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.framgia.photoalbum.R;
import com.framgia.photoalbum.data.model.AdjustItem;

import java.util.ArrayList;

/**
 * Created by dinhduc on 10/05/2016.
 */
public class AdjustFeatureAdapter extends RecyclerView.Adapter<AdjustFeatureAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<AdjustItem> mAdjustItems;
    private OnEditListener mOnEditListener;

    public AdjustFeatureAdapter(Context context, ArrayList<AdjustItem> adjustItems, OnEditListener onEditListener) {
        mContext = context;
        mAdjustItems = adjustItems;
        mOnEditListener = onEditListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager) mContext
                .getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay()
                .getMetrics(metrics);
        int layoutWidth = metrics.widthPixels / getItemCount();
        int layoutHeight = (int) mContext.getResources().getDimension(R.dimen.recycler_view_height);
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_list_feature, parent, false);
        RelativeLayout.LayoutParams rlParams = new RelativeLayout.LayoutParams(layoutWidth, layoutHeight);
        view.setLayoutParams(rlParams);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        AdjustItem adjustItem = mAdjustItems.get(position);
        holder.imageView.setImageResource(adjustItem.getIconRes());
        holder.textView.setText(adjustItem.getFeatureName());
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
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnEditListener.onEdit(getAdapterPosition());
                }
            });
        }
    }

    public interface OnEditListener {
        void onEdit(int position);
    }
}
