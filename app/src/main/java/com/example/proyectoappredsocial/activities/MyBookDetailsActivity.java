package com.example.proyectoappredsocial.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.renderscript.Allocation;
import androidx.renderscript.Element;
import androidx.renderscript.RenderScript;
import androidx.renderscript.ScriptIntrinsicBlur;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyectoappredsocial.R;
import com.example.proyectoappredsocial.models.Read;
import com.example.proyectoappredsocial.providers.AuthProvider;
import com.example.proyectoappredsocial.providers.BooksProvider;
import com.example.proyectoappredsocial.providers.ImageProvider;
import com.example.proyectoappredsocial.providers.ReadsProvider;
import com.example.proyectoappredsocial.providers.UsersProvider;
import com.example.proyectoappredsocial.utils.ViewedMessageHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;

import jp.wasabeef.blurry.Blurry;

public class MyBookDetailsActivity extends AppCompatActivity {

    BooksProvider booksProvider;
    ImageProvider imageProvider;
    AuthProvider authProvider;
    ReadsProvider readsProvider;
    UsersProvider usersProvider;
    View viewTitleDescription;
    ImageView iv_imageBook;
    ImageView iv_imageBook2;
    TextView tv_title;
    TextView tv_author;
    TextView tv_publisher;
    TextView tv_publishedDate;
    TextView tv_description;
    TextView tv_titleDescription;
    TextView tv_subject;
    TextView tv_pageCount;
    View rootView;
    private static final float BITMAP_SCALE = 0.4f;
    private static final float BLUR_RADIUS = 7.5f;

    TextView tv_bookState;
    Button btn_rate;


    String extraBookId;
    String imageBookUrl;

    int checkedItem;
    String valor;

