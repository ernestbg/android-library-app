package com.example.proyectoappredsocial.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectoappredsocial.R;
import com.example.proyectoappredsocial.models.Challenge;
import com.example.proyectoappredsocial.providers.AuthProvider;
import com.example.proyectoappredsocial.providers.ChallengeProvider;
import com.example.proyectoappredsocial.providers.ReadsProvider;
import com.example.proyectoappredsocial.providers.UsersProvider;
import com.example.proyectoappredsocial.utils.RelativeTime;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class ChallengeAdapter extends FirestoreRecyclerAdapter<Challenge, ChallengeAdapter.ViewHolder> {

    Context context;
    UsersProvider usersProvider;
    AuthProvider authProvider;
    ChallengeProvider challengeProvider;
    ReadsProvider readsProvider;
    int totalBooksRead = 0;


    public ChallengeAdapter(FirestoreRecyclerOptions<Challenge> options, Context context) {
        super(options);
        this.context = context;
        usersProvider = new UsersProvider();
        authProvider = new AuthProvider();
        challengeProvider = new ChallengeProvider();
        readsProvider = new ReadsProvider();
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Challenge challenge) {

        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(position);
        String idChallenge = documentSnapshot.getId();

        String relativeTime = RelativeTime.getTimeAgo(challenge.getTimestamp(), context);


        holder.tv_startDate.setText(challenge.getStartDate());
        holder.tv_finishDate.setText(challenge.getFinishDate());
        holder.tv_relativeTimeChallenge.setText(relativeTime);


        readsProvider.getReadByUser(authProvider.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        long timestamp = document.getLong("timestamp");

                        Date d = new Date(timestamp);

                        Instant instant = d.toInstant();
                        LocalDateTime ldt = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();

                        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");


                        try {
                            Date dateRead = sdf.parse(ldt.format(fmt));
                            Date startDate = sdf.parse(challenge.getStartDate());
                            Date finishDate = sdf.parse(challenge.getFinishDate());
                            if (dateRead.after(startDate) && dateRead.before(finishDate)) {
                                totalBooksRead += 1;

                                Log.i("erny", String.valueOf(totalBooksRead));
                                holder.tv_progressInfo.setText("Has leído " + totalBooksRead + " de " + challenge.getTotalBooks() + " libros");


                            }

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }


                    }
                } else {
                    Log.i("erny", "Error getting documents: ", task.getException());
                }

            }
        });

        totalBooksRead = 0;








        /*holder.viewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, PopUpActivity.class);
                i.putExtra("idChallenge", idChallenge);
                context.startActivity(i);

            }
        });*/

        holder.iv_deleteChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialogDeleteChallenge(idChallenge);

            }
        });


    }

    private void showAlertDialogDeleteChallenge(String idChallenge) {

        new AlertDialog.Builder(context).setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Eliminar desafio")
                .setMessage("¿Seguro que quieres eliminar este desafio?")
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteChallenge(idChallenge);
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteChallenge(String idChallenge) {
        challengeProvider.delete(idChallenge).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(context, "el desafio se eliminó correctamente", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "error al eliminar", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_challenge, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_progressInfo;
        TextView tv_startDate;
        TextView tv_finishDate;
        TextView tv_relativeTimeChallenge;
        ImageView iv_deleteChallenge;
        View viewHolder;

        public ViewHolder(View view) {
            super(view);

            tv_progressInfo = view.findViewById(R.id.tv_progressInfo);
            tv_startDate = view.findViewById(R.id.tv_startDate);
            tv_finishDate = view.findViewById(R.id.tv_finishDate);
            tv_relativeTimeChallenge = view.findViewById(R.id.tv_relativeTimeChallenge);
            iv_deleteChallenge = view.findViewById(R.id.iv_deleteChallenge);


            viewHolder = view;

        }


    }


}
