package com.framgia.photoalbum.data.model;

/**
 * Created by dinhduc on 26/04/2016.
 */
public class ImageItem {
    private String mImagePath;
    private int mId;
    private String mThumbnailPath;

    public ImageItem(String imagePath, int id, String thumbnailPath) {
        this.mImagePath = imagePath;
        this.mId = id;
        this.mThumbnailPath = thumbnailPath;
    }

    public int getId() {
        return mId;
    }
    public String getImagePath() {
        return mImagePath;
    }

    public String getThumbnailPath() {
        return mThumbnailPath;
    }
}
