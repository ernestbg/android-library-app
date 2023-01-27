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
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyectoappredsocial.R;
import com.example.proyectoappredsocial.models.Post;
import com.example.proyectoappredsocial.providers.AuthProvider;
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


public class WritePostActivity extends AppCompatActivity {

    ImageView imageView;
    private final int GALLERY_REQUEST_CODE = 1;
    private final int PHOTO_REQUEST_CODE = 3;
    File imageFile;
    Button btn_post;
    ImageProvider imageProvider;
    TextInputEditText ti_title;
    TextInputEditText ti_description;

    PostProvider postProvider;
    AuthProvider authProvider;


    ImageView imageView_paper;
    ImageView imageView_ebook;

    String categorySelected;
    TextView tv_categorySelected;

    String title;
    String description;

    AlertDialog alertDialog;
    AlertDialog.Builder ad_builderSelector;

    CharSequence options[];

    String absolutePhotoPath;
    String photoPath;
    File photoFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_post);


        ImageView imageView_arrow_back;

        imageView = findViewById(R.id.upload_image1);

        btn_post = findViewById(R.id.btn_publicar);
        imageProvider = new ImageProvider();

        postProvider = new PostProvider();
        authProvider = new AuthProvider();

        ti_title = findViewById(R.id.ti_title);
        ti_description = findViewById(R.id.ti_description);


        alertDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere un momento...")
                .setCancelable(false)
                .build();

        ad_builderSelector = new AlertDialog.Builder(this);
        ad_builderSelector.setTitle("Selecciona una opción");

        options = new CharSequence[]{"Elegir imagen de la galería", "Tomar foto"};

        imageView_arrow_back = findViewById(R.id.imageView_arrow_back);

        imageView_arrow_back.setOnClickListener(view -> finish());

        btn_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickPost();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectOptionImage(GALLERY_REQUEST_CODE);


            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        ViewedMessageHelper.updateOnline(true,WritePostActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false,WritePostActivity.this);
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
            if (ActivityCompat.checkSelfPermission(WritePostActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                goToCamera(requestCode);
            } else {
                ActivityCompat.requestPermissions(WritePostActivity.this, new String[]{Manifest.permission.CAMERA}, requestCode);
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
            Uri photoUri = FileProvider.getUriForFile(WritePostActivity.this, "com.example.proyectoappredsocial", photoFile);
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
        description = ti_description.getText().toString();

        if (!title.isEmpty() && !description.isEmpty()) {

            if (imageFile != null) {
                saveImageAndPost(imageFile);
            } else if (photoFile != null) {
                saveImageAndPost(photoFile);
            } else if (imageFile != null) {
                saveImageAndPost(imageFile);
            } else if (photoFile != null) {
                saveImageAndPost(photoFile);
            }
        } else {
            Toast.makeText(WritePostActivity.this, "Debe rellenar los campos", Toast.LENGTH_LONG).show();
        }


    }

    private void saveImageAndPost(File imageFile) {
        alertDialog.show();
        imageProvider.save(WritePostActivity.this, imageFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot1) {

                if (taskSnapshot1.getMetadata() != null) {

                    if (taskSnapshot1.getMetadata().getReference() != null) {

                        Task<Uri> result = taskSnapshot1.getStorage().getDownloadUrl();
                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String url = uri.toString();


                                Post post = new Post();
                                post.setImagePost(url);
                                post.setTitle(title.toLowerCase());
                                post.setDescription(description);
                                post.setCategory(categorySelected);
                                post.setIdUser(authProvider.getUid());
                                post.setTimestamp(new Date().getTime());


                                postProvider.save(post).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> taskSave) {
                                        alertDialog.dismiss();
                                        if (taskSave.isSuccessful()) {
                                            clearForm();
                                            finish();
                                            Toast.makeText(WritePostActivity.this, "La informacion ha sido registrada", Toast.LENGTH_SHORT).show();

                                        } else {

                                            Toast.makeText(WritePostActivity.this, "No se pudo almacenar la información", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }
                        });
                    }

                } else {
                    alertDialog.dismiss();
                    Toast.makeText(WritePostActivity.this, "No se pudo almacenar la imagen 1", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    private void clearForm() {
        ti_title.setText("");
        ti_description.setText("");

        imageView.setImageResource(R.drawable.ic_camera_black);
        title = "";
        description = "";
        categorySelected = "";
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
                imageView.setImageBitmap(BitmapFactory.decodeFile(imageFile.getAbsolutePath()));
            } catch (Exception e) {
                Log.d("ERROR", "Se ha producido un error" + e.getMessage());
                Toast.makeText(this, "Se ha producido un error" + e.getMessage(), Toast.LENGTH_LONG).show();

            }
        }


        //Seleccionar foto

        if (requestCode == PHOTO_REQUEST_CODE && resultCode == RESULT_OK) {
            imageFile = null;
            photoFile = new File(absolutePhotoPath);
            Picasso.with(WritePostActivity.this).load(photoPath).into(imageView);
        }


    }
}