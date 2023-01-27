package com.example.proyectoappredsocial.providers;

import com.example.proyectoappredsocial.models.Challenge;
import com.example.proyectoappredsocial.models.Comentario;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ChallengeProvider {

    CollectionReference collectionReference;

    public ChallengeProvider() {
        collectionReference = FirebaseFirestore.getInstance().collection("Challenges");


    }

    public Task<Void> create(Challenge challenge) {

        DocumentReference documentReference = collectionReference.document();
        String id = documentReference.getId();


        challenge.setId(id);
        return documentReference.set(challenge);
    }

    public Query getChallengesByUser(String idUser) {

        return collectionReference.whereEqualTo("idUser", idUser);


    }

    public Task<Void> delete(String id) {
        return collectionReference.document(id).delete();
    }

}
