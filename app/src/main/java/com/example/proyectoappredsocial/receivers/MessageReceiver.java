package com.example.proyectoappredsocial.receivers;

import static com.example.proyectoappredsocial.services.MyFirebaseMessagingClient.NOTIFICATION_REPLY;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.RemoteInput;

import com.example.proyectoappredsocial.activities.ChatActivity;
import com.example.proyectoappredsocial.models.FCMBody;
import com.example.proyectoappredsocial.models.FCMResponse;
import com.example.proyectoappredsocial.models.Message;
import com.example.proyectoappredsocial.providers.MessagesProvider;
import com.example.proyectoappredsocial.providers.NotificationProvider;
import com.example.proyectoappredsocial.providers.TokenProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageReceiver extends BroadcastReceiver {

    String extraIdSender;
    String extraIdReceiver;
    String extraIdChat;
    String extraUsernameSender;
    String extraUsernameReceiver;
    String extraImageSender;
    String extraImageReceiver;
    int extraIdNotification;

    TokenProvider tokenProvider;
    NotificationProvider notificationProvider;


    @Override
    public void onReceive(Context context, Intent intent) {

        tokenProvider = new TokenProvider();
        notificationProvider = new NotificationProvider();

        extraIdSender = intent.getExtras().getString("idSender");
        extraIdReceiver = intent.getExtras().getString("idReceiver");
        extraIdChat = intent.getExtras().getString("idChat");
        extraUsernameSender = intent.getExtras().getString("usernameSender");
        extraUsernameReceiver = intent.getExtras().getString("usernameReceiver");
        extraImageSender = intent.getExtras().getString("imageSender");
        extraImageReceiver = intent.getExtras().getString("imageReceiver");
        extraIdNotification = intent.getExtras().getInt("idNotification");

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(extraIdNotification);

        String message = getMessageText(intent).toString();

        sendMessage(message);
    }

    private void sendMessage(String textMessage) {
        Message message = new Message();

        message.setIdChat(extraIdChat);
        message.setIdSender(extraIdReceiver);
        message.setIdReceiver(extraIdSender);
        message.setTimestamp(new Date().getTime());
        message.setViewed(false);
        message.setMessage(textMessage);

        MessagesProvider messagesProvider = new MessagesProvider();
        messagesProvider.create(message).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {

                    getToken(message);

                }
            }
        });
    }

    private void getToken(Message message) {

        tokenProvider.getToken(extraIdSender).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("token")) {
                        String token = documentSnapshot.getString("token");
                        Gson gson = new Gson();
                        ArrayList<Message> messages_al = new ArrayList<>();
                        messages_al.add(message);
                        String messages = gson.toJson(messages_al);
                        sendNotification(token, messages, message);
                    }
                }
            }
        });

    }

    private CharSequence getMessageText(Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            return remoteInput.getCharSequence(NOTIFICATION_REPLY);
        }
        return null;
    }

    private void sendNotification(String token, String messages, Message message) {

        Map<String, String> data = new HashMap<>();
        data.put("title", "Nuevo mensaje");
        data.put("body", message.getMessage());
        data.put("idNotification", String.valueOf(extraIdNotification));
        data.put("messages", messages);
        data.put("usernameSender", extraUsernameReceiver.toUpperCase().charAt(0) + extraUsernameReceiver.substring(1, extraUsernameReceiver.length()).toLowerCase());
        data.put("usernameReceiver", extraUsernameSender.toUpperCase().charAt(0) + extraUsernameSender.substring(1, extraUsernameSender.length()).toLowerCase());
        data.put("idSender", message.getIdSender());
        data.put("idReceiver", message.getIdReceiver());
        data.put("idChat", message.getIdChat());
        data.put("imageSender", extraIdReceiver);
        data.put("imageReceiver", extraImageSender);


        FCMBody body = new FCMBody(token, "high", "4500s", data);
        notificationProvider.sendNotification(body).enqueue(new Callback<FCMResponse>() {
            @Override
            public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {


            }

            @Override
            public void onFailure(Call<FCMResponse> call, Throwable t) {

                Log.d("error", "El error fue " + t.getMessage());

            }
        });

    }


}
