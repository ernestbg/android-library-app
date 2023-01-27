package com.example.proyectoappredsocial.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.proyectoappredsocial.R;
import com.example.proyectoappredsocial.models.Challenge;
import com.example.proyectoappredsocial.providers.AuthProvider;
import com.example.proyectoappredsocial.providers.ChallengeProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Date;

import dmax.dialog.SpotsDialog;


public class CreateChallengePopUpFragment extends DialogFragment {
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
    View view;

    public CreateChallengePopUpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_create_challenge_pop_up, container, false);


        challengeProvider = new ChallengeProvider();
        authProvider = new AuthProvider();


        ti_totalBooks = view.findViewById(R.id.ti_totalBooks);


        btn_startChallenge = view.findViewById(R.id.btn_startChallenge);

        etPlannedDate = view.findViewById(R.id.etPlannedDate);
        etDeadline = view.findViewById(R.id.etDeadline);


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
                .setContext(getContext())
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

                    Bundle dataChallenge = new Bundle();

                    dataChallenge.putString("totalBooks", String.valueOf(totalBooks));
                    dataChallenge.putString("startDate", startDate);
                    dataChallenge.putString("finishDate", finishDate);
                    Fragment challengeFragment = new ChallengeFragment();
                    challengeFragment.setArguments(dataChallenge);

                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container, challengeFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();

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

                                Toast.makeText(getContext(), "La informacion ha sido registrada", Toast.LENGTH_SHORT).show();

                            } else {

                                Toast.makeText(getContext(), "No se pudo almacenar la información", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }


            }


        });


        /*DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        getWindow().setLayout((int) (width * .8), (int) (height * .5));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;

        getWindow().setAttributes(params);*/


        return view;
    }

    private void showDatePickerDialogStart() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                String selectedDate = twoDigits(day) + "/" + twoDigits(month + 1) + "/" + year;
                etPlannedDate.setText(selectedDate);


            }
        });

        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }


    private void showDatePickerDialogFinish() {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                String selectedDate = twoDigits(day) + "/" + twoDigits(month + 1) + "/" + year;
                etDeadline.setText(selectedDate);


            }
        });

        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }


    private String twoDigits(int n) {
        return (n <= 9) ? ("0" + n) : String.valueOf(n);
    }
}