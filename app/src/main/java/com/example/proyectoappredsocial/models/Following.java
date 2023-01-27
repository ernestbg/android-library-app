package com.example.proyectoappredsocial.models;

public class Following {

    private String idUser;
    private String idUserFollower;
    private long timestamp;

    public Following() {
    }

    public Following(String idUser, String idUserFollower, long timestamp) {
        this.idUser = idUser;
        this.idUserFollower = idUserFollower;
        this.timestamp = timestamp;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getIdUserFollower() {
        return idUserFollower;
    }

    public void setIdUserFollower(String idUserFollower) {
        this.idUserFollower = idUserFollower;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
