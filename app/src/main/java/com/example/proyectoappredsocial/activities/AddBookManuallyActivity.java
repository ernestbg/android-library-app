package com.example.proyectoappredsocial.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.proyectoappredsocial.R;
import com.example.proyectoappredsocial.models.Book;
import com.example.proyectoappredsocial.models.Post;
import com.example.proyectoappredsocial.providers.AuthProvider;
import com.example.proyectoappredsocial.providers.BooksProvider;
import com.example.proyectoappredsocial.providers.ImageProvider;
import com.example.proyectoappredsocial.providers.PostProvider;
import com.example.proyectoappredsocial.utils.FileUtil;
import com.example.proyectoappredsocial.utils.ViewedMessageHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import dmax.dialog.SpotsDialog;

public class AddBookManuallyActivity extends AppCompatActivity {

    ImageProvider imageProvider;
    AuthProvider authProvider;
    BooksProvider booksProvider;

    private final int GALLERY_REQUEST_CODE = 1;
    private final int PHOTO_REQUEST_CODE = 3;
    File imageFile;

    String absolutePhotoPath;
    String photoPath;
    File photoFile;


    ImageView iv_uploadImage;
    TextInputEditText ti_title;
    TextInputEditText ti_author;
    TextInputEditText ti_publisher;
    TextInputEditText ti_subject;
    

    ImageView imageBook;
    String title;
    String subtitle;
    String author;
    String publisher;
    String publishedDate;
    String description;
    String subject;
    int pageCount;
    float price;
    String volume;
    String isbn;


    Button btn_addBook;

