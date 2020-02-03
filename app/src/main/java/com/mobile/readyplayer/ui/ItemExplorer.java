package com.mobile.readyplayer.ui;

public class ItemExplorer {

    public boolean isChecked;
    public String fileType;
    public String nameOfFile;
    public String absolutePath;

    public ItemExplorer(String fileType, String nameOfFile, String absolutePath) {
        this.fileType = fileType;
        this.nameOfFile = nameOfFile;
        this.absolutePath = absolutePath;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getFileType() {
        return fileType;
    }

    public String getNameOfFile() {
        return nameOfFile;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }
}
