package com.example.proyectoappredsocial.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyectoappredsocial.R;
import com.example.proyectoappredsocial.fragments.DatePickerFragment;
import com.example.proyectoappredsocial.models.Challenge;
import com.example.proyectoappredsocial.providers.AuthProvider;
import com.example.proyectoappredsocial.providers.ChallengeProvider;
import com.example.proyectoappredsocial.utils.ViewedMessageHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Date;

import dmax.dialog.SpotsDialog;

public class CreateChallengePopUpActivity extends AppCompatActivity {


    ChallengeProvider challengeProvider;
    AuthProvider authProvider;

    TextInputEditText etPlannedDate;
    TextInputEditText etDeadline;

    int totalBooks = 0;

    Button btn_startChallenge;

    TextInputEditText ti_totalBooks;

    String startDate;
    String finishDate;

    AlertDialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up);

        challengeProvider=new ChallengeProvider();
        authProvider=new AuthProvider();


        ti_totalBooks = findViewById(R.id.ti_totalBooks);

        btn_startChallenge = findViewById(R.id.btn_startChallenge);

        etPlannedDate = findViewById(R.id.etPlannedDate);
        etDeadline = findViewById(R.id.etDeadline);


        etPlannedDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialogStart();


            }
        });

        etDeadline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialogFinish();
            }
        });

        alertDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere un momento...")
                .setCancelable(false)
                .build();

        btn_startChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.show();

                totalBooks = Integer.parseInt(ti_totalBooks.getText().toString());

                startDate = etPlannedDate.getText().toString();
                finishDate = etDeadline.getText().toString();
                if (totalBooks != 0 && !startDate.isEmpty() && !finishDate.isEmpty()) {

                    Challenge challenge = new Challenge();
                    challenge.setIdUser(authProvider.getUid());
                    challenge.setTotalBooks(String.valueOf(totalBooks));
                    challenge.setStartDate(startDate);
                    challenge.setFinishDate(finishDate);
                    challenge.setTimestamp(new Date().getTime());

                    challengeProvider.create(challenge).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> taskSave) {
                            alertDialog.dismiss();
                            if (taskSave.isSuccessful()) {

                                Toast.makeText(CreateChallengePopUpActivity.this, "La informacion ha sido registrada", Toast.LENGTH_SHORT).show();

                            } else {

                                Toast.makeText(CreateChallengePopUpActivity.this, "No se pudo almacenar la información", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }


            }


        });


        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        getWindow().setLayout((int) (width * .8), (int) (height * .5));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;

        getWindow().setAttributes(params);

    }

    private void showDatePickerDialogStart() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                String selectedDate = twoDigits(day) + "/" + twoDigits(month + 1) + "/" + year;
                etPlannedDate.setText(selectedDate);


            }
        });

        newFragment.show(getSupportFragmentManager(), "datePicker");
    }


    private void showDatePickerDialogFinish() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                String selectedDate = twoDigits(day) + "/" + twoDigits(month + 1) + "/" + year;
                etDeadline.setText(selectedDate);


            }
        });

        newFragment.show(getSupportFragmentManager(), "datePicker");
    }


    private String twoDigits(int n) {
        return (n <= 9) ? ("0" + n) : String.valueOf(n);
    }

    @Override
    protected void onStart() {
        super.onStart();
        ViewedMessageHelper.updateOnline(true,CreateChallengePopUpActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false,CreateChallengePopUpActivity.this);
    }
}