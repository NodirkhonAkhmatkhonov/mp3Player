package com.mobile.readyplayer;

import java.io.Serializable;
import java.util.Objects;

public class ItemSongs implements Serializable {
    String title;
    String absolutePath;
    String artist;
    Long duration;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemSongs itemSongs = (ItemSongs) o;
        return Objects.equals(title, itemSongs.title) &&
                Objects.equals(absolutePath, itemSongs.absolutePath) &&
                Objects.equals(artist, itemSongs.artist) &&
                Objects.equals(duration, itemSongs.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, absolutePath, artist, duration);
    }

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
