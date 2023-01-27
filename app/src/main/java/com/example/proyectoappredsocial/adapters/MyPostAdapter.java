package com.example.proyectoappredsocial.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectoappredsocial.R;
import com.example.proyectoappredsocial.activities.EditPostActivity;
import com.example.proyectoappredsocial.activities.PostDetailActivity;
import com.example.proyectoappredsocial.models.Post;
import com.example.proyectoappredsocial.providers.AuthProvider;
import com.example.proyectoappredsocial.providers.LikesProvider;
import com.example.proyectoappredsocial.providers.PostProvider;
import com.example.proyectoappredsocial.providers.UsersProvider;
import com.example.proyectoappredsocial.utils.RelativeTime;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyPostAdapter extends FirestoreRecyclerAdapter<Post, MyPostAdapter.ViewHolder> {

    Context context;
    UsersProvider usersProvider;
    LikesProvider likesProvider;
    AuthProvider authProvider;
    PostProvider postProvider;


    public MyPostAdapter(FirestoreRecyclerOptions<Post> options, Context context) {
        super(options);
        this.context = context;
        usersProvider = new UsersProvider();
        likesProvider = new LikesProvider();
        authProvider = new AuthProvider();
        postProvider = new PostProvider();
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Post post) {

        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(position);
        String postId = documentSnapshot.getId();
        String relativeTime = RelativeTime.getTimeAgo(post.getTimestamp(), context);
        holder.tv_title.setText(post.getTitle());

        if (post.getIdUser().equals(authProvider.getUid())) {
            holder.iv_deletePost.setVisibility(View.VISIBLE);
            holder.iv_updatePost.setVisibility(View.VISIBLE);
        } else {

            holder.iv_deletePost.setVisibility(View.GONE);
            holder.iv_updatePost.setVisibility(View.GONE);
        }


        holder.tv_title.setText(post.getTitle());
        holder.tv_relativeTimeMyPost.setText(relativeTime);

        if (post.getImagePost() != null) {
            if (!post.getImagePost().isEmpty()) {
                Picasso.with(context).load(post.getImagePost()).into(holder.civ_myPost);
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

        holder.iv_updatePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, EditPostActivity.class);
                i.putExtra("postId", postId);
                context.startActivity(i);

            }
        });

        holder.iv_deletePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmarEliminarPost(postId);

            }
        });


    }


    private void confirmarEliminarPost(String postId) {

        new AlertDialog.Builder(context).setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Eliminar publicacion")
                .setMessage("¿Seguro que quieres eliminar esta publicacion?")
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deletePost(postId);
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void deletePost(String postId) {
        postProvider.delete(postId).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "el post se eliminó correctamente", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "error al eliminar", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_my_post, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_title;
        TextView tv_relativeTimeMyPost;
        CircleImageView civ_myPost;
        View viewHolder;
        ImageView iv_deletePost;
        ImageView iv_updatePost;

        public ViewHolder(View view) {
            super(view);
            civ_myPost = view.findViewById(R.id.civ_myPost);
            tv_relativeTimeMyPost = view.findViewById(R.id.tv_relativeTimeMyPost);
            tv_title = view.findViewById(R.id.tv_title);
            iv_deletePost = view.findViewById(R.id.iv_eliminarPost);
            iv_updatePost = view.findViewById(R.id.iv_updatePost);

            viewHolder = view;

        }


    }


}
