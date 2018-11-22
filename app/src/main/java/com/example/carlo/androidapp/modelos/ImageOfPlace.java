package com.example.carlo.androidapp.modelos;

public class ImageOfPlace {

    String imageUrl;
    int id;

    public ImageOfPlace(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
