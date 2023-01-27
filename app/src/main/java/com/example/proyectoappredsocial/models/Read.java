package com.example.proyectoappredsocial.models;

public class Read {

    private String id;
    private String idUser;
    private String idBook;
    long timestamp;

    public Read() {
    }

    public Read(String id, String idUser, String idBook, long timestamp) {
        this.id = id;
        this.idUser = idUser;
        this.idBook = idBook;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getIdBook() {
        return idBook;
    }

    public void setIdBook(String idBook) {
        this.idBook = idBook;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
