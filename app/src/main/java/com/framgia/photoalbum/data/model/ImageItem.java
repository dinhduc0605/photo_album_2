package com.framgia.photoalbum.data.model;

/**
 * Created by dinhduc on 26/04/2016.
 */
public class ImageItem {
    private String mImagePath;
    private int mId;

    public ImageItem(String imagePath, int id) {
        mImagePath = imagePath;
        mId = id;
    }

    public int getId() {
        return mId;
    }
    public String getImagePath() {
        return mImagePath;
    }
}
