package com.example.proyectoappredsocial.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.proyectoappredsocial.R;
import com.example.proyectoappredsocial.activities.AddBookManuallyActivity;
import com.example.proyectoappredsocial.activities.CollectionToCsvActivity;
import com.example.proyectoappredsocial.activities.LoginActivity;
import com.example.proyectoappredsocial.activities.RecognizeTextActivity;
import com.example.proyectoappredsocial.activities.SearchBooksActivity;
import com.example.proyectoappredsocial.activities.StatisticsActivity;
import com.example.proyectoappredsocial.activities.WritePostActivity;
import com.example.proyectoappredsocial.adapters.MyBooksAdapter;
import com.example.proyectoappredsocial.models.Book;
import com.example.proyectoappredsocial.providers.AuthProvider;
import com.example.proyectoappredsocial.providers.BooksProvider;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.mancj.materialsearchbar.MaterialSearchBar;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyBooksFragment extends Fragment implements MaterialSearchBar.OnSearchActionListener {

    View view;

    Button scanBtn, searchButton;
    TextView tv_numberBooks;
    AlertDialog.Builder ad_builderSelector;

    FloatingActionButton fab_button;

    CharSequence options[];

    BooksProvider booksProvider;
    RecyclerView recyclerView;
    MyBooksAdapter myBooksAdapter;
    MyBooksAdapter myBooksAdapterSearch;
    AuthProvider authProvider;

    MaterialSearchBar materialSearchBar;
    ListenerRegistration listenerRegistration;

    public MyBooksFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_my_books, container, false);
        searchButton = view.findViewById(R.id.search_button);
        fab_button = view.findViewById(R.id.fab_button);
        tv_numberBooks = view.findViewById(R.id.tv_numberBooks);

        booksProvider = new BooksProvider();
        authProvider = new AuthProvider();

        materialSearchBar = view.findViewById(R.id.searchBar);

        materialSearchBar.setOnSearchActionListener(this);
        materialSearchBar.inflateMenu(R.menu.menu_main);

        materialSearchBar.getMenu().setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

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
                    Intent intent = new Intent(getActivity(), CollectionToCsvActivity.class);
                    startActivity(intent);
                }


                return true;

            }
        });


        recyclerView = view.findViewById(R.id.rv_myBooks);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setNestedScrollingEnabled(false);


        DividerItemDecoration horizontalDecoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        Drawable horizontalDivider = ContextCompat.getDrawable(getActivity(), R.drawable.horizontal_divider);

        horizontalDecoration.setDrawable(horizontalDivider);
        recyclerView.addItemDecoration(horizontalDecoration);


        fab_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ad_builderSelector = new AlertDialog.Builder(getContext());
                ad_builderSelector.setTitle("Selecciona una opción");
                options = new CharSequence[]{"Buscar libro", "Escanear Libro", "Añadir libro manualmente"};
                ad_builderSelector.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {

                            Intent intent = new Intent(getActivity(), SearchBooksActivity.class);
                            startActivity(intent);

                        } else if (i == 1) {


                            IntentIntegrator integrator = new IntentIntegrator(getActivity());
                            integrator.initiateScan();

                        } else if (i == 2) {

                            Intent intent = new Intent(getActivity(), AddBookManuallyActivity.class);
                            startActivity(intent);

                        }


                    }


                });

                ad_builderSelector.show();

            }
        });

        Log.i("idUser", authProvider.getUid());
        comprobarSiExistenLibros();


        return view;


    }


    private void logout() {
        authProvider.logout();
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }


    private void comprobarSiExistenLibros() {


        listenerRegistration = booksProvider.getBooksByUserQueryOrderByTitle(authProvider.getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {

                if (queryDocumentSnapshots != null) {
                    int numberBooks = queryDocumentSnapshots.size();
                    if (numberBooks == 0) {
                        tv_numberBooks.setText("No hay libros");
                        tv_numberBooks.setTextColor(Color.GRAY);
                    } else {

                        tv_numberBooks.setVisibility(View.GONE);
                    }

                }
            }
        });
    }

    private void getAllBooks() {
        Query query = booksProvider.getBooksByUserQueryOrderByTitle(authProvider.getUid());
        FirestoreRecyclerOptions<Book> options = new FirestoreRecyclerOptions.Builder<Book>().setQuery(query, Book.class).build();
        myBooksAdapter = new MyBooksAdapter(options, getContext());
        myBooksAdapter.notifyDataSetChanged();

        recyclerView.setAdapter(myBooksAdapter);
        myBooksAdapter.startListening();

    }


    @Override
    public void onStart() {
        super.onStart();

        getAllBooks();

    }

    @Override
    public void onStop() {
        super.onStop();
        myBooksAdapter.stopListening();
        if (myBooksAdapterSearch != null) {
            myBooksAdapterSearch.stopListening();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }

    private void searchBookByTitle(String bookTitle) {

        Query query = booksProvider.getBookByTitle(bookTitle);
        FirestoreRecyclerOptions<Book> options = new FirestoreRecyclerOptions.Builder<Book>().setQuery(query, Book.class).build();
        myBooksAdapterSearch = new MyBooksAdapter(options, getContext());


        myBooksAdapterSearch.notifyDataSetChanged();
        recyclerView.setAdapter(myBooksAdapterSearch);
        myBooksAdapterSearch.startListening();


    }


    @Override
    public void onSearchStateChanged(boolean enabled) {
        if (!enabled) {
            getAllBooks();
        }
    }

    @Override
    public void onSearchConfirmed(CharSequence text) {


        searchBookByTitle(text.toString());


    }

    @Override
    public void onButtonClicked(int buttonCode) {

    }
}