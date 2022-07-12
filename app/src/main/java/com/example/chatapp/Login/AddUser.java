package com.example.chatapp.Login;

public class AddUser {

    String email;
    String username;
    String tag;
    String mood;

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public AddUser(String email, String username, String tag, String about, String imageURL, String id, String search, String status, String mood) {
        this.email = email;
        this.username = username;
        this.tag = tag;
        this.about = about;
        this.imageURL = imageURL;
        this.id = id;
        this.search = search;
        this.status = status;
        this.connections = 0;
        this.mood = mood;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    String about;


    String imageURL;
    String id;
    String search;
    String status;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    int connections;

    public AddUser() {

    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getConnections() {
        return connections;
    }

    public void setConnections(int connections) {
        this.connections = connections;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }


}
