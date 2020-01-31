package com.mobile.readyplayer;

import java.io.Serializable;

public class ItemSongs implements Serializable {
    String title;
    String absolutePath;
    String artist;
    Long duration;

    public ItemSongs(String name, String absolutePath, String artist, Long duration) {
        this.title = name;
        this.absolutePath = absolutePath;
        this.artist = artist;
        this.duration = duration;
    }

    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public int getDuration() {
        return duration.intValue();
    }
}
