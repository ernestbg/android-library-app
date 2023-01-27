package com.example.proyectoappredsocial.providers;

import com.example.proyectoappredsocial.models.Post;
import com.example.proyectoappredsocial.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PostProvider {

    CollectionReference collectionReference;

    public PostProvider() {
        collectionReference = FirebaseFirestore.getInstance().collection("Posts");

    }

    public Task<Void> save(Post post) {
        return collectionReference.document().set(post);

    }

    public Query getAll() {
        return collectionReference.orderBy("timestamp", Query.Direction.DESCENDING);
    }

    public Query getPostByUserQuery(String id) {

        return collectionReference.whereEqualTo("idUser", id);

    }



    public Query getPostByCategoryDate(String category) {

        return collectionReference.whereEqualTo("category", category).orderBy("timestamp", Query.Direction.DESCENDING);

    }

    public Query getPostByTitle(String postTitle) {

        return collectionReference.orderBy("title").startAt(postTitle).endAt(postTitle + '\uf8ff');


    }

    public Task<Void> update(Post post) {

        Map<String, Object> map = new HashMap<>();
        map.put("title", post.getTitle());
        map.put("description", post.getDescription());
        map.put("imagePost", post.getImagePost());
        map.put("timestamp", new Date().getTime());

        return collectionReference.document(post.getId()).update(map);


    }


    public Task<DocumentSnapshot> getPostById(String id) {
        return collectionReference.document(id).get();
    }

    public Task<Void> delete(String id) {
        return collectionReference.document(id).delete();
    }


}
