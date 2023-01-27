package com.example.proyectoappredsocial.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectoappredsocial.R;
import com.example.proyectoappredsocial.activities.MyBookDetailsActivity;
import com.example.proyectoappredsocial.models.Book;
import com.example.proyectoappredsocial.providers.AuthProvider;
import com.example.proyectoappredsocial.providers.BooksProvider;
import com.example.proyectoappredsocial.providers.UsersProvider;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

public class MyBooksAdapter extends FirestoreRecyclerAdapter<Book, MyBooksAdapter.ViewHolder> {

    Context context;
    BooksProvider booksProvider;
    AuthProvider authProvider;
    UsersProvider usersProvider;


    public MyBooksAdapter(FirestoreRecyclerOptions<Book> options, Context context) {
        super(options);
        this.context = context;
        booksProvider = new BooksProvider();
        usersProvider = new UsersProvider();
        authProvider = new AuthProvider();


    }




    @Override
    protected void onBindViewHolder(@NonNull MyBooksAdapter.ViewHolder holder, int position, @NonNull Book book) {


        DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(position);
        String bookId = documentSnapshot.getId();


        holder.tv_title.setText(book.getTitle());

        if (book.getAuthors().size() != 0) {
            for (int i = 0; i < book.getAuthors().size(); i++) {
                holder.tv_authors.setText("de " + book.getAuthors().get(i));

            }
        }

        holder.tv_publisher.setText(book.getPublisher());
        holder.tv_publishedDate.setText("Año de publicación: "+ book.getPublishedDate());
        holder.tv_pageCount.setText(book.getPageCount()+ " páginas");
        holder.ratingBar.setRating(book.getRating());

        if (book.getImageBook() != null) {
            if (!book.getImageBook().isEmpty()) {
                Picasso.with(context).load(book.getImageBook()).into(holder.iv_book);
            }
        }

        holder.viewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, MyBookDetailsActivity.class);
                i.putExtra("bookId", bookId);
                context.startActivity(i);

            }
        });


    }


    @NonNull
    @Override
    public MyBooksAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_books, parent, false);
        return new MyBooksAdapter.ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_title;
        TextView tv_authors;
        RatingBar ratingBar;
        TextView tv_publisher;
        TextView tv_publishedDate;
        TextView tv_pageCount;
        ImageView iv_book;
        View viewHolder;

        public ViewHolder(View view) {
            super(view);
            tv_title = view.findViewById(R.id.tv_title);
            tv_authors = view.findViewById(R.id.tv_authors);
            ratingBar = view.findViewById(R.id.ratingBar);
            tv_publisher = view.findViewById(R.id.tv_publisher);
            tv_publishedDate = view.findViewById(R.id.tv_publishedDate);
            tv_pageCount = view.findViewById(R.id.tv_pageCount);
            iv_book = view.findViewById(R.id.iv_book);
            viewHolder = view;

        }
    }


}
