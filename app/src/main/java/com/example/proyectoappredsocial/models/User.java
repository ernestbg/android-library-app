package com.example.proyectoappredsocial.models;

public class User {


    private String id;
    private String email;
    private String username;
    private String aboutMe;
    private long timeStamp;
    private String imageProfile;
    private int totalBooks;
    private int followers;
    private long lastConnection;
    private boolean online;


    public User() {
    }


    public User(String id, String email, String username, String aboutMe, long timeStamp, String imageProfile, int totalBooks, int followers, long lastConnection, boolean online) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.aboutMe = aboutMe;
        this.timeStamp = timeStamp;
        this.imageProfile = imageProfile;
        this.totalBooks = totalBooks;
        this.followers = followers;
        this.lastConnection = lastConnection;
        this.online = online;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getImageProfile() {
        return imageProfile;
    }

    public void setImageProfile(String imageProfile) {
        this.imageProfile = imageProfile;
    }

    public int getTotalBooks() {
        return totalBooks;
    }

    public void setTotalBooks(int totalBooks) {
        this.totalBooks = totalBooks;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public long getLastConnection() {
        return lastConnection;
    }

    public void setLastConnection(long lastConnection) {
        this.lastConnection = lastConnection;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }
}
