package com.example.proyectoappredsocial.providers;

import com.example.proyectoappredsocial.models.Follower;
import com.example.proyectoappredsocial.models.Following;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class FollowingsProvider {

    CollectionReference collectionReference;

    public FollowingsProvider() {
        collectionReference= FirebaseFirestore.getInstance().collection("Following");
    }

    public Task<Void> create(Following following){
        return collectionReference.document().set(following);
    }

    public Task<DocumentSnapshot> getFollowersByUser(String idUser) {
        return collectionReference.document(idUser).get();
    }

    public Task<Void> delete(String id) {
        return collectionReference.document(id).delete();
    }



}
