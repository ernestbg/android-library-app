package com.example.proyectoappredsocial.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.proyectoappredsocial.R;
import com.example.proyectoappredsocial.activities.FiltersActivity;
import com.example.proyectoappredsocial.activities.LoginActivity;
import com.example.proyectoappredsocial.providers.AuthProvider;
import com.example.proyectoappredsocial.providers.PostProvider;
import com.google.firebase.firestore.Query;

/**
 * A simple {@link Fragment} subclass.
 */
public class FiltersFragment extends Fragment {

    View view;
    Toolbar toolbar;
    AuthProvider authProvider;
    PostProvider postProvider;
    CardView cv_paper;
    CardView cv_ebook;


    public FiltersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_filters, container, false);
        toolbar = view.findViewById(R.id.toolbar);
        cv_paper = view.findViewById(R.id.cv_paper);
        cv_ebook = view.findViewById(R.id.cv_ebook);

        cv_paper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFilterActivity("paper");


            }
        });

        cv_ebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFilterActivity("ebook");


            }
        });


        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Publicar un articulo");


        setHasOptionsMenu(true);
        authProvider = new AuthProvider();
        postProvider = new PostProvider();


        return view;
    }

    private void goToFilterActivity(String category) {
        Intent i = new Intent(getContext(), FiltersActivity.class);
        i.putExtra("category", category);
        startActivity(i);

    }

    @Override
    public void onStart() {
        super.onStart();

        Query query = postProvider.getAll();


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


        return true;
    }

    private void logout() {
        authProvider.logout();
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);


    }
}