package com.example.chatapp.Model;

public class Users {
    String username;
    String imageURL;
    String id;
    String search;
    String email, about;
    long connections;
    String mood;
    String frid;
    String tag;
    String status;

    public Users(String username, String imageURL, String id, String search, String mood, String frid, String tag, String status) {
        this.username = username;
        this.imageURL = imageURL;
        this.id = id;
        this.search = search;
        this.mood = mood;
        this.frid = frid;
        this.tag = tag;
        this.status = status;
    }

    public Users() {

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public long getConnections() {
        return connections;
    }

    public void setConnections(long connections) {
        this.connections = connections;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getFrid() {
        return frid;
    }

    public void setFrid(String frid) {
        this.frid = frid;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
