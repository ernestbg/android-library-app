package com.example.proyectoappredsocial.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyectoappredsocial.R;
import com.example.proyectoappredsocial.fragments.DatePickerFragment;
import com.example.proyectoappredsocial.models.Challenge;
import com.example.proyectoappredsocial.providers.AuthProvider;
import com.example.proyectoappredsocial.providers.BooksProvider;
import com.example.proyectoappredsocial.providers.ChallengeProvider;
import com.example.proyectoappredsocial.utils.ViewedMessageHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Date;

import dmax.dialog.SpotsDialog;

public class RateBookPopUpActivity extends AppCompatActivity {


    AuthProvider authProvider;
    BooksProvider booksProvider;


    AlertDialog alertDialog;
    Button btn_rate;
    RatingBar ratingBar;
    Float rating;
    String extraBookId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_book_pop_up);

        authProvider = new AuthProvider();
        booksProvider = new BooksProvider();
        btn_rate=findViewById(R.id.btn_rate);
        ratingBar=findViewById(R.id.ratingBar);
        extraBookId = getIntent().getStringExtra("idBook");





        alertDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere un momento...")
                .setCancelable(false)
                .build();


        btn_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //if (rating != null) {

                rating = ratingBar.getRating();

                Log.i("erny", extraBookId);


                booksProvider.updateBookRating(rating, extraBookId);
                Toast.makeText(RateBookPopUpActivity.this, "Has calificado el libro", Toast.LENGTH_LONG).show();

                finish();


               /* } else {
                    Toast.makeText(WriteBookReviewActivity.this, "Debe rellenar los campos", Toast.LENGTH_LONG).show();


                }*/

            }
        });


        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        getWindow().setLayout((int) (width * .8), (int) (height * .5));
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;

        getWindow().setAttributes(params);

    }

    @Override
    protected void onStart() {
        super.onStart();
        ViewedMessageHelper.updateOnline(true,RateBookPopUpActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false,RateBookPopUpActivity.this);
    }


}