package com.mobile.readyplayer.ui;

import java.io.Serializable;

public class ItemPlaylist implements Serializable {

    public String name;

    public ItemPlaylist(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
