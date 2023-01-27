package com.example.proyectoappredsocial.providers;

import com.example.proyectoappredsocial.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UsersProvider {


    private CollectionReference collectionReference;

    public UsersProvider() {
        collectionReference = FirebaseFirestore.getInstance().collection("Users");

    }

    public Task<DocumentSnapshot> getUserTask(String id) {

        return collectionReference.document(id).get();

    }

    public DocumentReference getUserTaskRealtime(String id) {

        return collectionReference.document(id);

    }

    public Query getUserQuery(String idUser) {

        return collectionReference.whereEqualTo("id", idUser);


    }

    public Task<Void> create(User user) {
        return collectionReference.document(user.getId()).set(user);


    }

    public Task<Void> update(User user) {

        Map<String, Object> map = new HashMap<>();
        map.put("username", user.getUsername());
        map.put("timestamp", new Date().getTime());
        map.put("imageProfile", user.getImageProfile());

        return collectionReference.document(user.getId()).update(map);


    }

    public Task<Void> updateOnline(String idUser, boolean status) {

        Map<String, Object> map = new HashMap<>();
        map.put("online", status);
        map.put("lastConnect", new Date().getTime());

        return collectionReference.document(idUser).update(map);


    }

    public Task<Void> delete(String id) {
        return collectionReference.document(id).delete();
    }

}
