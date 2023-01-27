package com.example.proyectoappredsocial.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.proyectoappredsocial.R;
import com.example.proyectoappredsocial.activities.EditProfileActivity;
import com.example.proyectoappredsocial.adapters.MyPostAdapter;
import com.example.proyectoappredsocial.models.Post;
import com.example.proyectoappredsocial.providers.AuthProvider;
import com.example.proyectoappredsocial.providers.BookReviewsProvider;
import com.example.proyectoappredsocial.providers.BooksProvider;
import com.example.proyectoappredsocial.providers.CommentsProvider;
import com.example.proyectoappredsocial.providers.ImageProvider;
import com.example.proyectoappredsocial.providers.LikesProvider;
import com.example.proyectoappredsocial.providers.PostProvider;
import com.example.proyectoappredsocial.providers.ReadsProvider;
import com.example.proyectoappredsocial.providers.UsersProvider;
import com.example.proyectoappredsocial.utils.ViewedMessageHelper;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    UsersProvider usersProvider;
    AuthProvider authProvider;
    PostProvider postProvider;
    BooksProvider booksProvider;
    BookReviewsProvider bookReviewsProvider;
    CommentsProvider commentsProvider;
    ReadsProvider readsProvider;
    LikesProvider likesProvider;
    ImageProvider imageProvider;



    TextView tv_numberOfPost;

    TextView tv_totalBooks;
    TextView tv_userName;
    TextView tv_editProfile;

    CircleImageView civ_profile;
    RecyclerView recyclerView;
    MyPostAdapter myPostAdapter;

    Button btn_deleteAccount;
    TextView tv_numeroPublicacionesActuales;

    ListenerRegistration listenerRegistration;

    public ProfileFragment() {
        // Required empty public constructor
    }

    View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);


        tv_editProfile = view.findViewById(R.id.tv_editProfile);

        tv_editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToEditProfile();

            }
        });

        usersProvider = new UsersProvider();
        authProvider = new AuthProvider();
        postProvider = new PostProvider();
        booksProvider = new BooksProvider();
        bookReviewsProvider = new BookReviewsProvider();
        commentsProvider = new CommentsProvider();
        readsProvider = new ReadsProvider();
        likesProvider = new LikesProvider();
        imageProvider = new ImageProvider();

        tv_numberOfPost = view.findViewById(R.id.tv_numberOfPost);

        tv_userName = view.findViewById(R.id.tv_userName);
        tv_totalBooks = view.findViewById(R.id.tv_totalBooks);
        civ_profile = view.findViewById(R.id.iv_profile);
        tv_numeroPublicacionesActuales = view.findViewById(R.id.tv_numeroPublicacionesActuales);
        recyclerView = view.findViewById(R.id.rv_myPost);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);


        getUser();
        getTotalBooks();
        getPostNumber();

        comprobarSiExistePost();


        return view;
    }


    private void comprobarSiExistePost() {
        listenerRegistration = postProvider.getPostByUserQuery(authProvider.getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                if (queryDocumentSnapshots != null) {
                    int numberPost = queryDocumentSnapshots.size();
                    if (numberPost > 0) {
                        tv_numeroPublicacionesActuales.setText("Mis publicaciones");

                    } else {

                        tv_numeroPublicacionesActuales.setText("No hay publicaciones");
                        tv_numeroPublicacionesActuales.setTextColor(Color.GRAY);
                        tv_numeroPublicacionesActuales.setTextSize(18);
                        tv_numeroPublicacionesActuales.setAllCaps(false);
                        tv_numeroPublicacionesActuales.setTypeface(tv_numeroPublicacionesActuales.getTypeface(), Typeface.BOLD_ITALIC);

                    }
                }

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        ViewedMessageHelper.updateOnline(true, getContext());

        Query query = postProvider.getPostByUserQuery(authProvider.getUid());
        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>().setQuery(query, Post.class).build();
        myPostAdapter = new MyPostAdapter(options, getContext());
        recyclerView.setAdapter(myPostAdapter);
        myPostAdapter.startListening();

    }

    @Override
    public void onStop() {
        super.onStop();
        myPostAdapter.stopListening();

    }


    private void goToEditProfile() {

        Intent i = new Intent(getActivity(), EditProfileActivity.class);
        startActivity(i);
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

    private void getPostNumber() {
        postProvider.getPostByUserQuery(authProvider.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numberOfPost = queryDocumentSnapshots.size();
                tv_numberOfPost.setText(numberOfPost + " publicaciones");

            }
        });

    }

    private void getUser() {
        usersProvider.getUserTask(authProvider.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {


                    if (documentSnapshot.contains("username")) {
                        String username = documentSnapshot.getString("username");
                        tv_userName.setText(username);

                    }
                    if (documentSnapshot.contains("imageProfile")) {
                        String imageProfile = documentSnapshot.getString("imageProfile");

                        if (imageProfile != null) {
                            if (!imageProfile.isEmpty()) {
                                Picasso.with(getContext()).load(imageProfile).into(civ_profile);
                            }

                        }


                    }

                }


            }
        });

    }


}

