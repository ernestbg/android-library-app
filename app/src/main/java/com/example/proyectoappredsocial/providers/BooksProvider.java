package com.example.proyectoappredsocial.providers;

import android.content.Context;
import android.util.Log;

import com.example.proyectoappredsocial.models.Book;
import com.example.proyectoappredsocial.models.Post;
import com.example.proyectoappredsocial.models.User;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BooksProvider {

    CollectionReference collectionReference;

    public BooksProvider() {
        collectionReference = FirebaseFirestore.getInstance().collection("Books");



    }



    public Task<Void> save(Book book) {

        DocumentReference documentReference = collectionReference.document();
        String id = documentReference.getId();
        book.setIdBook(id);

        return documentReference.set(book);

    }


    public Query getBooksByUserQueryOrderByTitle(String id) {

        return collectionReference.whereEqualTo("idUser", id).orderBy("title", Query.Direction.ASCENDING);


    }

    public Query getBooksByUserQuery(String id) {

        return collectionReference.whereEqualTo("idUser", id);


    }

    public Task<DocumentSnapshot> getBookById(String id) {
        return collectionReference.document(id).get();
    }


    public Query getBookByTitle(String bookTitle) {

        return collectionReference.orderBy("title").startAt(bookTitle).endAt(bookTitle + '\uf8ff');


    }


    public Task<Void> updateBookState(Boolean isRead, String idBook) {

        Map<String, Object> map = new HashMap<>();
        map.put("read", isRead);

        return collectionReference.document(idBook).update(map);


    }

    public Task<Void> updateBookRating(float rating, String idBook) {

        Map<String, Object> map = new HashMap<>();
        map.put("rating", rating);

        return collectionReference.document(idBook).update(map);


    }

    public Task<Void> delete(String id) {
        return collectionReference.document(id).delete();
    }


}
