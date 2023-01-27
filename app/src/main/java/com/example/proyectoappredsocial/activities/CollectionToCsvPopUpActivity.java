package com.example.proyectoappredsocial.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.proyectoappredsocial.R;
import com.example.proyectoappredsocial.providers.AuthProvider;
import com.example.proyectoappredsocial.providers.BooksProvider;
import com.example.proyectoappredsocial.utils.ViewedMessageHelper;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import org.json.JSONObject;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import dmax.dialog.SpotsDialog;

public class CollectionToCsvPopUpActivity extends AppCompatActivity {


    AuthProvider authProvider;
    BooksProvider booksProvider;


    AlertDialog alertDialog;
    Button btn_rate;
    RatingBar ratingBar;
    Float rating;
    String extraBookId;
    CheckBox c1, c2;

    Button btn_export;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_to_csv_pop_up);

        authProvider = new AuthProvider();
        booksProvider = new BooksProvider();
        btn_rate = findViewById(R.id.btn_rate);
        ratingBar = findViewById(R.id.ratingBar);
        extraBookId = getIntent().getStringExtra("idBook");
        btn_export = findViewById(R.id.btn_export);

        c1 = findViewById(R.id.checkbox_csv);
        c2 = findViewById(R.id.checkbox_json);

        alertDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere un momento...")
                .setCancelable(false)
                .build();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        getWindow().setLayout((int) (width * .8), (int) (height * .7));
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;

        getWindow().setAttributes(params);

        btn_export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validar();
            }
        });


    }


    @Override
    protected void onStart() {
        super.onStart();
        ViewedMessageHelper.updateOnline(true, CollectionToCsvPopUpActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false, CollectionToCsvPopUpActivity.this);
    }

    public void convertToCsv() {
        booksProvider.getBooksByUserQuery(authProvider.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {


                String path = getFilesDir().getAbsolutePath();
                File file = new File(path, "library.csv");


                FileWriter outputfile = null;
                try {
                    outputfile = new FileWriter(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                CsvMapWriter writer = new CsvMapWriter(outputfile, CsvPreference.STANDARD_PREFERENCE);


                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (file.exists()) {

                    try {
                        String[] arrayKeys = {"title", "description", "publisher", "publishedDate"};
                        writer.writeHeader(arrayKeys);

                        for (int i = 0; i < queryDocumentSnapshots.getDocuments().size(); i++) {
                            writer.write(queryDocumentSnapshots.getDocuments().get(i).getData(), arrayKeys);
                        }

                        for (int i = 0; i < queryDocumentSnapshots.getDocuments().size(); i++) {
                            JSONObject jsonObject = new JSONObject(queryDocumentSnapshots.getDocuments().get(i).getData());

                            Gson gson = new Gson();
                            String json = gson.toJson(jsonObject);
                            Log.i("erny", json);


                        }


                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }

            }


        });


    }

    public void validar() {
        if (c1.isChecked()) {

            convertToCsv();

        }
    }


}

