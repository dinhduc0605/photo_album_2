package com.framgia.photoalbum.ui.adapter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.framgia.photoalbum.R;

import java.util.List;

/**
 * Created by HungNT on 5/17/16.
 */
public class ShareGridAdapter extends BaseAdapter {

    private Context mContext;
    private List<ResolveInfo> mListApps;
    private PackageManager mPackageManager;

    public ShareGridAdapter(Context context, List<ResolveInfo> listApps) {
        this.mContext = context;
        this.mListApps = listApps;
        this.mPackageManager = mContext.getPackageManager();
    }

    @Override
    public int getCount() {
        return mListApps.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private static class Holder {
        TextView tv;
        ImageView img;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_share, parent, false);
            holder = new Holder();
            holder.tv = (TextView) convertView.findViewById(R.id.tvAppName);
            holder.img = (ImageView) convertView.findViewById(R.id.imageAppIcon);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        ResolveInfo item = mListApps.get(position);
        holder.tv.setText(item.loadLabel(mPackageManager));
        holder.img.setImageDrawable(item.loadIcon(mPackageManager));

        return convertView;
    }

}
