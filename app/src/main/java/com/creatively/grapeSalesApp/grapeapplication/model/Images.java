package com.creatively.grapeSalesApp.grapeapplication.model;

import android.net.Uri;

import java.io.File;

public class Images {
    private String id, images;
    private Uri uri;
    private File imageFile;

    public Images(String images) {
        this.images = images;
    }

    public File getImageFile() {
        return imageFile;
    }

    public void setImageFile(File imageFile) {
        this.imageFile = imageFile;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public Images(Uri uri) {
        this.uri = uri;
    }


    public Images(Uri uri, File imageFile) {
        this.uri = uri;
        this.imageFile = imageFile;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }
}
