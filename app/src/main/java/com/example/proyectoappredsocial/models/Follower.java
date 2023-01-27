package com.example.proyectoappredsocial.models;

public class Follower {

    private String idUser;
    private String idUserFollowing;
    private long timestamp;

    public Follower() {
    }

    public Follower(String idUser, String idUserFollowing, long timestamp) {
        this.idUser = idUser;
        this.idUserFollowing = idUserFollowing;
        this.timestamp = timestamp;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getIdUserFollowing() {
        return idUserFollowing;
    }

    public void setIdUserFollowing(String idUserFollowing) {
        this.idUserFollowing = idUserFollowing;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