    Boolean read;
    TextView tv_deleteBook;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_book_detail);

        booksProvider = new BooksProvider();
        imageProvider = new ImageProvider();
        authProvider = new AuthProvider();
        readsProvider = new ReadsProvider();
        usersProvider = new UsersProvider();


        iv_imageBook = findViewById(R.id.iv_imageBook);
        iv_imageBook2 = findViewById(R.id.iv_imageBook2);
        tv_title = findViewById(R.id.tv_title);
        tv_author = findViewById(R.id.tv_author);
        tv_description = findViewById(R.id.tv_description);
        tv_titleDescription = findViewById(R.id.tv_titleDescription);
        tv_publisher = findViewById(R.id.tv_publisher);
        tv_publishedDate = findViewById(R.id.tv_publishDate);
        tv_pageCount = findViewById(R.id.tv_pageCount);


        tv_bookState = findViewById(R.id.tv_bookState);
        btn_rate = findViewById(R.id.btn_rate);
        tv_deleteBook = findViewById(R.id.tv_deleteBook);
        viewTitleDescription = findViewById(R.id.viewTitleDescription);

        extraBookId = getIntent().getStringExtra("bookId");

       /* booksProvider.getBookById(extraBookId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {


                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("read")) {
                        read = documentSnapshot.getBoolean("read");

                        if (read != null) {
                            if (read) {


                                btn_bookState.setText("Leído");
                                btn_bookState.setBackgroundColor(Color.GREEN);


                            } else {


                                btn_bookState.setText("Marcar como leído");
                                btn_bookState.setBackgroundColor(Color.GRAY);


                            }
                        }


                    }


                }
            }


        });*/


        tv_bookState.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Read read = new Read();
                read.setIdUser(authProvider.getUid());
                read.setIdBook(extraBookId);
                read.setTimestamp(new Date().getTime());
                read(read);

            }
        });

        btn_rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MyBookDetailsActivity.this, RateBookPopUpActivity.class);
                intent.putExtra("idBook", extraBookId);
                startActivity(intent);
            }
        });

        tv_deleteBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                confirmDeleteBook(extraBookId);


            }
        });


      /*  rootView = (View) findViewById(R.id.content);

        rootView.post(new Runnable() {
            @Override
            public void run() {
                Blurry.with(getApplicationContext()).radius(25).sampling(2).onto((ViewGroup) rootView);
            }
        });*/


        getBook();
        checkIfReadExists(extraBookId, authProvider.getUid());


    }

    @Override
    protected void onStart() {
        super.onStart();
        ViewedMessageHelper.updateOnline(true, MyBookDetailsActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false, MyBookDetailsActivity.this);
    }


    private void read(Read read) {
        readsProvider.getReadByBookAndUser(read.getIdBook(), authProvider.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numeroDocumentos = queryDocumentSnapshots.size();
                if (numeroDocumentos > 0) {
                    String idRead = queryDocumentSnapshots.getDocuments().get(0).getId();
                    tv_bookState.setTextColor(Color.GRAY);
                    tv_bookState.setText("Marcar como leído");
                    tv_bookState.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add_bluegreen, 0, 0, 0);



                    readsProvider.delete(idRead);
                } else {
                    tv_bookState.setTextColor(Color.GREEN);
                    tv_bookState.setText("Leído");

                    readsProvider.create(read);
                }
            }
        });


    }


    private void checkIfReadExists(String idBook, String idUser) {
        readsProvider.getReadByBookAndUser(idBook, idUser).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numberOfDocuments = queryDocumentSnapshots.size();
                if (numberOfDocuments > 0) {
                    tv_bookState.setTextColor(Color.GREEN);
                    tv_bookState.setText("Leído");


                } else {

                    tv_bookState.setText("Marcar como leído");
                    tv_bookState.setTextColor(Color.GRAY);

                    tv_bookState.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add_bluegreen, 0, 0, 0);


                }
            }
        });


    }

    private void showAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MyBookDetailsActivity.this);
        alertDialog.setTitle("AlertDialog");


        String[] items = {"Leído", "Sin leer"};

        alertDialog.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        checkedItem = which;


                        valor = items[which];

                        booksProvider.updateBookState(true, extraBookId);


                        break;
                    case 1:

                        valor = items[which];

                        checkedItem = which;

                        booksProvider.updateBookState(false, extraBookId);


                        break;

                }
            }
        });


        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


            }
        });


        AlertDialog alert = alertDialog.create();
        alert.show();


    }


    private void getBook() {

        booksProvider.getBookById(extraBookId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("imageBook")) {
                        imageBookUrl = documentSnapshot.getString("imageBook");
                        Picasso.with(MyBookDetailsActivity.this).load(imageBookUrl).into(iv_imageBook);
                        Picasso.with(MyBookDetailsActivity.this).load(imageBookUrl).into(iv_imageBook2);


                    }

                    if (documentSnapshot.contains("title")) {
                        String title = documentSnapshot.getString("title");
                        tv_title.setText(title);

                    }

                    if (documentSnapshot.contains("authors")) {

                        List<String> authors = (List<String>) documentSnapshot.get("authors");
                        for (int i = 0; i < authors.size(); i++) {
                            tv_author.setText("de " + authors.get(i));

                        }
                    }

                    if (documentSnapshot.contains("description")) {
                        String description = documentSnapshot.getString("description");

                        if (!description.equals("")) {

                            tv_description.setText(description);

                        } else {
                            tv_titleDescription.setVisibility(View.GONE);
                            viewTitleDescription.setVisibility(View.GONE);
                            tv_description.setVisibility(View.GONE);

                        }

                    }


                    if (documentSnapshot.contains("publisher")) {
                        String publisher = documentSnapshot.getString("publisher");


                        tv_publisher.setText("Editorial: " + publisher);


                    }

                    if (documentSnapshot.contains("publishedDate")) {
                        String publishedDate = documentSnapshot.getString("publishedDate");

                        tv_publishedDate.setText("Fecha de publicación: " + publishedDate);

                    }

                    if (documentSnapshot.contains("pageCount")) {
                        String pageCount = String.valueOf(documentSnapshot.getLong("pageCount"));

                        tv_pageCount.setText("Páginas: " + pageCount);

                    }


                }

            }
        });
    }

    private void deleteBook(String idBook) {
        booksProvider.delete(idBook).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MyBookDetailsActivity.this, "el libro se eliminó correctamente", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MyBookDetailsActivity.this, "error al eliminar", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }


    private void confirmDeleteBook(String idBook) {

        new AlertDialog.Builder(MyBookDetailsActivity.this).setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Eliminar libro")
                .setMessage("¿Seguro que quieres eliminar este libro de tu biblioteca?")
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteBook(idBook);
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }


}



