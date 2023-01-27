package com.example.proyectoappredsocial.models;

public class Challenge {

    private String id;
    private String idUser;
    private String totalBooks;
    private String totalBooksRead;
    private String startDate;
    private String finishDate;
    private String progressInfo;
    private String state;
    private long timestamp;


    public Challenge() {
    }

    public Challenge(String id, String idUser, String totalBooks, String totalBooksRead, String startDate, String finishDate, String progressInfo, String state, long timestamp) {
        this.id = id;
        this.idUser = idUser;
        this.totalBooks = totalBooks;
        this.totalBooksRead = totalBooksRead;
        this.startDate = startDate;
        this.finishDate = finishDate;
        this.progressInfo = progressInfo;
        this.state = state;
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

    public String getTotalBooks() {
        return totalBooks;
    }

    public void setTotalBooks(String totalBooks) {
        this.totalBooks = totalBooks;
    }

    public String getTotalBooksRead() {
        return totalBooksRead;
    }

    public void setTotalBooksRead(String totalBooksRead) {
        this.totalBooksRead = totalBooksRead;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(String finishDate) {
        this.finishDate = finishDate;
    }

    public String getProgressInfo() {
        return progressInfo;
    }

    public void setProgressInfo(String progressInfo) {
        this.progressInfo = progressInfo;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
