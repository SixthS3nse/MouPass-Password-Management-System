package com.example.moupass10.ui.dashboard;

import java.io.Serializable;

public class DataItem implements Serializable {
    private String title;
    private String user;
    private String pass;
    private String website;

    //Constructor
    public DataItem(String title, String user, String pass, String website) {
        this.title = title;
        this.user = user;
        this.pass = pass;
        this.website = website;
    }

    //Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}

