package com.example.proyectoappredsocial.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyectoappredsocial.R;
import com.example.proyectoappredsocial.models.BookReview;
import com.example.proyectoappredsocial.providers.AuthProvider;
import com.example.proyectoappredsocial.providers.BookReviewsProvider;
import com.example.proyectoappredsocial.providers.UsersProvider;
import com.example.proyectoappredsocial.utils.ViewedMessageHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Date;

import dmax.dialog.SpotsDialog;

public class WriteBookReviewActivity extends AppCompatActivity {

    UsersProvider usersProvider;
    AuthProvider authProvider;
    BookReviewsProvider bookReviewsProvider;

    String extraBookIsbn;

    RatingBar ratingBar;
    TextInputEditText ti_reviewText;
    String reviewText;
    Float rating;


    ImageView iv_saveReview;
    ImageView iv_cancel;

    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_book_review);


        ratingBar = findViewById(R.id.ratingBar);
        iv_saveReview = findViewById(R.id.iv_saveReview);
        iv_cancel = findViewById(R.id.iv_cancel);
        ti_reviewText = findViewById(R.id.ti_reviewText);

        usersProvider = new UsersProvider();
        authProvider = new AuthProvider();
        bookReviewsProvider = new BookReviewsProvider();

        extraBookIsbn = getIntent().getStringExtra("extraBookIsbn");




        iv_cancel.setOnClickListener(view -> finish());

        iv_saveReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //if (rating != null) {

                    rating = ratingBar.getRating();
                    reviewText = ti_reviewText.getText().toString();


                    saveBookReview(reviewText, rating);


               /* } else {
                    Toast.makeText(WriteBookReviewActivity.this, "Debe rellenar los campos", Toast.LENGTH_LONG).show();


                }*/

            }
        });

        alertDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere un momento...")
                .setCancelable(false)
                .build();


    }

    @Override
    protected void onStart() {
        super.onStart();
        ViewedMessageHelper.updateOnline(true,WriteBookReviewActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false,WriteBookReviewActivity.this);
    }


    private void saveBookReview(String reviewText, float rating) {
        alertDialog.show();


        BookReview bookReview = new BookReview();
        bookReview.setTextReview(reviewText);
        bookReview.setIdUser(authProvider.getUid());
        bookReview.setBookIsbn(extraBookIsbn);
        bookReview.setRating(rating);
        bookReview.setDateReview(new Date().getTime());

        bookReviewsProvider.save(bookReview).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                alertDialog.dismiss();
                if (task.isSuccessful()) {
                    Toast.makeText(WriteBookReviewActivity.this, "Review guardada", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(WriteBookReviewActivity.this, "Error al guardar review", Toast.LENGTH_LONG).show();
                }
            }
        });


    }

}