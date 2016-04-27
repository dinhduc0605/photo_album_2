package com.framgia.photoalbum.data.model;

/**
 * Created by dinhduc on 26/04/2016.
 */
public class ImageItem {
    private String imagePath;
    private int id;

    public ImageItem() {

    }

    public ImageItem(String imagePath, int id) {
        this.imagePath = imagePath;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImagePath() {
        return imagePath;
    }


    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

}
