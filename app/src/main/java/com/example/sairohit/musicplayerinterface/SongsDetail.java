package com.example.sairohit.musicplayerinterface;

import android.graphics.Bitmap;

/**
 * Created by sairohit on 04/12/17.
 */

public class SongsDetail {
    private long id ;
    private String artist;
    private String title;

    public SongsDetail(long id, String artist, String title) {
        this.id = id;
        this.artist = artist;
        this.title = title;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
