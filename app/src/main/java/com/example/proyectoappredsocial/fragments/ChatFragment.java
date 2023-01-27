package com.example.proyectoappredsocial.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.proyectoappredsocial.R;
import com.example.proyectoappredsocial.adapters.ChatsAdapter;

import com.example.proyectoappredsocial.models.Chat;

import com.example.proyectoappredsocial.providers.AuthProvider;
import com.example.proyectoappredsocial.providers.ChatsProvider;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {


    ChatsAdapter chatsAdapter;
    ChatsProvider chatsProvider;
    AuthProvider authProvider;
    RecyclerView recyclerView;
    View view;

    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView = view.findViewById(R.id.rv_chats);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        authProvider = new AuthProvider();
        chatsProvider = new ChatsProvider();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        Query query = chatsProvider.getAll(authProvider.getUid());

        FirestoreRecyclerOptions<Chat> options = new FirestoreRecyclerOptions.Builder<Chat>().setQuery(query, Chat.class).build();

        chatsAdapter = new ChatsAdapter(options, getContext());

        recyclerView.setAdapter(chatsAdapter);
        chatsAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();

        chatsAdapter.stopListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (chatsAdapter.getListenerRegistration() != null) {
            chatsAdapter.getListenerRegistration().remove();
        }

        if (chatsAdapter.getListenerRegistrationLastMessage() != null) {
            chatsAdapter.getListenerRegistrationLastMessage().remove();
        }


    }
}