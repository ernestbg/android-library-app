package com.example.proyectoappredsocial.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectoappredsocial.R;
import com.example.proyectoappredsocial.activities.BookDetailsActivity;
import com.example.proyectoappredsocial.models.Book;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    // creating variables for arraylist and context.
    private ArrayList<Book> bookInfoArrayList;
    private Context context;

    // creating constructor for array list and context.
    public BookAdapter(ArrayList<Book> bookInfoArrayList, Context context) {
        this.bookInfoArrayList = bookInfoArrayList;
        this.context = context;
    }



    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflating our layout for item of recycler view item.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_books, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {

        // inside on bind view holder method we are
        // setting ou data to each UI component.
        Book bookInfo = bookInfoArrayList.get(position);
        holder.tv_title.setText(bookInfo.getTitle());
        holder.tv_publisher.setText(bookInfo.getPublisher());
        holder.tv_pageCount.setText("Páginas : " + bookInfo.getPageCount());
        holder.tv_publishedDate.setText(bookInfo.getPublishedDate());

        // below line is use to set image from URL in our image view.
        Picasso.with(context).load(bookInfo.getImageBook()).into(holder.iv_book);

        // below line is use to add on click listener for our item of recycler view.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // inside on click listener method we are calling a new activity
                // and passing all the data of that item in next intent.
                Intent i = new Intent(context, BookDetailsActivity.class);
                i.putExtra("title", bookInfo.getTitle());
                i.putExtra("subtitle", bookInfo.getSubtitle());
                i.putExtra("authors", bookInfo.getAuthors());
                i.putExtra("publisher", bookInfo.getPublisher());
                i.putExtra("publishedDate", bookInfo.getPublishedDate());
                i.putExtra("description", bookInfo.getDescription());
                i.putExtra("pageCount", bookInfo.getPageCount());
                i.putExtra("imageBook", bookInfo.getImageBook());
                i.putExtra("bookIsbn", bookInfo.getIsbn());


                // after passing that data we are
                // starting our new  intent.
                context.startActivity(i);
            }
        });



    }

    @Override
    public int getItemCount() {
        // inside get item count method we
        // are returning the size of our array list.
        return bookInfoArrayList.size();
    }

    public class BookViewHolder extends RecyclerView.ViewHolder {
        // below line is use to initialize
        // our text view and image views.
        TextView tv_title, tv_publisher, tv_pageCount, tv_publishedDate;
        ImageView iv_book;

        public BookViewHolder(View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_publisher = itemView.findViewById(R.id.tv_publisher);
            tv_pageCount = itemView.findViewById(R.id.tv_pageCount);
            tv_publishedDate = itemView.findViewById(R.id.tv_publishedDate);
            iv_book = itemView.findViewById(R.id.iv_book);
        }
    }
}
