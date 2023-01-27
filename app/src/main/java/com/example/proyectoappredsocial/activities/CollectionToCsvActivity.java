package com.example.proyectoappredsocial.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.example.proyectoappredsocial.R;
import com.example.proyectoappredsocial.providers.AuthProvider;
import com.example.proyectoappredsocial.providers.BooksProvider;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import org.json.JSONObject;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CollectionToCsvActivity extends AppCompatActivity {

    BooksProvider booksProvider;
    AuthProvider authProvider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_to_csv);

        booksProvider = new BooksProvider();
        authProvider = new AuthProvider();


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


}







