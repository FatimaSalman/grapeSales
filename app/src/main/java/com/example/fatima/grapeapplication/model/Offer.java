package com.example.fatima.grapeapplication.model;

import java.io.File;
import java.util.List;

public class Offer {
    private String id, offerTitle, offerBio, previousPrice, NextPrice, offerImage, shop_id, user_id, rating;
    private File imageFile;
    private List<Images> imagesList;

    public Offer(String id, String offerTitle, String offerBio, String previousPrice,
                 String user_id, String nextPrice, String offerImage, String shop_id) {
        this.id = id;
        this.offerTitle = offerTitle;
        this.offerBio = offerBio;
        this.previousPrice = previousPrice;
        this.NextPrice = nextPrice;
        this.offerImage = offerImage;
        this.shop_id = shop_id;
        this.user_id = user_id;
    }

    public Offer(String id, String offerTitle, String offerBio, String previousPrice,
                 String nextPrice, String offerImage, String shop_id, String rating, String data) {
        this.id = id;
        this.offerTitle = offerTitle;
        this.offerBio = offerBio;
        this.previousPrice = previousPrice;
        this.NextPrice = nextPrice;
        this.offerImage = offerImage;
        this.shop_id = shop_id;
        this.rating = rating;
    }

    public Offer() {

    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public List<Images> getImagesList() {
        return imagesList;
    }

    public void setImagesList(List<Images> imagesList) {
        this.imagesList = imagesList;
    }

    public String getShop_id() {
        return shop_id;
    }

    public void setShop_id(String shop_id) {
        this.shop_id = shop_id;
    }

    public File getImageFile() {
        return imageFile;
    }

    public void setImageFile(File imageFile) {
        this.imageFile = imageFile;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOfferTitle() {
        return offerTitle;
    }

    public void setOfferTitle(String offerTitle) {
        this.offerTitle = offerTitle;
    }

    public String getOfferBio() {
        return offerBio;
    }

    public void setOfferBio(String offerBio) {
        this.offerBio = offerBio;
    }

    public String getPreviousPrice() {
        return previousPrice;
    }

    public void setPreviousPrice(String previousPrice) {
        this.previousPrice = previousPrice;
    }

    public String getNextPrice() {
        return NextPrice;
    }

    public void setNextPrice(String nextPrice) {
        NextPrice = nextPrice;
    }

    public String getOfferImage() {
        return offerImage;
    }

    public void setOfferImage(String offerImage) {
        this.offerImage = offerImage;
    }
}
