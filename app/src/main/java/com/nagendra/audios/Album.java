package com.nagendra.audios;

public class Album {
    private String name = "";
    private String thumbnail = "";

    public Album(String name, String pic) {
        this.name = name;
        this.thumbnail = pic;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String profilePic) {
        this.thumbnail = profilePic;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
