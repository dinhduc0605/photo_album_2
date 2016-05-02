package com.framgia.photoalbum.data.model;

/**
 * Created by dinhduc on 27/04/2016.
 */
public class FeatureItem {
    private int mIconRes;
    private String mFeatureName;

    public FeatureItem(int iconRes, String featureName) {
        this.mIconRes = iconRes;
        this.mFeatureName = featureName;
    }

    public int getIconRes() {
        return mIconRes;
    }

    public String getFeatureName() {
        return mFeatureName;
    }
}
