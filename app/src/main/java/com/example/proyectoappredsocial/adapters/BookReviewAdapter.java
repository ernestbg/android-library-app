package com.example.proyectoappredsocial.adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectoappredsocial.R;
import com.example.proyectoappredsocial.models.BookReview;
import com.example.proyectoappredsocial.providers.BooksProvider;
import com.example.proyectoappredsocial.providers.UsersProvider;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class BookReviewAdapter extends FirestoreRecyclerAdapter<BookReview, BookReviewAdapter.ViewHolder> {

    Context context;
    UsersProvider usersProvider;

    BooksProvider booksProvider;

    public BookReviewAdapter(FirestoreRecyclerOptions<BookReview> options, Context context) {
        super(options);
        this.context = context;
        usersProvider = new UsersProvider();
        booksProvider=new BooksProvider();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull BookReview bookReview) {

        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(position);
        String bookReviewId = documentSnapshot.getId();

        holder.tv_textReview.setText(bookReview.getTextReview());



        Date d = new Date(bookReview.getDateReview());

        Instant instant = d.toInstant();
        LocalDateTime ldt = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String dateReview=ldt.format(fmt);

        holder.tv_dateReview.setText(dateReview);
        holder.ratingBar.setRating(bookReview.getRating());

        String idUser = documentSnapshot.getString("idUser");
        getUserInfo(idUser, holder);




    }




    private void getUserInfo(String idUser, ViewHolder holder) {

        usersProvider.getUserTask(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {

                    if (documentSnapshot.contains("username")) {
                        String username = documentSnapshot.getString("username");


                        holder.tv_authorReview.setText(username);
                    }
                    if (documentSnapshot.contains("imageProfile")) {
                        String imageProfile = documentSnapshot.getString("imageProfile");
                        if (imageProfile != null) {
                            if (!imageProfile.isEmpty()) {
                                Picasso.with(context).load(imageProfile).into(holder.civ_authorReview);


                            }
                        }
                    }
                }
            }
        });

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_book_review, parent, false);
        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView civ_authorReview;
        TextView tv_authorReview;
        RatingBar ratingBar;
        TextView tv_dateReview;
        TextView tv_textReview;
        View viewHolder;

        public ViewHolder(View view) {
            super(view);
            civ_authorReview = view.findViewById(R.id.civ_authorReview);
            tv_authorReview = view.findViewById(R.id.tv_authorReview);
            ratingBar = view.findViewById(R.id.ratingBar);
            tv_dateReview = view.findViewById(R.id.tv_dateReview);
            tv_textReview = view.findViewById(R.id.tv_textReview);
            viewHolder = view;

        }


    }


}