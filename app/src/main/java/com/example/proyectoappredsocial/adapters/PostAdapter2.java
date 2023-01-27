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

public class PostAdapter2 extends FirestoreRecyclerAdapter<Post, PostAdapter2.ViewHolder> {

    Context context;
    UsersProvider usersProvider;
    LikesProvider likesProvider;
    AuthProvider authProvider;
    TextView tv_numeroPublicaciones;
    ListenerRegistration listenerRegistration;


    public PostAdapter2(FirestoreRecyclerOptions<Post> options, Context context) {
        super(options);
        this.context = context;
        usersProvider = new UsersProvider();
        likesProvider = new LikesProvider();
        authProvider = new AuthProvider();
    }

    public PostAdapter2(FirestoreRecyclerOptions<Post> options, Context context, TextView textView) {
        super(options);
        this.context = context;
        usersProvider = new UsersProvider();
        likesProvider = new LikesProvider();
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
        holder.tv_descriptionCardPost.setText(post.getDescription());

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

        holder.iv_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Like like = new Like();
                like.setIdUser(authProvider.getUid());
                like.setIdPost(postId);
                like.setDate(new Date().getTime());
                like(like, holder);
            }
        });

        getUserInfo(post.getIdUser(), holder);
        obtenerNumeroLikesPost(postId, holder);
        comprobarSiExisteLike(postId, authProvider.getUid(), holder);
    }

    private void obtenerNumeroLikesPost(String idPost, ViewHolder holder) {
        listenerRegistration = likesProvider.getLikesByPost(idPost).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                if (queryDocumentSnapshots != null) {
                    int numeroLikes = queryDocumentSnapshots.size();
                    holder.tv_likes.setText(String.valueOf(numeroLikes));
                }

            }
        });
    }

    private void like(Like like, ViewHolder holder) {
        likesProvider.getLikeByPostAndUser(like.getIdPost(), authProvider.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numeroDocumentos = queryDocumentSnapshots.size();
                if (numeroDocumentos > 0) {
                    String idLike = queryDocumentSnapshots.getDocuments().get(0).getId();
                    holder.iv_like.setImageResource(R.drawable.ic_like);
                    likesProvider.delete(idLike);
                } else {
                    holder.iv_like.setImageResource(R.drawable.ic_like_black);

                    likesProvider.create(like);
                }
            }
        });


    }

    private void comprobarSiExisteLike(String idPost, String idUser, ViewHolder holder) {
        likesProvider.getLikeByPostAndUser(idPost, idUser).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numeroDocumentos = queryDocumentSnapshots.size();
                if (numeroDocumentos > 0) {
                    holder.iv_like.setImageResource(R.drawable.ic_like_black);

                } else {
                    holder.iv_like.setImageResource(R.drawable.ic_like);

                }
            }
        });


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
        TextView tv_descriptionCardPost;
        ImageView iv_post;
        ImageView iv_like;
        View viewHolder;
        TextView tv_userName;
        TextView tv_likes;

        public ViewHolder(View view) {
            super(view);
            tv_title = view.findViewById(R.id.tv_titleCardPost);
            tv_descriptionCardPost = view.findViewById(R.id.tv_descriptionCardPost);
            iv_post = view.findViewById(R.id.iv_cardPost);
            tv_userName = view.findViewById(R.id.tv_author);
            iv_like = view.findViewById(R.id.iv_like);
            tv_likes = view.findViewById(R.id.tv_likes);
            viewHolder = view;

        }


    }


}
