package com.example.moupass10;

    public class DataModel {
        String title;
        String website;

        //Constructor
        public DataModel(String title, String website){
            this.title = title;
            this.website = website;
        }

        //Getter and setter methods
        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getWebsite() {
            return website;
        }

        public void setWebsite(String website) {
            this.website = website;
        }
    }
