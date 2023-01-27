package com.example.proyectoappredsocial.providers;

import androidx.annotation.NonNull;

import com.example.proyectoappredsocial.models.Token;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

public class TokenProvider {

    CollectionReference collectionReference;

    public TokenProvider() {
        collectionReference = FirebaseFirestore.getInstance().collection("Token");
    }

    public void create(String idUser) {
        if (idUser == null) {

            return;
        }


        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isComplete()) {
                    Token token = new Token (task.getResult());
                    collectionReference.document(idUser).set(token);



                }
            }
        });

    }

    public Task<DocumentSnapshot> getToken(String idUser){

        return collectionReference.document(idUser).get();
    }



}



