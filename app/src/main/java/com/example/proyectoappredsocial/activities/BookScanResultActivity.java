package com.example.proyectoappredsocial.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.proyectoappredsocial.R;
import com.example.proyectoappredsocial.models.Book;
import com.example.proyectoappredsocial.providers.AuthProvider;
import com.example.proyectoappredsocial.providers.BooksProvider;
import com.example.proyectoappredsocial.utils.ViewedMessageHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import dmax.dialog.SpotsDialog;

public class BookScanResultActivity extends AppCompatActivity {

    TextView tv_bookScanResult;
    String bookIsbn;
    BooksProvider booksProvider;
    AuthProvider authProvider;

    Button btn_addBook;
    ImageView iv_arrowBack;


    RequestQueue requestQueue;
    Book bookInfo;
    ProgressBar progressBar;
    ArrayList<String> authors;

    TextView tv_title, tv_subtitle, tv_publisher, tv_description, tv_pageCount, tv_publishDate;
    ImageView iv_bookImage;

    AlertDialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_scan_result);

        bookIsbn = getIntent().getStringExtra("isbn");
        booksProvider = new BooksProvider();
        authProvider = new AuthProvider();


        btn_addBook = findViewById(R.id.btn_addBook);
        iv_arrowBack = findViewById(R.id.iv_arrowBack);

        progressBar = findViewById(R.id.progressBar);

        alertDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere un momento...")
                .setCancelable(false)
                .build();

        tv_title = findViewById(R.id.tv_title);
        tv_subtitle = findViewById(R.id.tv_subtitle);
        tv_publisher = findViewById(R.id.tv_publisher);
        tv_description = findViewById(R.id.tv_description);
        tv_pageCount = findViewById(R.id.tv_pageCount);
        tv_publishDate = findViewById(R.id.tv_publishDate);
        iv_bookImage = findViewById(R.id.iv_book);

        iv_arrowBack.setOnClickListener(view -> finish());


        progressBar.setVisibility(View.VISIBLE);
        getBooksInfo(bookIsbn);


    }

    @Override
    protected void onStart() {
        super.onStart();
        ViewedMessageHelper.updateOnline(true,BookScanResultActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false,BookScanResultActivity.this);
    }

    private void getBooksInfo(String bookIsbn) {


        // below line is use to initialize
        // the variable for our request queue.
        requestQueue = Volley.newRequestQueue(BookScanResultActivity.this);

        // below line is use to clear cache this
        // will be use when our data is being updated.
        requestQueue.getCache().clear();

        // below is the url for getting data from API in json format.
        String url = "https://www.googleapis.com/books/v1/volumes?q=isbn:" + bookIsbn;

        // below line we are  creating a new request queue.
        RequestQueue queue = Volley.newRequestQueue(BookScanResultActivity.this);


        // below line is use to make json object request inside that we
        // are passing url, get method and getting json object. .
        JsonObjectRequest booksObjrequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressBar.setVisibility(View.GONE);
                // inside on response method we are extracting all our json data.
                try {
                    JSONArray itemsArray = response.getJSONArray("items");
                    for (int i = 0; i < itemsArray.length(); i++) {
                        JSONObject itemsObj = itemsArray.getJSONObject(i);


                        JSONObject volumeObj = itemsObj.getJSONObject("volumeInfo");
                        String title = volumeObj.optString("title");
                        String subtitle = volumeObj.optString("subtitle");
                        JSONArray authorsArray = volumeObj.getJSONArray("authors");
                        String publisher = volumeObj.optString("publisher");
                        String publishedDate = volumeObj.optString("publishedDate");
                        String description = volumeObj.optString("description");
                        int pageCount = volumeObj.optInt("pageCount");
                        JSONObject imageLinks = volumeObj.optJSONObject("imageLinks");
                        String imageBook = imageLinks.optString("thumbnail");
                        JSONObject saleInfoObj = itemsObj.optJSONObject("saleInfo");
                        ArrayList<String> authorsArrayList = new ArrayList<>();
                        if (authorsArray.length() != 0) {
                            for (int j = 0; j < authorsArray.length(); j++) {
                                authorsArrayList.add(authorsArray.optString(i));
                            }
                        }
                        // after extracting all the data we are
                        // saving this data in our modal class.


                        bookInfo = new Book(authProvider.getUid(), title, subtitle, authorsArrayList, publisher, publishedDate, description, pageCount, imageBook);


                        tv_title.setText(bookInfo.getTitle());
                        tv_subtitle.setText(bookInfo.getSubtitle());
                        tv_publisher.setText(bookInfo.getPublisher());
                        tv_publishDate.setText(bookInfo.getPublishedDate());
                        tv_description.setText(bookInfo.getDescription());
                        tv_pageCount.setText(String.valueOf(bookInfo.getPageCount()));
                        Picasso.with(BookScanResultActivity.this).load(imageBook).into(iv_bookImage);

                        btn_addBook.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                booksProvider.save(bookInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> taskSave) {
                                        alertDialog.dismiss();
                                        if (taskSave.isSuccessful()) {

                                            Toast.makeText(BookScanResultActivity.this, "La informacion ha sido registrada", Toast.LENGTH_SHORT).show();

                                        } else {

                                            Toast.makeText(BookScanResultActivity.this, "No se pudo almacenar la información", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });


                            }
                        });


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    // displaying a toast message when we get any error from API
                    Toast.makeText(BookScanResultActivity.this, "No Data Found" + e, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // also displaying error message in toast.
                Toast.makeText(BookScanResultActivity.this, "Error found is " + error, Toast.LENGTH_SHORT).show();
            }
        });
        // at last we are adding our json object
        // request in our request queue.
        queue.add(booksObjrequest);
    }

}