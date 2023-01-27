package com.example.proyectoappredsocial.models;

import android.content.Intent;

import com.example.proyectoappredsocial.fragments.ChallengeFragment;

public class BookReview {

    String idUser;
    String bookIsbn;
    float rating;
    String textReview;
    long dateReview;



    public BookReview() {
    }

    public BookReview(String idUser, String bookIsbn, float rating, String textReview, long dateReview) {
        this.idUser = idUser;
        this.bookIsbn = bookIsbn;
        this.rating = rating;
        this.textReview = textReview;
        this.dateReview = dateReview;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getBookIsbn() {
        return bookIsbn;
    }

    public void setBookIsbn(String bookIsbn) {
        this.bookIsbn = bookIsbn;
    }


    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getTextReview() {
        return textReview;
    }

    public void setTextReview(String textReview) {
        this.textReview = textReview;
    }

    public long getDateReview() {
        return dateReview;
    }

    public void setDateReview(long dateReview) {
        this.dateReview = dateReview;
    }
}
