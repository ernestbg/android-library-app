package com.example.proyectoappredsocial.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyectoappredsocial.R;
import com.example.proyectoappredsocial.adapters.MessagesAdapter;
import com.example.proyectoappredsocial.adapters.MyPostAdapter;
import com.example.proyectoappredsocial.models.Chat;
import com.example.proyectoappredsocial.models.FCMBody;
import com.example.proyectoappredsocial.models.FCMResponse;
import com.example.proyectoappredsocial.models.Message;
import com.example.proyectoappredsocial.models.Post;
import com.example.proyectoappredsocial.providers.AuthProvider;
import com.example.proyectoappredsocial.providers.ChatsProvider;
import com.example.proyectoappredsocial.providers.MessagesProvider;
import com.example.proyectoappredsocial.providers.NotificationProvider;
import com.example.proyectoappredsocial.providers.TokenProvider;
import com.example.proyectoappredsocial.providers.UsersProvider;
import com.example.proyectoappredsocial.utils.RelativeTime;
import com.example.proyectoappredsocial.utils.ViewedMessageHelper;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    String extraIdUser1;
    String extraIdUser2;
    String extraIdChat;
    long idNotificationChat;

    ChatsProvider chatsProvider;
    MessagesProvider messagesProvider;
    AuthProvider authProvider;
    UsersProvider usersProvider;
    NotificationProvider notificationProvider;
    TokenProvider tokenProvider;

    View actionBarView;

    EditText et_message;
    ImageView iv_send_message;

    CircleImageView civ_profile;
    ImageView iv_arrowBack;
    TextView tv_username;
    TextView tv_relativeTime;

    RecyclerView rv_message;

    MessagesAdapter messagesAdapter;
    LinearLayoutManager linearLayoutManager;

    ListenerRegistration listenerRegistration;

    String myUsername;
    String usernameChat;
    String imageReceiver = "";
    String imageSender = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatsProvider = new ChatsProvider();
        messagesProvider = new MessagesProvider();
        authProvider = new AuthProvider();
        usersProvider = new UsersProvider();
        notificationProvider = new NotificationProvider();
        tokenProvider = new TokenProvider();

        et_message = findViewById(R.id.et_message);
        iv_send_message = findViewById(R.id.iv_send_message);
        rv_message = findViewById(R.id.rv_message);

        linearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        linearLayoutManager.setStackFromEnd(true);
        rv_message.setLayoutManager(linearLayoutManager);

        extraIdUser1 = getIntent().getStringExtra("idUser1");
        extraIdUser2 = getIntent().getStringExtra("idUser2");
        extraIdChat = getIntent().getStringExtra("idChat");


        showCustomToolbar(R.layout.custom_chat_toolbar);
        getMyInfoUser();


        iv_send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });


        checkIfChatExists();
        Log.i("erny", "hola");


    }

   /* private BroadcastReceiver networkStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = manager.getActiveNetworkInfo();
            onNetworkChange(ni);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
    }


    private void onNetworkChange(NetworkInfo networkInfo) {
        if (networkInfo != null) {
            if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                // CONNECTED
                Toast.makeText(ChatActivity.this, "Tienes conexión", Toast.LENGTH_LONG).show();
                ViewedMessageHelper.updateOnline(true, ChatActivity.this);
                getUserInfo();


            } else {
                // DISCONNECTED
                Toast.makeText(ChatActivity.this, "No tienes conexión", Toast.LENGTH_LONG).show();
                ViewedMessageHelper.updateOnline(false, ChatActivity.this);
                getUserInfo();


            }
        }
    }*/

    @Override
    public void onStart() {
        super.onStart();

        /*ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            // Si hay conexión a Internet en este momento

            ViewedMessageHelper.updateOnline(true, ChatActivity.this);

            Toast.makeText(ChatActivity.this, "Tienes conexión", Toast.LENGTH_LONG).show();

        } else {
            // No hay conexión a Internet en este momento
            Toast.makeText(ChatActivity.this, "No tienes conexión", Toast.LENGTH_LONG).show();

            ViewedMessageHelper.updateOnline(false, ChatActivity.this);

            tv_relativeTime.setText("Hace un momento");
        }*/

        ViewedMessageHelper.updateOnline(true, ChatActivity.this);


        if (messagesAdapter != null) {
            messagesAdapter.startListening();
        }


    }

    @Override
    public void onStop() {
        super.onStop();
        messagesAdapter.stopListening();

    }


    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false, ChatActivity.this);
        //unregisterReceiver(networkStateReceiver);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }

    private void getMessageChat() {
        Query query = messagesProvider.getMessageByChat(extraIdChat);
        FirestoreRecyclerOptions<Message> options = new FirestoreRecyclerOptions.Builder<Message>().setQuery(query, Message.class).build();
        messagesAdapter = new MessagesAdapter(options, ChatActivity.this);
        rv_message.setAdapter(messagesAdapter);
        messagesAdapter.startListening();
        messagesAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                updateViewed();
                int numberOfMessages = messagesAdapter.getItemCount();
                int lastMessagePosition = linearLayoutManager.findLastVisibleItemPosition();

                if (lastMessagePosition == -1 || (positionStart >= (numberOfMessages - 1) && lastMessagePosition == (positionStart - 1))) {

                    rv_message.scrollToPosition(positionStart);
                }
            }
        });


    }

    private void sendMessage() {

        String txt_message = et_message.getText().toString();
        if (!txt_message.isEmpty()) {

            Message message = new Message();
            message.setIdChat(extraIdChat);
            if (authProvider.getUid().equals(extraIdUser1)) {

                message.setIdSender(extraIdUser1);
                message.setIdReceiver(extraIdUser2);

            } else {
                message.setIdSender(extraIdUser2);
                message.setIdReceiver(extraIdUser1);

            }
            message.setTimestamp(new Date().getTime());
            message.setViewed(false);
            message.setIdChat(extraIdChat);
            message.setMessage(txt_message);
            messagesProvider.create(message).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful()) {
                        et_message.setText("");
                        messagesAdapter.notifyDataSetChanged();
                        getToken(message);

                    } else {
                        Toast.makeText(ChatActivity.this, "El mensaje no se pudo enviar", Toast.LENGTH_LONG).show();

                    }
                }


            });
        }
    }

    private void showCustomToolbar(int resource) {

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        actionBarView = inflater.inflate(resource, null);
        actionBar.setCustomView(actionBarView);
        civ_profile = actionBarView.findViewById(R.id.civ_profile);
        tv_username = actionBarView.findViewById(R.id.tv_username);
        tv_relativeTime = actionBarView.findViewById(R.id.tv_relativeTime);
        iv_arrowBack = actionBarView.findViewById(R.id.iv_arrowBack);

        iv_arrowBack.setOnClickListener(view -> finish());


        getUserInfo();

    }

    private void getUserInfo() {

        String idUserInfo = "";
        if (authProvider.getUid().equals(extraIdUser1)) {
            idUserInfo = extraIdUser2;

        } else {
            idUserInfo = extraIdUser1;
        }

        listenerRegistration = usersProvider.getUserTaskRealtime(idUserInfo).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot.exists()) {

                    if (documentSnapshot.contains("username")) {
                        usernameChat = documentSnapshot.getString("username");
                        tv_username.setText(usernameChat);
                    }

                    if (documentSnapshot.contains("online")) {
                        boolean online = documentSnapshot.getBoolean("online");
                        if (online) {
                            tv_relativeTime.setText("En línea");
                        } else if (documentSnapshot.contains("lastConnect")) {
                            long lastConnect = documentSnapshot.getLong("lastConnect");
                            String relativeTime = RelativeTime.getTimeAgo(lastConnect, ChatActivity.this);
                            tv_relativeTime.setText(relativeTime);

                        }
                    }

                    if (documentSnapshot.contains("imageProfile")) {
                        imageReceiver = documentSnapshot.getString("imageProfile");
                        if (imageReceiver != null) {
                            if (!imageReceiver.equals("")) {
                                Picasso.with(ChatActivity.this).load(imageReceiver).into(civ_profile);
                            }

                        }
                    }
                }
            }
        });
    }


    private void createChat() {

        Chat chat = new Chat();
        chat.setIdUser1(extraIdUser1);
        chat.setIdUser2(extraIdUser2);
        chat.setId(extraIdUser1 + extraIdUser2);
        Random random = new Random();
        int num = random.nextInt(1000000);
        chat.setIdNotification(num);
        idNotificationChat = num;

        ArrayList<String> ids = new ArrayList<>();
        ids.add(extraIdUser1);
        ids.add(extraIdUser2);
        chat.setIds(ids);
        chat.setWriting(false);
        chat.setTimestamp(new Date().getTime());
        chatsProvider.create(chat);
        extraIdChat = chat.getId();
        getMessageChat();
    }

    private void checkIfChatExists() {

        chatsProvider.getChatByUser1AndUser2(extraIdUser1, extraIdUser2).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int size = queryDocumentSnapshots.size();

                if (size == 0) {


                    createChat();
                } else {
                    extraIdChat = queryDocumentSnapshots.getDocuments().get(0).getId();
                    idNotificationChat = queryDocumentSnapshots.getDocuments().get(0).getLong("idNotification");
                    getMessageChat();
                    updateViewed();


                }
            }
        });
    }

    private void updateViewed() {

        String idSender = "";

        if (authProvider.getUid().equals(extraIdUser1)) {

            idSender = extraIdUser2;
        } else {

            idSender = extraIdUser1;

        }

        messagesProvider.getMessagesByChatAndSender(extraIdChat, idSender).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {

                    messagesProvider.updateViewed(documentSnapshot.getId(), true);

                }

            }
        });
    }

    private void getToken(Message message) {

        String idUser = "";
        if (authProvider.getUid().equals(extraIdUser1)) {
            idUser = extraIdUser2;
        } else {
            idUser = extraIdUser1;
        }

        tokenProvider.getToken(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("token")) {
                        String token = documentSnapshot.getString("token");
                        getLastThreeMessages(message, token);


                    }
                } else {
                    Toast.makeText(ChatActivity.this, "El token no existe", Toast.LENGTH_LONG).show();

                }
            }
        });


    }

    private void getLastThreeMessages(Message message, String token) {

        messagesProvider.getLastThreeMessagesByChatAndSender(extraIdChat, authProvider.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                ArrayList<Message> messageArrayList = new ArrayList<>();

                for (DocumentSnapshot d : queryDocumentSnapshots.getDocuments()) {
                    if (d.exists()) {
                        Message message = d.toObject(Message.class);
                        messageArrayList.add(message);

                    }
                }

                if (messageArrayList.size() == 0) {
                    messageArrayList.add(message);

                }

                Collections.reverse(messageArrayList);

                Gson gson = new Gson();
                String messages = gson.toJson(messageArrayList);


                sendNotification(token, messages, message);


            }
        });
    }

    private void sendNotification(String token, String messages, Message message) {

        Map<String, String> data = new HashMap<>();
        data.put("title", "Nuevo mensaje");
        data.put("body", message.getMessage());
        data.put("idNotification", String.valueOf(idNotificationChat));
        data.put("messages", messages);
        data.put("usernameSender", myUsername.toUpperCase().charAt(0) + myUsername.substring(1, myUsername.length()).toLowerCase());
        data.put("usernameReceiver", usernameChat.toUpperCase().charAt(0) + usernameChat.substring(1, usernameChat.length()).toLowerCase());
        data.put("idSender", message.getIdSender());
        data.put("idReceiver", message.getIdReceiver());
        data.put("idChat", message.getIdChat());


        if (imageSender.equals("")) {

            imageSender = "IMAGEN NO VALIDA";
        }

        if (imageReceiver.equals("")) {
            imageReceiver = "IMAGEN NO VALIDA";
        }

        data.put("imageSender", imageSender);
        data.put("imageReceiver", imageReceiver);

        String idSender = "";
        if (authProvider.getUid().equals(extraIdUser1)) {
            idSender = extraIdUser2;
        } else {
            idSender = extraIdUser1;
        }
        messagesProvider.getLastMessageSender(extraIdChat, idSender).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int size = queryDocumentSnapshots.size();
                String lastMessage = "";

                if (size > 0) {
                    lastMessage = queryDocumentSnapshots.getDocuments().get(0).getString("message");
                    data.put("lastMessage", lastMessage);

                }

                FCMBody body = new FCMBody(token, "high", "4500s", data);
                notificationProvider.sendNotification(body).enqueue(new Callback<FCMResponse>() {
                    @Override
                    public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                        if (response.body() != null) {
                            if (response.body().getSuccess() == 1) {

                                //Toast.makeText(ChatActivity.this, "Notificación enviada", Toast.LENGTH_LONG).show();
                            } else {

                                Toast.makeText(ChatActivity.this, "Error al enviar notificación", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(ChatActivity.this, "Error al enviar notificación", Toast.LENGTH_LONG).show();

                        }

                    }

                    @Override
                    public void onFailure(Call<FCMResponse> call, Throwable t) {

                    }
                });

            }
        });

    }

    private void getMyInfoUser() {

        usersProvider.getUserTask(authProvider.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("username")) {
                        myUsername = documentSnapshot.getString("username");
                    }

                    if (documentSnapshot.contains("imageProfile")) {
                        imageSender = documentSnapshot.getString("imageProfile");
                    }
                }
            }
        });
    }
}