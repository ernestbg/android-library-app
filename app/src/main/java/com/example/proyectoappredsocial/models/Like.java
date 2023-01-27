package com.example.proyectoappredsocial.models;

public class Like {

    private String id;
    private String idPost;
    private String idUser;
    private long date;

    public Like(String id, String idPost, String idUser, long date) {
        this.id = id;
        this.idPost = idPost;
        this.idUser = idUser;
        this.date = date;
    }

    public Like() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdPost() {
        return idPost;
    }

    public void setIdPost(String idPost) {
        this.idPost = idPost;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
