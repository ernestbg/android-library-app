package com.example.proyectoappredsocial.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyectoappredsocial.R;
import com.example.proyectoappredsocial.adapters.CommentsAdapter;
import com.example.proyectoappredsocial.adapters.PostAdapter2;
import com.example.proyectoappredsocial.models.Comentario;
import com.example.proyectoappredsocial.models.FCMBody;
import com.example.proyectoappredsocial.models.FCMResponse;
import com.example.proyectoappredsocial.models.Like;
import com.example.proyectoappredsocial.providers.AuthProvider;
import com.example.proyectoappredsocial.providers.CommentsProvider;
import com.example.proyectoappredsocial.providers.LikesProvider;
import com.example.proyectoappredsocial.providers.NotificationProvider;
import com.example.proyectoappredsocial.providers.PostProvider;
import com.example.proyectoappredsocial.providers.TokenProvider;
import com.example.proyectoappredsocial.providers.UsersProvider;
import com.example.proyectoappredsocial.utils.RelativeTime;
import com.example.proyectoappredsocial.utils.ViewedMessageHelper;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostDetailActivity extends AppCompatActivity {
    ImageView iv_postDetail;
    ImageView iv_arrowBack;

    PostProvider postProvider;
    NotificationProvider notificationProvider;
    TokenProvider tokenProvider;


    String extraPostId;


    TextView tv_title;
    TextView tv_description;
    TextView tv_username;

    TextView tv_datePost;
    TextView tv_likes;
    CircleImageView circleImageView;
    TextView tv_showProfile;
    UsersProvider usersProvider;
    LikesProvider likesProvider;

    AuthProvider authProvider;
    CommentsProvider commentsProvider;
    TextView tv_comment;
    CommentsAdapter commentsAdapter;

    RecyclerView recyclerView;

    String idUser = "";
    ImageView iv_like;

    ListenerRegistration listenerRegistration;




    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        iv_postDetail = findViewById(R.id.iv_postDetail);
        iv_arrowBack = findViewById(R.id.imageView_arrow_back);
        tv_comment = findViewById(R.id.tv_comment);

        iv_like = findViewById(R.id.iv_like);

        extraPostId = getIntent().getStringExtra("postId");

        tv_title = findViewById(R.id.tv_title);
        tv_description = findViewById(R.id.tv_description);
        tv_username = findViewById(R.id.tv_username);

        tv_datePost = findViewById(R.id.tv_datePost);
        tv_likes = findViewById(R.id.tv_likes);

        iv_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Like like = new Like();
                like.setIdUser(authProvider.getUid());
                like.setIdPost(extraPostId);
                like.setDate(new Date().getTime());
                like(like);
            }
        });


        tv_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mostrarVentanaComentario();

            }
        });


        iv_arrowBack.setOnClickListener(view -> finish());


        tv_showProfile = findViewById(R.id.tv_mostrarPerfil);


        circleImageView = findViewById(R.id.civ_profile);


        tv_showProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!idUser.equals("")) {
                    Intent i = new Intent(PostDetailActivity.this, ViewProfileActivity.class);
                    i.putExtra("idUser", idUser);

                    startActivity(i);
                } else {

                    Toast.makeText(PostDetailActivity.this, "el id del usuario aun no se ha cargado", Toast.LENGTH_LONG).show();
                }


            }
        });

        postProvider = new PostProvider();
        usersProvider = new UsersProvider();
        authProvider = new AuthProvider();
        commentsProvider = new CommentsProvider();
        likesProvider = new LikesProvider();
        notificationProvider = new NotificationProvider();
        tokenProvider = new TokenProvider();
        recyclerView = findViewById(R.id.rv_comments);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PostDetailActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setNestedScrollingEnabled(false);

        DividerItemDecoration horizontalDecoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        Drawable horizontalDivider = ContextCompat.getDrawable(PostDetailActivity.this, R.drawable.horizontal_divider);

        horizontalDecoration.setDrawable(horizontalDivider);
        recyclerView.addItemDecoration(horizontalDecoration);


        getPost();
        getLikes();
        //getUserInfo(post.getIdUser());
        obtenerNumeroLikesPost(extraPostId);
        comprobarSiExisteLike(extraPostId, authProvider.getUid());

    }

    @Override
    protected void onStart() {
        super.onStart();
        ViewedMessageHelper.updateOnline(true, PostDetailActivity.this);

        Query query = commentsProvider.getCommentsByPost(extraPostId);
        FirestoreRecyclerOptions<Comentario> options = new FirestoreRecyclerOptions.Builder<Comentario>().setQuery(query, Comentario.class).build();
        commentsAdapter = new CommentsAdapter(options, PostDetailActivity.this);
        recyclerView.setAdapter(commentsAdapter);
        commentsAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        commentsAdapter.stopListening();

    }


    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false, PostDetailActivity.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }


    private void obtenerNumeroLikesPost(String idPost) {
        listenerRegistration = likesProvider.getLikesByPost(idPost).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                if (queryDocumentSnapshots != null) {
                    int numeroLikes = queryDocumentSnapshots.size();
                    tv_likes.setText(String.valueOf(numeroLikes));
                }

            }
        });
    }
    private void comprobarSiExisteLike(String idPost, String idUser) {
        likesProvider.getLikeByPostAndUser(idPost, idUser).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numeroDocumentos = queryDocumentSnapshots.size();
                if (numeroDocumentos > 0) {
                    iv_like.setImageResource(R.drawable.ic_like_black);

                } else {
                    iv_like.setImageResource(R.drawable.ic_like);

                }
            }
        });


    }

    private void like(Like like) {
        likesProvider.getLikeByPostAndUser(like.getIdPost(), authProvider.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numeroDocumentos = queryDocumentSnapshots.size();
                if (numeroDocumentos > 0) {
                    String idLike = queryDocumentSnapshots.getDocuments().get(0).getId();
                    iv_like.setImageResource(R.drawable.ic_like);
                    likesProvider.delete(idLike);
                } else {
                    iv_like.setImageResource(R.drawable.ic_like_black);

                    likesProvider.create(like);
                }
            }
        });


    }

    private void mostrarVentanaComentario() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PostDetailActivity.this);
        alertDialogBuilder.setTitle("Comentario");
        alertDialogBuilder.setMessage("Ingresa tu comentario");
        EditText editText = new EditText(PostDetailActivity.this);

        editText.setHint("Ingresa aquí tu comentario");
        alertDialogBuilder.setView(editText);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        layoutParams.setMargins(30, 0, 30, 30);
        editText.setLayoutParams(layoutParams);

        RelativeLayout relativeLayout = new RelativeLayout(PostDetailActivity.this);
        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        relativeLayout.setLayoutParams(layoutParams1);
        relativeLayout.addView(editText);
        alertDialogBuilder.setView(relativeLayout);


        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String comentarioIntroducido = editText.getText().toString();
                if (!comentarioIntroducido.isEmpty()) {
                    createComment(comentarioIntroducido);

                } else {
                    Toast.makeText(PostDetailActivity.this, "Debe ingresar un comentario", Toast.LENGTH_LONG).show();

                }
            }
        });
        alertDialogBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        alertDialogBuilder.show();
    }

    private void createComment(String value) {
        Comentario comentario = new Comentario();
        comentario.setComment(value);
        comentario.setIdPost(extraPostId);
        comentario.setIdUser(authProvider.getUid());
        comentario.setTimestamp(new Date().getTime());
        commentsProvider.create(comentario).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                    sendNotification(value);
                    Toast.makeText(PostDetailActivity.this, "Comentario introducido", Toast.LENGTH_LONG).show();


                } else {
                    Toast.makeText(PostDetailActivity.this, "Fallo al insertar comentario", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void sendNotification(String comment) {

        if (idUser == null) {

            return;
        }
        tokenProvider.getToken(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("token")) {

                        String token = documentSnapshot.getString("token");
                        Map<String, String> data = new HashMap<>();
                        data.put("title", "Nuevo comentario");
                        data.put("body", comment);
                        FCMBody body = new FCMBody(token, "high", "4500s", data);
                        notificationProvider.sendNotification(body).enqueue(new Callback<FCMResponse>() {
                            @Override
                            public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                                if (response.body() != null) {
                                    if (response.body().getSuccess() == 1) {

                                        Toast.makeText(PostDetailActivity.this, "Notificación enviada", Toast.LENGTH_LONG).show();
                                    } else {

                                        Toast.makeText(PostDetailActivity.this, "Error al enviar notificación", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(PostDetailActivity.this, "Error al enviar notificación", Toast.LENGTH_LONG).show();

                                }

                            }

                            @Override
                            public void onFailure(Call<FCMResponse> call, Throwable t) {

                            }
                        });
                    }
                } else {
                    Toast.makeText(PostDetailActivity.this, "El token no existe", Toast.LENGTH_LONG).show();

                }
            }
        });


    }

    private void getPost() {

        postProvider.getPostById(extraPostId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("imagePost")) {
                        String imagePost = documentSnapshot.getString("imagePost");

                        Picasso.with(PostDetailActivity.this).load(imagePost).into(iv_postDetail);
                    }

                    if (documentSnapshot.contains("title")) {
                        String title = documentSnapshot.getString("title");
                        tv_title.setText(title);

                    }
                    if (documentSnapshot.contains("description")) {
                        String description = documentSnapshot.getString("description");
                        tv_description.setText(description);

                    }

                    if (documentSnapshot.contains("idUser")) {
                        idUser = documentSnapshot.getString("idUser");
                        getUserInfo(idUser);

                    }
                    if (documentSnapshot.contains("timestamp")) {
                        long timestamp = documentSnapshot.getLong("timestamp");
                        String relativeTime = RelativeTime.getTimeAgo(timestamp, PostDetailActivity.this);
                        tv_datePost.setText(relativeTime);
                    }


                }

            }
        });


    }


    public void getLikes() {
        listenerRegistration = likesProvider.getLikesByPost(extraPostId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                if (queryDocumentSnapshots != null) {
                    int numberOfLikes = queryDocumentSnapshots.size();
                    if (numberOfLikes == 1) {
                        tv_likes.setText(numberOfLikes + "Me gusta");

                    } else {
                        tv_likes.setText(numberOfLikes + "Me gustas");
                    }
                }

            }
        });
    }


    private void getUserInfo(String idUser) {
        usersProvider.getUserTask(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("username")) {
                        String username = documentSnapshot.getString("username");
                        tv_username.setText(username);


                    }


                    if (documentSnapshot.contains("imageProfile")) {
                        String imageProfile = documentSnapshot.getString("imageProfile");
                        Picasso.with(PostDetailActivity.this).load(imageProfile);


                    }


                }
            }
        });
    }
}
