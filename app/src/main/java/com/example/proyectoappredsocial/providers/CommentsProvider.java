package com.example.proyectoappredsocial.providers;

import com.example.proyectoappredsocial.models.Comentario;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class CommentsProvider {
    CollectionReference collectionReference;

    public CommentsProvider() {
        collectionReference= FirebaseFirestore.getInstance().collection("Comments");
    }

    public Task<Void> create(Comentario comentario){
        return collectionReference.document().set(comentario);
    }

    public Query getCommentsByPost(String idPost){

        return collectionReference.whereEqualTo("idPost", idPost);

    }

    public Query getCommentsByUserQuery(String idUser) {

        return collectionReference.whereEqualTo("idUser", idUser);


    }

    public Task<Void> delete(String id) {
        return collectionReference.document(id).delete();
    }

}
