package com.example.proyectoappredsocial.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectoappredsocial.R;

import com.example.proyectoappredsocial.activities.ChatActivity;
import com.example.proyectoappredsocial.models.Chat;
import com.example.proyectoappredsocial.providers.AuthProvider;
import com.example.proyectoappredsocial.providers.ChatsProvider;
import com.example.proyectoappredsocial.providers.MessagesProvider;
import com.example.proyectoappredsocial.providers.UsersProvider;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsAdapter extends FirestoreRecyclerAdapter<Chat, ChatsAdapter.ViewHolder> {

    Context context;
    UsersProvider usersProvider;
    AuthProvider authProvider;
    ChatsProvider chatsProvider;
    MessagesProvider messagesProvider;

    ListenerRegistration listenerRegistration;
    ListenerRegistration listenerRegistrationLastMessage;

    public ChatsAdapter(FirestoreRecyclerOptions<Chat> options, Context context) {
        super(options);
        this.context = context;
        usersProvider = new UsersProvider();
        authProvider = new AuthProvider();
        chatsProvider = new ChatsProvider();
        messagesProvider = new MessagesProvider();
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Chat chat) {

        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(position);
        String idChat = documentSnapshot.getId();
        Log.i("erny", idChat);

        if (authProvider.getUid().equals(chat.getIdUser1())) {
            getUserInfo(chat.getIdUser2(), holder);


        } else {
            getUserInfo(chat.getIdUser1(), holder);

        }

        holder.viewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToChatActivity(idChat, chat.getIdUser1(), chat.getIdUser2());
            }
        });

        getLastMessage(idChat, holder.tv_lastMessage);

        String idSender = "";

        if (authProvider.getUid().equals(chat.getIdUser1())) {
            idSender = chat.getIdUser2();
        } else {
            idSender = chat.getIdUser1();
        }
        getMessageNotRead(idChat, idSender, holder.tv_messageNotRead, holder.fl_messageNotRead);

    }

    private void getMessageNotRead(String idChat, String idSender, TextView tv_messageNotRead, FrameLayout fl_messageNotRead) {

        listenerRegistration = messagesProvider.getMessagesByChatAndSender(idChat, idSender).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                if (queryDocumentSnapshots != null) {
                    int size = queryDocumentSnapshots.size();
                    if (size > 0) {
                        fl_messageNotRead.setVisibility(View.VISIBLE);
                        tv_messageNotRead.setText(String.valueOf(size));
                    } else {
                        fl_messageNotRead.setVisibility(View.GONE);
                    }
                }

            }
        });

    }

    public ListenerRegistration getListenerRegistration() {
        return listenerRegistration;
    }

    public ListenerRegistration getListenerRegistrationLastMessage() {
        return listenerRegistrationLastMessage;
    }

    private void getLastMessage(String idChat, TextView tv_lastMessage) {

        listenerRegistrationLastMessage= messagesProvider.getLastMessage(idChat).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                if (queryDocumentSnapshots != null) {
                    int size = queryDocumentSnapshots.size();
                    if (size > 0) {
                        String lastMessage = queryDocumentSnapshots.getDocuments().get(0).getString("message");
                        tv_lastMessage.setText(lastMessage);
                    }
                }

            }
        });
    }

    private void goToChatActivity(String idChat, String idUser1, String idUser2) {

        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("idChat", idChat);
        intent.putExtra("idUser1", idUser1);
        intent.putExtra("idUser2", idUser2);
        context.startActivity(intent);
    }

    private void getUserInfo(String idUser, ViewHolder holder) {

        usersProvider.getUserTask(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {

                    if (documentSnapshot.contains("username")) {
                        String username = documentSnapshot.getString("username");
                        Log.i("username", username);
                        holder.tv_usernameChat.setText(username);
                    }
                    if (documentSnapshot.contains("imageProfile")) {
                        String imageProfile = documentSnapshot.getString("imageProfile");
                        if (imageProfile != null) {
                            if (!imageProfile.isEmpty()) {
                                Picasso.with(context).load(imageProfile).into(holder.civ_chat);


                            }
                        }
                    }
                }
            }
        });

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_chat, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_usernameChat;
        TextView tv_lastMessage;
        TextView tv_messageNotRead;
        CircleImageView civ_chat;
        FrameLayout fl_messageNotRead;
        View viewHolder;

        public ViewHolder(View view) {
            super(view);
            tv_usernameChat = view.findViewById(R.id.tv_usernameChat);
            tv_lastMessage = view.findViewById(R.id.tv_lastMessage);
            tv_messageNotRead = view.findViewById(R.id.tv_messageNotRead);
            civ_chat = view.findViewById(R.id.civ_chat);
            fl_messageNotRead = view.findViewById(R.id.fl_messageNotRead);
            viewHolder = view;

        }


    }


}
