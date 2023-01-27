package com.example.proyectoappredsocial.providers;

import android.util.Log;

import com.example.proyectoappredsocial.models.Book;
import com.example.proyectoappredsocial.models.BookReview;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class BookReviewsProvider {

    CollectionReference collectionReference;

    public BookReviewsProvider() {
        collectionReference = FirebaseFirestore.getInstance().collection("BookReviews");

    }


    public Task<Void> save(BookReview bookReview) {
        return collectionReference.document().set(bookReview);

    }

    public Query getReviewsByBook(String isbn) {


        return collectionReference.whereEqualTo("bookIsbn", isbn);

    }



    public Query getBookReviewsByUser(String idUser) {

        return collectionReference.whereEqualTo("idUser", idUser);


    }


    public Task<Void> delete(String id) {
        return collectionReference.document(id).delete();
    }



}
