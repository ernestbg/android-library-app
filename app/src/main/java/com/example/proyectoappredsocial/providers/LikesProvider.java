package com.example.proyectoappredsocial.providers;

import android.util.Log;

import com.example.proyectoappredsocial.models.Like;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class LikesProvider {

    CollectionReference collectionReference;

    public LikesProvider() {
        collectionReference = FirebaseFirestore.getInstance().collection("Likes");

    }

    public Task<Void> create(Like like) {
        DocumentReference documentReference = collectionReference.document();
        String id = documentReference.getId();


        like.setId(id);
        return documentReference.set(like);

    }

    public Query getLikesByPost(String idPost) {
        return collectionReference.whereEqualTo("idPost", idPost);

    }

    public Query getAll() {
        return collectionReference;
    }

    public Query getLikeByPostAndUser(String idPost, String idUser) {
        return collectionReference.whereEqualTo("idPost", idPost).whereEqualTo("idUser", idUser);
    }

    public Query getLikeByUser(String idUser) {
        return collectionReference.whereEqualTo("idUser", idUser);
    }

    public Task<Void> delete(String id) {
        return collectionReference.document(id).delete();
    }
}
