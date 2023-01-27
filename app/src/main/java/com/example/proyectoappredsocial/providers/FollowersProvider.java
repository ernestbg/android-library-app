package com.example.proyectoappredsocial.providers;

import com.example.proyectoappredsocial.models.Comentario;
import com.example.proyectoappredsocial.models.Follower;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class FollowersProvider {

    CollectionReference collectionReference;

    public FollowersProvider() {
        collectionReference = FirebaseFirestore.getInstance().collection("Followers");
    }

    public Task<Void> create(Follower follower) {
        return collectionReference.document().set(follower);
    }

    public Query getFollowersByUser(String idUser) {
        return collectionReference.whereEqualTo("idUser", idUser);

    }
    public Task<Void> delete(String id) {
        return collectionReference.document(id).delete();
    }

}
