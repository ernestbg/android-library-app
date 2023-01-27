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
import com.example.proyectoappredsocial.models.Post;
import com.example.proyectoappredsocial.models.User;
import com.example.proyectoappredsocial.providers.AuthProvider;
import com.example.proyectoappredsocial.providers.ImageProvider;
import com.example.proyectoappredsocial.providers.PostProvider;
import com.example.proyectoappredsocial.providers.UsersProvider;
import com.example.proyectoappredsocial.utils.FileUtil;
import com.example.proyectoappredsocial.utils.ViewedMessageHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class EditPostActivity extends AppCompatActivity {


    ImageView imageViewArrowBack;
    ImageView iv_post;
    TextInputEditText ti_title;
    TextInputEditText ti_description;


    private final int GALLERY_REQUEST_CODE_PROFILE = 2;
    private final int PHOTO_REQUEST_CODE_PROFILE = 4;

    Button btn_editPost;


    AuthProvider authProvider;
    ImageProvider imageProvider;
    UsersProvider usersProvider;
    PostProvider postProvider;

    String extraIdPost;
    String title;
    String description;
    String imagePost;


    AlertDialog alertDialog;
    AlertDialog.Builder ad_builderSelector;
    CharSequence options[];

    File imageFile;

    String absolutePhotoPath;
    String photoPath;
    File photoFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        imageViewArrowBack = findViewById(R.id.imageView_arrow_back);

        extraIdPost = getIntent().getExtras().getString("postId");
        ti_title = findViewById(R.id.ti_title);
        ti_description = findViewById(R.id.ti_description);
        iv_post = findViewById(R.id.upload_image1);




        btn_editPost = findViewById(R.id.btn_editPost);
        authProvider = new AuthProvider();
        imageProvider = new ImageProvider();
        usersProvider = new UsersProvider();
        postProvider = new PostProvider();

        alertDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere un momento")
                .setCancelable(false).build();

        imageViewArrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ad_builderSelector = new AlertDialog.Builder(this);
        ad_builderSelector.setTitle("Selecciona una opción");

        options = new CharSequence[]{"Elegir imagen de la galería", "Tomar foto"};


        iv_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectOptionImage(2);
            }
        });

        btn_editPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickEditPost();
            }
        });

        getPost();


    }


    private void selectOptionImage(int numberImage) {

        ad_builderSelector.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    if (numberImage == 2) {
                        openGallery(GALLERY_REQUEST_CODE_PROFILE);
                    }
                } else if (i == 1) {
                    if (numberImage == 2) {
                        takePhoto(PHOTO_REQUEST_CODE_PROFILE);
                    }
                }
            }
        });

        ad_builderSelector.show();


    }

    private void takePhoto(int requestCode) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(EditPostActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                goToCamera(requestCode);
            } else {
                ActivityCompat.requestPermissions(EditPostActivity.this, new String[]{Manifest.permission.CAMERA}, requestCode);
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
            Uri photoUri = FileProvider.getUriForFile(EditPostActivity.this, "com.example.proyectoappredsocial", photoFile);
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
        if (requestCode == PHOTO_REQUEST_CODE_PROFILE) {
            photoPath = "file:" + photoFile.getAbsolutePath();
            absolutePhotoPath = photoFile.getAbsolutePath();
        }
        return photoFile;


    }

    private void clickEditPost() {

        title = ti_title.getText().toString();
        description = ti_description.getText().toString();



        if (!title.isEmpty()) {
            if (imageFile != null) {
                saveImageProfile(imageFile);
            } else if (photoFile != null) {
                saveImageProfile(photoFile);
            }
        } else {
            Post post = new Post();
            post.setTitle(title);
            post.setDescription(description);
            post.setIdUser(authProvider.getUid());
            updatePost(post);
            Toast.makeText(EditPostActivity.this, "Debe rellenar los campos", Toast.LENGTH_LONG).show();
        }


    }


    private void saveImageProfile(File imageFile) {
        alertDialog.show();

        imageProvider.save(EditPostActivity.this, imageFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                if (taskSnapshot.getMetadata() != null) {

                    if (taskSnapshot.getMetadata().getReference() != null) {

                        Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String urlPost = uri.toString();

                                Post post = new Post();
                                post.setTitle(title);
                                post.setImagePost(urlPost);
                                post.setId(extraIdPost);
                                updatePost(post);

                            }
                        });
                    }

                } else {
                    alertDialog.dismiss();
                    Toast.makeText(EditPostActivity.this, "No se pudo actualizar la imagen", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    private void openGallery(int request_code) {

        Intent intentGallery = new Intent(Intent.ACTION_GET_CONTENT);
        intentGallery.setType("image/*");
        startActivityForResult(intentGallery, request_code);

    }


    private void updatePost(Post post) {
        if (alertDialog.isShowing()) {

            alertDialog.show();
        }
        alertDialog.show();
        postProvider.update(post).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                alertDialog.dismiss();
                if (task.isSuccessful()) {
                    Toast.makeText(EditPostActivity.this, "Publicación actualizada", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EditPostActivity.this, "No se ha podido actualizar la publicación", Toast.LENGTH_SHORT).show();

                }

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Seleccionar desde la galería


        if (requestCode == GALLERY_REQUEST_CODE_PROFILE && resultCode == RESULT_OK) {
            try {
                photoFile = null;

                imageFile = FileUtil.from(this, data.getData());
                iv_post.setImageBitmap(BitmapFactory.decodeFile(imageFile.getAbsolutePath()));
            } catch (Exception e) {
                Log.d("ERROR", "Se ha producido un error" + e.getMessage());
                Toast.makeText(this, "Se ha producido un error" + e.getMessage(), Toast.LENGTH_LONG).show();

            }
        }


        /**
         * SELECCION DE FOTOGRAFIA
         */
        if (requestCode == PHOTO_REQUEST_CODE_PROFILE && resultCode == RESULT_OK) {
            imageFile = null;
            photoFile = new File(absolutePhotoPath);
            Picasso.with(EditPostActivity.this).load(photoPath).into(iv_post);
        }
    }

    private void getPost() {
        postProvider.getPostById(extraIdPost).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("title")) {
                        title = documentSnapshot.getString("title");
                        ti_title.setText(title);

                    }

                    if (documentSnapshot.contains("description")) {
                        description = documentSnapshot.getString("description");
                        ti_description.setText(description);

                    }

                    if (documentSnapshot.contains("imagePost")) {
                        imagePost = documentSnapshot.getString("imagePost");

                        Picasso.with(EditPostActivity.this).load(imagePost).into(iv_post);

                    }


                }
            }
        });
    }
}
