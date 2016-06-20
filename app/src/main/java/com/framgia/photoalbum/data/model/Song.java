package com.framgia.photoalbum.data.model;

/**
 * Created by HungNT on 6/17/16.
 */
public class Song {
    private String name;
    private int id;
    private boolean isChecked;

    public Song(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
