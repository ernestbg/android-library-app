package com.example.proyectoappredsocial.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.proyectoappredsocial.R;
import com.example.proyectoappredsocial.activities.CollectionToCsvActivity;
import com.example.proyectoappredsocial.activities.CollectionToCsvPopUpActivity;
import com.example.proyectoappredsocial.activities.LoginActivity;
import com.example.proyectoappredsocial.activities.RecognizeTextActivity;
import com.example.proyectoappredsocial.activities.StatisticsActivity;
import com.example.proyectoappredsocial.activities.WritePostActivity;
import com.example.proyectoappredsocial.adapters.MyPostAdapter;
import com.example.proyectoappredsocial.adapters.PostAdapter;
import com.example.proyectoappredsocial.models.Post;
import com.example.proyectoappredsocial.providers.AuthProvider;
import com.example.proyectoappredsocial.providers.CommentsProvider;
import com.example.proyectoappredsocial.providers.LikesProvider;
import com.example.proyectoappredsocial.providers.PostProvider;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    View view;
    Toolbar toolbar;
    AuthProvider authProvider;
    RecyclerView recyclerView;
    PostProvider postProvider;
    PostAdapter postAdapter;
    PostAdapter postAdapterSearch;
    LikesProvider likesProvider;
    CommentsProvider commentsProvider;

    ImageView iv_cardPost;
    TextView tv_titleCardPost;
    ListenerRegistration listenerRegistration;
    TextView tv_likes;
    TextView tv_comments;


    MyPostAdapter myPostAdapter;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        view = inflater.inflate(R.layout.fragment_home, container, false);

        toolbar = view.findViewById(R.id.toolbar);
        recyclerView = view.findViewById(R.id.rv_home);
        iv_cardPost = view.findViewById(R.id.iv_cardPost);
        tv_titleCardPost = view.findViewById(R.id.tv_titleCardPost);
        tv_likes = view.findViewById(R.id.tv_likes);
        tv_comments = view.findViewById(R.id.tv_comments);


        LinearLayoutManager linearLayoutManager
                = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);


        authProvider = new AuthProvider();
        postProvider = new PostProvider();
        likesProvider = new LikesProvider();
        commentsProvider = new CommentsProvider();

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);


        setHasOptionsMenu(true);


        comprobarSiExistePost();

        likesProvider.getAll().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                if (queryDocumentSnapshots != null) {


                    for (int i = 0; i < queryDocumentSnapshots.getDocuments().size(); i++) {

                        List<String> list = new ArrayList<>();

                        String idPost = queryDocumentSnapshots.getDocuments().get(i).getString("idPost");

                        getNumberOfLikesByPost(idPost);
                        getNumberOfCommentsByPost(idPost);
                        list.add(idPost);

                        String idPostMax = Collections.max(list);

                        postProvider.getPostById(idPostMax).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                if (documentSnapshot.exists()) {
                                    if (documentSnapshot.contains("imagePost")) {
                                        String imagePost = documentSnapshot.getString("imagePost");

                                        Picasso.with(getContext()).load(imagePost).into(iv_cardPost);
                                    }

                                    if (documentSnapshot.contains("title")) {
                                        String title = documentSnapshot.getString("title");
                                        tv_titleCardPost.setText(title);

                                    }


                                }


                            }
                        });


                    }


                }

            }
        });


        return view;
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.itemLogout) {
            logout();
        }

        if (item.getItemId() == R.id.itemWritePost) {
            Intent intent = new Intent(getActivity(), WritePostActivity.class);
            startActivity(intent);
        }

        if (item.getItemId() == R.id.itemStatistics) {
            Intent intent = new Intent(getActivity(), StatisticsActivity.class);
            startActivity(intent);
        }

        if (item.getItemId() == R.id.itemTextRecognition) {
            Intent intent = new Intent(getActivity(), RecognizeTextActivity.class);
            startActivity(intent);
        }

        if (item.getItemId() == R.id.itemConvertToCsv) {
            Intent intent = new Intent(getActivity(), CollectionToCsvPopUpActivity.class);
            startActivity(intent);
        }


        return true;
    }


    private void comprobarSiExistePost() {
        postProvider.getAll().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                if (queryDocumentSnapshots != null) {
                    int numberPost = queryDocumentSnapshots.size();
                    if (numberPost > 0) {
                        getAllPost();
                    } else {


                    }
                }

            }
        });
    }


    private void getAllPost() {

        Query query = postProvider.getAll();
        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>().setQuery(query, Post.class).build();

        postAdapter = new PostAdapter(options, getContext());
        postAdapter.notifyDataSetChanged();

        recyclerView.setAdapter(postAdapter);
        postAdapter.startListening();
    }

    private void getNumberOfLikesByPost(String idPost) {
        listenerRegistration = likesProvider.getLikesByPost(idPost).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                if (queryDocumentSnapshots != null) {
                    int numberOfLikes = queryDocumentSnapshots.size();
                    tv_likes.setText(numberOfLikes + " me gusta");
                }

            }
        });
    }

    private void getNumberOfCommentsByPost(String idPost) {
        listenerRegistration = commentsProvider.getCommentsByPost(idPost).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                if (queryDocumentSnapshots != null) {
                    int numberOfComments = queryDocumentSnapshots.size();

                    if (numberOfComments > 1) {
                        tv_comments.setText(numberOfComments + " comentarios");
                    } else {
                        tv_comments.setText(numberOfComments + " comentario");
                    }

                }

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        getAllPost();

    }


    @Override
    public void onStop() {
        super.onStop();

        postAdapter.stopListening();
        if (postAdapterSearch != null) {
            postAdapterSearch.stopListening();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (postAdapter.getListenerRegistration() != null) {
            postAdapter.getListenerRegistration().remove();
        }
    }


    private void logout() {
        ViewedMessageHelper.updateOnline(false, getActivity());


        authProvider.logout();


        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        startActivity(intent);


    }


}