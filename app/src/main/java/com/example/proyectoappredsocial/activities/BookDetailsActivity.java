package com.example.proyectoappredsocial.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectoappredsocial.R;
import com.example.proyectoappredsocial.adapters.BookReviewAdapter;
import com.example.proyectoappredsocial.models.Book;
import com.example.proyectoappredsocial.models.BookReview;
import com.example.proyectoappredsocial.providers.AuthProvider;
import com.example.proyectoappredsocial.providers.BookReviewsProvider;
import com.example.proyectoappredsocial.providers.BooksProvider;
import com.example.proyectoappredsocial.utils.ViewedMessageHelper;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import dmax.dialog.SpotsDialog;

public class BookDetailsActivity extends AppCompatActivity {

    BooksProvider booksProvider;
    AuthProvider authProvider;
    BookReviewsProvider bookReviewsProvider;
    BookReviewAdapter bookReviewAdapter;

    Book book;

    String title, subtitle, publisher, publishedDate, description, imageBook, isbn;
    int pageCount;
    ArrayList<String> authors = new ArrayList<>();

    TextView tv_title, tv_subtitle, tv_authors, tv_publisher, tv_description, tv_pageCount, tv_publishDate;
    Button btn_addBook, btn_writeReview;
    ImageView iv_book;


    RecyclerView recyclerView;

    AlertDialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);


        tv_title = findViewById(R.id.tv_title);
        tv_subtitle = findViewById(R.id.tv_subtitle);
        tv_publisher = findViewById(R.id.tv_publisher);
        tv_authors = findViewById(R.id.tv_authors);
        tv_description = findViewById(R.id.tv_description);
        tv_pageCount = findViewById(R.id.tv_pageCount);
        tv_publishDate = findViewById(R.id.tv_publishedDate);
        btn_addBook = findViewById(R.id.btn_addBook);
        iv_book = findViewById(R.id.iv_book);
        btn_writeReview = findViewById(R.id.btn_writeReview);

        recyclerView = findViewById(R.id.rv_bookReviews);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(BookDetailsActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setNestedScrollingEnabled(false);

        DividerItemDecoration horizontalDecoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        Drawable horizontalDivider = ContextCompat.getDrawable(BookDetailsActivity.this, R.drawable.horizontal_divider);

        horizontalDecoration.setDrawable(horizontalDivider);
        recyclerView.addItemDecoration(horizontalDecoration);

        booksProvider = new BooksProvider();
        bookReviewsProvider = new BookReviewsProvider();
        authProvider = new AuthProvider();


        // getting the data which we have passed from our adapter class.
        title = getIntent().getStringExtra("title");
        subtitle = getIntent().getStringExtra("subtitle");
        authors = getIntent().getStringArrayListExtra("authors");
        publisher = getIntent().getStringExtra("publisher");
        publishedDate = getIntent().getStringExtra("publishedDate");
        description = getIntent().getStringExtra("description");
        pageCount = getIntent().getIntExtra("pageCount", 0);
        imageBook = getIntent().getStringExtra("imageBook");
        isbn = getIntent().getStringExtra("bookIsbn");


        // after getting the data we are setting
        // that data to our text views and image view.
        tv_title.setText(title);
        tv_subtitle.setText(subtitle);
        if (authors.size() != 0) {
            for (int i = 0; i < authors.size(); i++) {
                tv_authors.setText(authors.get(i));
            }
        }
        tv_publisher.setText("Editorial: " + publisher);
        tv_publishDate.setText("Fecha de publicación : " + publishedDate);
        tv_description.setText(description);
        tv_pageCount.setText("Páginas : " + pageCount);
        Picasso.with(BookDetailsActivity.this).load(imageBook).into(iv_book);

        alertDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere un momento...")
                .setCancelable(false)
                .build();


        btn_addBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                book = new Book(authProvider.getUid(), title, subtitle, authors, publisher, publishedDate, description, pageCount, imageBook);


                booksProvider.save(book).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> taskSave) {
                        alertDialog.dismiss();
                        if (taskSave.isSuccessful()) {

                            Toast.makeText(BookDetailsActivity.this, "Libro guardado en tu biblioteca", Toast.LENGTH_SHORT).show();

                        } else {

                            Toast.makeText(BookDetailsActivity.this, "No se pudo almacenar el libro en tu biblioteca", Toast.LENGTH_SHORT).show();

                        }
                    }
                });


            }
        });


        btn_writeReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BookDetailsActivity.this, WriteBookReviewActivity.class);
                intent.putExtra("extraBookIsbn", isbn);
                startActivity(intent);

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        ViewedMessageHelper.updateOnline(true, BookDetailsActivity.this);


        Query query = bookReviewsProvider.getReviewsByBook(isbn);
        FirestoreRecyclerOptions<BookReview> options = new FirestoreRecyclerOptions.Builder<BookReview>().setQuery(query, BookReview.class).build();
        bookReviewAdapter = new BookReviewAdapter(options, BookDetailsActivity.this);
        recyclerView.setAdapter(bookReviewAdapter);
        bookReviewAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        bookReviewAdapter.stopListening();

    }


    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false, BookDetailsActivity.this);
    }
}