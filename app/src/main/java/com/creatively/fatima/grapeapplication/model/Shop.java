package com.creatively.fatima.grapeapplication.model;

import java.io.File;
import java.io.Serializable;

public class Shop implements Serializable {
    private String id, name, address, offerCount, shopImage, user_id, shop_phone, shop_bio,
            is_active, category_name, city_name, record_no;
    private File imageFile;

    public Shop(String id, String name, String address, String offerCount, String shopImage, String shop_phone) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.offerCount = offerCount;
        this.shopImage = shopImage;
        this.shop_phone = shop_phone;
    }

    public Shop() {

    }

    public String getRecord_no() {
        return record_no;
    }

    public File getImageFile() {
        return imageFile;
    }

    public void setImageFile(File imageFile) {
        this.imageFile = imageFile;
    }

    public void setRecord_no(String record_no) {
        this.record_no = record_no;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getShop_phone() {
        return shop_phone;
    }

    public void setShop_phone(String shop_phone) {
        this.shop_phone = shop_phone;
    }

    public String getShop_bio() {
        return shop_bio;
    }

    public void setShop_bio(String shop_bio) {
        this.shop_bio = shop_bio;
    }

    public String getIs_active() {
        return is_active;
    }

    public void setIs_active(String is_active) {
        this.is_active = is_active;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOfferCount() {
        return offerCount;
    }

    public void setOfferCount(String offerCount) {
        this.offerCount = offerCount;
    }

    public String getShopImage() {
        return shopImage;
    }

    public void setShopImage(String shopImage) {
        this.shopImage = shopImage;
    }
}