    AlertDialog alertDialog;
    AlertDialog.Builder ad_builderSelector;
    CharSequence options[];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book_manually);


        imageProvider = new ImageProvider();

        booksProvider = new BooksProvider();
        authProvider = new AuthProvider();

        iv_uploadImage = findViewById(R.id.iv_uploadImage);

        ImageView imageView_arrow_back;


        imageView_arrow_back = findViewById(R.id.imageView_arrow_back);

        imageView_arrow_back.setOnClickListener(view -> finish());


        ti_title = findViewById(R.id.ti_title);
        ti_author = findViewById(R.id.ti_author);
        ti_publisher = findViewById(R.id.ti_publisher);
        ti_subject = findViewById(R.id.ti_subject);


        btn_addBook = findViewById(R.id.btn_addBook);


        btn_addBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickPost();
            }
        });

        iv_uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectOptionImage(GALLERY_REQUEST_CODE);


            }
        });


        ad_builderSelector = new AlertDialog.Builder(this);
        ad_builderSelector.setTitle("Selecciona una opción");

        options = new CharSequence[]{"Elegir imagen de la galería", "Tomar foto"};

        alertDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere un momento...")
                .setCancelable(false)
                .build();


    }

    @Override
    protected void onStart() {
        super.onStart();
        ViewedMessageHelper.updateOnline(true, AddBookManuallyActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false, AddBookManuallyActivity.this);
    }

    private void saveImageAndBook(File imageFile) {
        alertDialog.show();
        imageProvider.save(AddBookManuallyActivity.this, imageFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot1) {

                if (taskSnapshot1.getMetadata() != null) {

                    if (taskSnapshot1.getMetadata().getReference() != null) {

                        Task<Uri> result = taskSnapshot1.getStorage().getDownloadUrl();
                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String url = uri.toString();


                                Book book = new Book();
                                book.setIdUser(authProvider.getUid());
                                book.setImageBook(url);
                                book.setTitle(title);
                                book.setSubtitle(subtitle);
                                book.setAuthor(author);
                                book.setPublisher(publisher);
                                book.setPublishedDate(publishedDate);
                                book.setDescription(description);
                                book.setSubject(subject);
                                book.setPrice(price);
                                book.setPageCount(pageCount);
                                book.setVolume(volume);
                                book.setIsbn(isbn);
                                book.setRead(false);


                                booksProvider.save(book).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> taskSave) {
                                        alertDialog.dismiss();
                                        if (taskSave.isSuccessful()) {
                                            clearForm();
                                            Toast.makeText(AddBookManuallyActivity.this, "La informacion ha sido registrada", Toast.LENGTH_SHORT).show();

                                        } else {

                                            Toast.makeText(AddBookManuallyActivity.this, "No se pudo almacenar la información", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }
                        });
                    }

                } else {
                    alertDialog.dismiss();
                    Toast.makeText(AddBookManuallyActivity.this, "No se pudo almacenar la imagen 1", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void selectOptionImage(int numberImage) {

        ad_builderSelector.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    if (numberImage == 1) {
                        openGallery(GALLERY_REQUEST_CODE);
                    }
                } else if (i == 1) {
                    if (numberImage == 1) {
                        takePhoto(PHOTO_REQUEST_CODE);
                    }
                }
            }
        });

        ad_builderSelector.show();


    }

    private void takePhoto(int requestCode) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(AddBookManuallyActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                goToCamera(requestCode);
            } else {
                ActivityCompat.requestPermissions(AddBookManuallyActivity.this, new String[]{Manifest.permission.CAMERA}, requestCode);
            }
        } else {
            goToCamera(requestCode);
        }
    }

    private void goToCamera(int req) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File photoFile = null;
        try {
            photoFile = createPhotoFile(req);
        } catch (Exception e) {
            Toast.makeText(this, "Hubo un error con el archivo " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        if (photoFile != null) {
            Uri photoUri = FileProvider.getUriForFile(AddBookManuallyActivity.this, "com.example.proyectoappredsocial", photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(takePictureIntent, req);
        }
    }


    private File createPhotoFile(int requestCode) throws IOException {

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File photoFile = File.createTempFile(
                new Date() + "_photo",
                ".jpg",
                storageDir
        );
        if (requestCode == PHOTO_REQUEST_CODE) {
            photoPath = "file:" + photoFile.getAbsolutePath();
            absolutePhotoPath = photoFile.getAbsolutePath();
        }
        return photoFile;


    }

    private void clickPost() {


        title = ti_title.getText().toString();
        author = ti_author.getText().toString();
        publisher = ti_publisher.getText().toString();
        subject = ti_subject.getText().toString();




        if (!title.isEmpty() && !author.isEmpty()) {

            if (imageFile != null) {
                saveImageAndBook(imageFile);
            } else if (photoFile != null) {
                saveImageAndBook(photoFile);
            } else if (imageFile != null) {
                saveImageAndBook(imageFile);
            } else if (photoFile != null) {
                saveImageAndBook(photoFile);
            }
        } else {
            Toast.makeText(AddBookManuallyActivity.this, "Debe rellenar los campos", Toast.LENGTH_LONG).show();
        }


    }

    private void clearForm() {
        ti_title.setText("");
        ti_author.setText("");
        ti_publisher.setText("");
        ti_subject.setText("");
        iv_uploadImage.setImageResource(R.drawable.ic_baseline_image_search_24);

        title = "";
        author = "";
        publisher = "";
        subject = "";
        imageFile = null;

    }


    private void openGallery(int request_code) {

        Intent intentGallery = new Intent(Intent.ACTION_GET_CONTENT);
        intentGallery.setType("image/*");
        startActivityForResult(intentGallery, request_code);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PHOTO_REQUEST_CODE) {
            if (permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                goToCamera(requestCode);
            } else {
                Toast.makeText(this, "Se necesita habilitar los permisos", Toast.LENGTH_LONG).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Seleccionar desde la galería

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            try {

                photoFile = null;
                imageFile = FileUtil.from(this, data.getData());
                iv_uploadImage.setImageBitmap(BitmapFactory.decodeFile(imageFile.getAbsolutePath()));
            } catch (Exception e) {
                Log.d("ERROR", "Se ha producido un error" + e.getMessage());
                Toast.makeText(this, "Se ha producido un error" + e.getMessage(), Toast.LENGTH_LONG).show();

            }
        }


        //Seleccionar foto

        if (requestCode == PHOTO_REQUEST_CODE && resultCode == RESULT_OK) {
            imageFile = null;
            photoFile = new File(absolutePhotoPath);
            Picasso.with(AddBookManuallyActivity.this).load(photoPath).into(iv_uploadImage);
        }


    }


}