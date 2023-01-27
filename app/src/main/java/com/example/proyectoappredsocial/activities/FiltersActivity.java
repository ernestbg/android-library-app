package com.example.proyectoappredsocial.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;


import com.example.proyectoappredsocial.R;
import com.example.proyectoappredsocial.adapters.PostAdapter;
import com.example.proyectoappredsocial.models.Post;
import com.example.proyectoappredsocial.providers.AuthProvider;
import com.example.proyectoappredsocial.providers.PostProvider;
import com.example.proyectoappredsocial.utils.ViewedMessageHelper;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class FiltersActivity extends AppCompatActivity {

    String putExtraCategory;

    AuthProvider authProvider;
    RecyclerView recyclerView;
    PostProvider postProvider;
    PostAdapter postAdapter;
    Toolbar toolbar;
    TextView tv_numeroPublicaciones;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);

        recyclerView = findViewById(R.id.rv_filters);
        recyclerView.setLayoutManager(new GridLayoutManager(FiltersActivity.this, 2));


        authProvider = new AuthProvider();
        postProvider = new PostProvider();


        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Filtrar publicaciones");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        putExtraCategory = getIntent().getStringExtra("category");
        Toast.makeText(this, "la categoria que selecciono es " + putExtraCategory, Toast.LENGTH_SHORT).show();

        tv_numeroPublicaciones = findViewById(R.id.tv_numeroPublicaciones);
    }

    @Override
    public void onStart() {
        super.onStart();

        ViewedMessageHelper.updateOnline(true, FiltersActivity.this);


        Query query = postProvider.getPostByCategoryDate(putExtraCategory);
        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>().setQuery(query, Post.class).build();
        postAdapter = new PostAdapter(options, FiltersActivity.this, tv_numeroPublicaciones);
        recyclerView.setAdapter(postAdapter);
        postAdapter.startListening();


    }

    @Override
    public void onStop() {
        super.onStop();

        postAdapter.stopListening();

    }


    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false, FiltersActivity.this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();

        }
        return true;

    }
}