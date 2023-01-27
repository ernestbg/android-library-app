package com.example.proyectoappredsocial.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectoappredsocial.R;
import com.example.proyectoappredsocial.activities.PostDetailActivity;
import com.example.proyectoappredsocial.models.Like;
import com.example.proyectoappredsocial.models.Post;
import com.example.proyectoappredsocial.providers.AuthProvider;
import com.example.proyectoappredsocial.providers.LikesProvider;
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

import java.util.Date;

public class PostAdapter extends FirestoreRecyclerAdapter<Post, PostAdapter.ViewHolder> {

    Context context;
    UsersProvider usersProvider;
    AuthProvider authProvider;
    TextView tv_numeroPublicaciones;
    ListenerRegistration listenerRegistration;


    public PostAdapter(FirestoreRecyclerOptions<Post> options, Context context) {
        super(options);
        this.context = context;
        usersProvider = new UsersProvider();
        authProvider = new AuthProvider();
    }

    public PostAdapter(FirestoreRecyclerOptions<Post> options, Context context, TextView textView) {
        super(options);
        this.context = context;
        usersProvider = new UsersProvider();
        authProvider = new AuthProvider();
        tv_numeroPublicaciones = textView;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Post post) {

        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(position);
        String postId = documentSnapshot.getId();

        if (tv_numeroPublicaciones != null) {
            int numeroPublicaciones = getSnapshots().size();
            tv_numeroPublicaciones.setText(String.valueOf(numeroPublicaciones));
        }

        holder.tv_title.setText(post.getTitle());


        if (post.getImagePost() != null) {
            if (!post.getImagePost().isEmpty()) {
                Picasso.with(context).load(post.getImagePost()).into(holder.iv_post);
            }
        }

        holder.viewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, PostDetailActivity.class);
                i.putExtra("postId", postId);
                context.startActivity(i);

            }
        });


        getUserInfo(post.getIdUser(), holder);
    }


    private void getUserInfo(String idUser, ViewHolder holder) {

        usersProvider.getUserTask(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("username")) {
                        String username = documentSnapshot.getString("username");
                        holder.tv_userName.setText(username);
                    }
                }
            }
        });


    }

    public ListenerRegistration getListenerRegistration() {
        return listenerRegistration;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_post2, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_title;
        ImageView iv_post;
        View viewHolder;
        TextView tv_userName;

        public ViewHolder(View view) {
            super(view);
            tv_title = view.findViewById(R.id.tv_titleCardPost);

            iv_post = view.findViewById(R.id.iv_cardPost);
            tv_userName = view.findViewById(R.id.tv_author);
            viewHolder = view;

        }


    }


}
