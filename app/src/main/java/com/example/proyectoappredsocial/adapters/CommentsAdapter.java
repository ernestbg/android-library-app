package com.example.proyectoappredsocial.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectoappredsocial.R;
import com.example.proyectoappredsocial.models.Comentario;
import com.example.proyectoappredsocial.providers.UsersProvider;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsAdapter extends FirestoreRecyclerAdapter<Comentario, CommentsAdapter.ViewHolder> {

    Context context;
    UsersProvider usersProvider;

    public CommentsAdapter(FirestoreRecyclerOptions<Comentario> options, Context context) {
        super(options);
        this.context = context;
        usersProvider = new UsersProvider();
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Comentario comentario) {

        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(position);
        String commentId = documentSnapshot.getId();

        holder.tv_comentario.setText(comentario.getComment());
        String idUser = documentSnapshot.getString("idUser");
        getUserInfo(idUser, holder);


    }

    private void getUserInfo(String idUser, ViewHolder holder) {

        usersProvider.getUserTask(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {

                    if (documentSnapshot.contains("username")) {
                        String username = documentSnapshot.getString("username");
                        Log.i("username", username);
                        holder.tv_userName.setText(username);
                    }
                    if (documentSnapshot.contains("imageProfile")) {
                        String imageProfile = documentSnapshot.getString("imageProfile");
                        if (imageProfile != null) {
                            if (!imageProfile.isEmpty()) {
                                Picasso.with(context).load(imageProfile).into(holder.civ_comentario);


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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_comentarios, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_userName;
        TextView tv_comentario;
        CircleImageView civ_comentario;
        View viewHolder;

        public ViewHolder(View view) {
            super(view);
            tv_userName = view.findViewById(R.id.tv_userName);
            tv_comentario = view.findViewById(R.id.tv_comentario);
            civ_comentario = view.findViewById(R.id.civ_comment);
            viewHolder = view;

        }


    }


}
