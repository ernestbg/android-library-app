package com.example.proyectoappredsocial.providers;

import com.example.proyectoappredsocial.models.Message;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class MessagesProvider {

    CollectionReference collectionReference;

    public MessagesProvider() {
        collectionReference = FirebaseFirestore.getInstance().collection("Messages");
    }

    public Task<Void> create(Message message) {
        DocumentReference documentReference = collectionReference.document();
        message.setId(documentReference.getId());
        return documentReference.set(message);


    }


    public Query getMessageByChat(String idChat) {
        return collectionReference.whereEqualTo("idChat", idChat).orderBy("timestamp", Query.Direction.ASCENDING);

    }

    public Query getMessagesByChatAndSender(String idChat, String idSender) {
        return collectionReference.whereEqualTo("idChat", idChat).whereEqualTo("idSender", idSender).whereEqualTo("viewed", false);

    }


    public Query getLastThreeMessagesByChatAndSender(String idChat, String idSender) {
        return collectionReference
                .whereEqualTo("idChat", idChat)
                .whereEqualTo("idSender", idSender)
                .whereEqualTo("viewed", false)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(3);

    }

    public Query getLastMessage(String idChat) {
        return collectionReference.whereEqualTo("idChat", idChat).orderBy("timestamp", Query.Direction.DESCENDING).limit(1);

    }

    public Query getLastMessageSender(String idChat, String idSender) {
        return collectionReference
                .whereEqualTo("idChat", idChat)
                .whereEqualTo("idSender", idSender)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(1);
    }



    public Task<Void> updateViewed(String idDocument, boolean state) {

        Map<String, Object> map = new HashMap<>();
        map.put("viewed", state);
        return collectionReference.document(idDocument).update(map);

    }
}
