package com.mobile.readyplayer.ui;

public class ItemExplorer {

    public boolean isChecked;
    public boolean isFolder;
    public String nameOfFile;
    public String absolutePath;

    public ItemExplorer(boolean isFolder, String nameOfFile, String absolutePath) {
        this.isFolder = isFolder;
        this.nameOfFile = nameOfFile;
        this.absolutePath = absolutePath;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public boolean isFolder() {
        return isFolder;
    }

    public String getNameOfFile() {
        return nameOfFile;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }
}
