package com.ubunifu.appclone.models;

import java.util.ArrayList;

public class User {
    String email, full_names, phone_number, profile_img_url, user_id;

    public User() {
    }

    public User(String email, String full_names, String phone_number, String profile_img_url, String user_id) {
        this.email = email;
        this.full_names = full_names;
        this.phone_number = phone_number;
        this.profile_img_url = profile_img_url;
        this.user_id = user_id;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFull_names() {
        return full_names;
    }

    public void setFull_names(String full_names) {
        this.full_names = full_names;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getProfile_img_url() {
        return profile_img_url;
    }

    public void setProfile_img_url(String profile_img_url) {
        this.profile_img_url = profile_img_url;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
