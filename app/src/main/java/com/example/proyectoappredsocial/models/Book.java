package com.example.proyectoappredsocial.models;

import java.util.ArrayList;

public class Book {

    private String idBook;
    private String idUser;
    private String title;
    private String subtitle;
    private ArrayList<String> authors;
    private String author;
    private String publisher;
    private String publishedDate;
    private String description;
    private String subject;
    private String volume;
    private float price;
    private float rating;
    private int pageCount;
    private String imageBook;
    private String isbn;
    private Boolean read;
    private String idRead;

    public Book() {
    }

    public Book(String idBook, String idUser, String title, String subtitle, String author, String publisher, String publishedDate, String description, String subject, String volume, float price,float rating, int pageCount, String imageBook, String isbn, Boolean read, String idRead) {
        this.idBook=idBook;
        this.idUser = idUser;
        this.title = title;
        this.subtitle = subtitle;
        this.author = author;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
        this.description = description;
        this.subject = subject;
        this.volume = volume;
        this.price = price;
        this.rating = rating;
        this.pageCount = pageCount;
        this.imageBook = imageBook;
        this.isbn = isbn;
        this.read = read;
        this.idRead = idRead;

    }

    public Book(String idUser, String title, String subtitle, ArrayList<String> authors, String publisher, String publishedDate, String description, int pageCount, String imageBook) {
        this.idUser = idUser;
        this.title = title;
        this.subtitle = subtitle;
        this.authors = authors;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
        this.description = description;
        this.pageCount = pageCount;
        this.imageBook = imageBook;

    }

    public Book(String title, String subtitle, ArrayList<String> authors, String publisher, String publishedDate, String description, int pageCount, String imageBook, String isbn) {

        this.title = title;
        this.subtitle = subtitle;
        this.authors = authors;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
        this.description = description;
        this.pageCount = pageCount;
        this.imageBook = imageBook;
        this.isbn = isbn;
    }

    public String getIdBook() {
        return idBook;
    }

    public void setIdBook(String idBook) {
        this.idBook = idBook;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public ArrayList<String> getAuthors() {
        return authors;
    }

    public void setAuthors(ArrayList<String> authors) {
        this.authors = authors;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public String getImageBook() {
        return imageBook;
    }

    public void setImageBook(String imageBook) {
        this.imageBook = imageBook;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Boolean getRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getIdRead() {
        return idRead;
    }

    public void setIdRead(String idRead) {
        this.idRead = idRead;
    }
}
