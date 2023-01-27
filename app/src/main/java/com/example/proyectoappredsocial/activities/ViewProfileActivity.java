package com.example.proyectoappredsocial.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.proyectoappredsocial.R;
import com.example.proyectoappredsocial.adapters.MyPostAdapter;
import com.example.proyectoappredsocial.models.Post;
import com.example.proyectoappredsocial.providers.AuthProvider;
import com.example.proyectoappredsocial.providers.BookReviewsProvider;
import com.example.proyectoappredsocial.providers.BooksProvider;
import com.example.proyectoappredsocial.providers.CommentsProvider;
import com.example.proyectoappredsocial.providers.FollowersProvider;
import com.example.proyectoappredsocial.providers.FollowingsProvider;
import com.example.proyectoappredsocial.providers.ImageProvider;
import com.example.proyectoappredsocial.providers.LikesProvider;
import com.example.proyectoappredsocial.providers.PostProvider;
import com.example.proyectoappredsocial.providers.ReadsProvider;
import com.example.proyectoappredsocial.providers.UsersProvider;
import com.example.proyectoappredsocial.utils.ViewedMessageHelper;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewProfileActivity extends AppCompatActivity {

    LinearLayout linearLayoutEditProfile;
    View view;
    UsersProvider usersProvider;
    AuthProvider authProvider;
    PostProvider postProvider;
    FollowersProvider followersProvider;
    FollowingsProvider followingsProvider;

    BooksProvider booksProvider;


    TextView tv_numberOfPost;
    TextView tv_numeroPublicacionesActuales;
    FloatingActionButton fab_button_chat;



    TextView tv_userName;
    ImageView iv_profile;

    MyPostAdapter myPostAdapter;
    RecyclerView recyclerView;
    ImageView iv_arrowBack;
    String extraIdUser;
    Toolbar toolbar;
    TextView tv_totalBooks;


    Button btn_follow;

    ListenerRegistration listenerRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        usersProvider = new UsersProvider();
        authProvider = new AuthProvider();
        postProvider = new PostProvider();
        followersProvider = new FollowersProvider();
        followingsProvider = new FollowingsProvider();
        usersProvider = new UsersProvider();
        authProvider = new AuthProvider();
        postProvider = new PostProvider();
        booksProvider = new BooksProvider();



        tv_numberOfPost = findViewById(R.id.tv_numberOfPost);

        tv_userName = findViewById(R.id.tv_userName);
        tv_totalBooks=findViewById(R.id.tv_totalBooks);
        fab_button_chat = findViewById(R.id.fab_button_chat);


        iv_profile = findViewById(R.id.iv_profile);


        extraIdUser = getIntent().getStringExtra("idUser");

        tv_numeroPublicacionesActuales = findViewById(R.id.tv_numeroPublicacionesActuales);
        //btn_follow = findViewById(R.id.btn_follow);

        recyclerView = findViewById(R.id.rv_myPost);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ViewProfileActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);


        iv_arrowBack = findViewById(R.id.iv_arrowBack);
        iv_arrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

       fab_button_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToChatActivity();
            }
        });

        if (authProvider.getUid().equals(extraIdUser)) {
            fab_button_chat.setVisibility(View.GONE);
        }


        /*followersProvider.getFollowersByUser(authProvider.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String idUserFollowing = document.getString("idUserFollowing");


                        if (idUserFollowing.equals(putExtraIdUser)) {


                            Log.i("erny", authProvider.getUid());

                            btn_follow.setText("Siguiendo");
                            btn_follow.setBackgroundColor(Color.GREEN);


                        } else {


                            btn_follow.setText("Seguir");
                            btn_follow.setBackgroundColor(Color.GRAY);


                        }
                    }


                }
            }
        });


        btn_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_follow.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onClick(View view) {

                        if (btn_follow.getText().equals("Seguir")) {


                            Follower follower = new Follower();
                            follower.setIdUser(authProvider.getUid());
                            follower.setIdUserFollowing(putExtraIdUser);
                            follower.setTimestamp(new Date().getTime());


                            followersProvider.create(follower);


                            btn_follow.setText("Siguiendo");


                        } else {

                            followingsProvider.delete(authProvider.getUid());

                            btn_follow.setText("Seguir");
                        }


                    }
                });

            }
        });*/

        getUser();
        getTotalBooks();
        getPostNumber();

        comprobarSiExistePost();

    }

    private void goToChatActivity() {

        Intent intent = new Intent(ViewProfileActivity.this, ChatActivity.class);
        intent.putExtra("idUser1", authProvider.getUid());
        intent.putExtra("idUser2", extraIdUser);
        startActivity(intent);
    }

    private void comprobarSiExistePost() {
        listenerRegistration = postProvider.getPostByUserQuery(extraIdUser).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
                if (querySnapshot != null) {
                    int numberPost = querySnapshot.size();
                    if (numberPost > 0) {
                        tv_numeroPublicacionesActuales.setText("Publicaciones");

                    } else {

                        tv_numeroPublicacionesActuales.setText("No hay publicaciones");
                        tv_numeroPublicacionesActuales.setTextColor(Color.GRAY);
                    }
                }

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        ViewedMessageHelper.updateOnline(true, ViewProfileActivity.this);

        Query query = postProvider.getPostByUserQuery(extraIdUser);
        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>().setQuery(query, Post.class).build();
        myPostAdapter = new MyPostAdapter(options, ViewProfileActivity.this);
        recyclerView.setAdapter(myPostAdapter);
        myPostAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        myPostAdapter.stopListening();

    }


    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false, ViewProfileActivity.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }

    private void getPostNumber() {
        postProvider.getPostByUserQuery(extraIdUser).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numberOfPost = queryDocumentSnapshots.size();
                tv_numberOfPost.setText(numberOfPost+ "publicaciones");

            }
        });

    }

    private void getUser() {
        usersProvider.getUserTask(extraIdUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {


                    if (documentSnapshot.contains("username")) {
                        String username = documentSnapshot.getString("username");
                        tv_userName.setText(username);

                    }


                }
                if (documentSnapshot.contains("imageProfile")) {
                    String imageProfile = documentSnapshot.getString("imageProfile");

                    if (imageProfile != null) {
                        if (!imageProfile.isEmpty()) {
                            Picasso.with(ViewProfileActivity.this).load(imageProfile).into(iv_profile);
                        }

                    }


                }
            }


        });


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();

        }
        return true;

    }


    private void getTotalBooks() {
        booksProvider.getBooksByUserQueryOrderByTitle(authProvider.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int totalBooks = queryDocumentSnapshots.size();
                tv_totalBooks.setText(totalBooks + " libros");
            }
        });
    }


}