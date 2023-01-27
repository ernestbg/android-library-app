package com.example.proyectoappredsocial.fragments;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.compose.State;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.proyectoappredsocial.R;
import com.example.proyectoappredsocial.activities.CreateChallengePopUpActivity;
import com.example.proyectoappredsocial.adapters.ChallengeAdapter;
import com.example.proyectoappredsocial.models.Challenge;
import com.example.proyectoappredsocial.providers.AuthProvider;
import com.example.proyectoappredsocial.providers.ChallengeProvider;
import com.example.proyectoappredsocial.providers.ReadsProvider;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;


public class ChallengeFragment extends Fragment {

    View view;

    String startDate;
    String finishDate;
    String totalBooks;


    int totalBooksRead = 0;


    ReadsProvider readsProvider;
    ChallengeProvider challengeProvider;
    AuthProvider authProvider;

    ChallengeAdapter challengeAdapter;


    TextView tv_progressInfo;


    TextView tv_startDate;
    TextView tv_finishDate;

    int currentProgress = 0;
    ProgressBar progressBar;
    Button startProgress;


    Button btn_createChallenge;

    RecyclerView recyclerView;


    public ChallengeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_challenge, container, false);


        readsProvider = new ReadsProvider();
        challengeProvider = new ChallengeProvider();
        authProvider = new AuthProvider();


        tv_progressInfo = view.findViewById(R.id.tv_progressInfo);

        btn_createChallenge = view.findViewById(R.id.btn_createChallenge);


        tv_startDate = view.findViewById(R.id.tv_startDate);
        tv_finishDate = view.findViewById(R.id.tv_finishDate);

        /*progressBar = view.findViewById(R.id.progressBar);
        startProgress = view.findViewById(R.id.startProgress);
        startProgress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentProgress = currentProgress + 10;
                progressBar.setProgress(currentProgress);
                progressBar.setMax(100);


            }
        });*/

        btn_createChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), CreateChallengePopUpActivity.class);
                startActivity(intent);


               /* CreateChallengePopUpFragment createChallengePopUpFragment = new CreateChallengePopUpFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, createChallengePopUpFragment, "createChallengePopUpFragment")
                        .addToBackStack(null)
                        .commit();*/


            }
        });


        recyclerView = view.findViewById(R.id.rv_myChallenges);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

/*       tv_progressInfo.setText("Has leído  de " + totalBooks + " libros");
        tv_startDate.setText(startDate);
        tv_finishDate.setText(finishDate);*/


        return view;
    }


   /* @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Bundle datosRecuperados = getArguments();
        if (datosRecuperados == null) {
            // No hay datos, manejar excepción
            return;
        }

        totalBooks = datosRecuperados.getString("totalBooks");
        startDate = datosRecuperados.getString("startDate");
        finishDate = datosRecuperados.getString("finishDate");

        tv_progressInfo.setText("Has leído  de " + totalBooks + " libros");
        tv_startDate.setText(startDate);
        tv_finishDate.setText(finishDate);


    }*/


    @Override
    public void onStart() {
        super.onStart();

        Query query = challengeProvider.getChallengesByUser(authProvider.getUid());
        FirestoreRecyclerOptions<Challenge> options = new FirestoreRecyclerOptions.Builder<Challenge>().setQuery(query, Challenge.class).build();
        challengeAdapter = new ChallengeAdapter(options, getContext());
        recyclerView.setAdapter(challengeAdapter);
        challengeAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        challengeAdapter.stopListening();

    }


}