package com.example.moupass10;

public class SettingsOption {

    private int iconID;
    private String title;

    public SettingsOption(int iconID, String title) {
        this.iconID = iconID;
        this.title = title;
    }

    public int getIconID() {
        return iconID;
    }

    public String getTitle() {
        return title;
    }
}
