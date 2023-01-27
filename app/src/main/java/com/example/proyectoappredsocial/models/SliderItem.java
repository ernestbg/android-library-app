package com.example.proyectoappredsocial.models;

public class SliderItem {


    String urlImage;
    long timestamp;


    public SliderItem(String urlImage, long timestamp) {
        this.urlImage = urlImage;
        this.timestamp = timestamp;
    }

    public SliderItem() {
    }

    public String getUrlImage() {
        return urlImage;
    }

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
