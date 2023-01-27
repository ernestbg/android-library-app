package com.example.proyectoappredsocial.providers;

import com.example.proyectoappredsocial.models.Read;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ReadsProvider {

    CollectionReference collectionReference;

    public ReadsProvider() {
        collectionReference = FirebaseFirestore.getInstance().collection("Reads");

    }

    public Task<Void> create(Read read) {
        DocumentReference documentReference = collectionReference.document();
        String id = documentReference.getId();


        read.setId(id);
        return documentReference.set(read);

    }

    public Query getReadByUser(String idUser) {

        return collectionReference.whereEqualTo("idUser", idUser);


    }

    public Query getReadByBookAndUser(String idBook, String idUser) {

        return collectionReference.whereEqualTo("idBook", idBook).whereEqualTo("idUser", idUser);


    }


    public Task<Void> delete(String id) {
        return collectionReference.document(id).delete();
    }


    /*public Task<QuerySnapshot> getReadsByUserAndDateChallenge(String idUser) {




    }*/


}
