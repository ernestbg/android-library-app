package com.example.proyectoappredsocial.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyectoappredsocial.R;
import com.example.proyectoappredsocial.adapters.MyPostAdapter;
import com.example.proyectoappredsocial.models.User;
import com.example.proyectoappredsocial.providers.AuthProvider;
import com.example.proyectoappredsocial.providers.BookReviewsProvider;
import com.example.proyectoappredsocial.providers.BooksProvider;
import com.example.proyectoappredsocial.providers.CommentsProvider;
import com.example.proyectoappredsocial.providers.ImageProvider;
import com.example.proyectoappredsocial.providers.LikesProvider;
import com.example.proyectoappredsocial.providers.PostProvider;
import com.example.proyectoappredsocial.providers.ReadsProvider;
import com.example.proyectoappredsocial.providers.UsersProvider;
import com.example.proyectoappredsocial.utils.FileUtil;
import com.example.proyectoappredsocial.utils.ViewedMessageHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class EditProfileActivity extends AppCompatActivity {
    ImageView imageViewArrowBack;
    CircleImageView civ_profile;
    TextInputEditText ti_userName;
    TextInputEditText ti_aboutMe;
    TextView tv_deleteAccount;


    private final int GALLERY_REQUEST_CODE_PROFILE = 2;
    private final int PHOTO_REQUEST_CODE_PROFILE = 4;

    Button btn_editProfile;


    AuthProvider authProvider;
    ImageProvider imageProvider;
    UsersProvider usersProvider;

    String userName;
    String aboutMe;
    String imageProfile;


    AlertDialog alertDialog;
    AlertDialog.Builder ad_builderSelector;
    CharSequence options[];

    File imageFile;

    String absolutePhotoPath;
    String photoPath;
    File photoFile;

    PostProvider postProvider;
    BooksProvider booksProvider;
    BookReviewsProvider bookReviewsProvider;
    CommentsProvider commentsProvider;
    ReadsProvider readsProvider;
    LikesProvider likesProvider;

    Button btn_deleteAccount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        usersProvider = new UsersProvider();
        authProvider = new AuthProvider();
        postProvider = new PostProvider();
        booksProvider = new BooksProvider();
        bookReviewsProvider = new BookReviewsProvider();
        commentsProvider = new CommentsProvider();
        readsProvider = new ReadsProvider();
        likesProvider = new LikesProvider();
        imageProvider = new ImageProvider();



        imageViewArrowBack = findViewById(R.id.imageView_arrow_back);
        civ_profile = findViewById(R.id.civ_profile);
        ti_userName = findViewById(R.id.ti_username);
        tv_deleteAccount = findViewById(R.id.tv_deleteAccount);
        btn_editProfile = findViewById(R.id.btn_editProfile);

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

        tv_deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialogInputPassword();
            }
        });

        ad_builderSelector = new AlertDialog.Builder(this);
        ad_builderSelector.setTitle("Selecciona una opción");

        options = new CharSequence[]{"Elegir imagen de la galería", "Tomar foto"};


        civ_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectOptionImage(2);
            }
        });

        btn_editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickEditProfile();
            }
        });

        getUser();


    }

    @Override
    protected void onStart() {
        super.onStart();
        ViewedMessageHelper.updateOnline(true, EditProfileActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false, EditProfileActivity.this);
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
            if (ActivityCompat.checkSelfPermission(EditProfileActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                goToCamera(requestCode);
            } else {
                ActivityCompat.requestPermissions(EditProfileActivity.this, new String[]{Manifest.permission.CAMERA}, requestCode);
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
            Uri photoUri = FileProvider.getUriForFile(EditProfileActivity.this, "com.example.proyectoappredsocial", photoFile);
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

    private void clickEditProfile() {

        userName = ti_userName.getText().toString();


        if (!userName.isEmpty()) {


            if (imageFile != null) {
                saveImageProfile(imageFile);
            } else if (photoFile != null) {
                saveImageProfile(photoFile);
            }
        } else {
            User user = new User();
            user.setUsername(userName);
            user.setId(authProvider.getUid());
            updateUserData(user);
            Toast.makeText(EditProfileActivity.this, "Debe rellenar los campos", Toast.LENGTH_LONG).show();
        }


    }

    private void saveImageProfile(File imageFile) {
        alertDialog.show();

        imageProvider.save(EditProfileActivity.this, imageFile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                if (taskSnapshot.getMetadata() != null) {

                    if (taskSnapshot.getMetadata().getReference() != null) {

                        Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String urlProfile = uri.toString();

                                User user = new User();
                                user.setUsername(userName);
                                user.setImageProfile(urlProfile);
                                user.setId(authProvider.getUid());
                                updateUserData(user);

                            }
                        });
                    }

                } else {
                    alertDialog.dismiss();
                    Toast.makeText(EditProfileActivity.this, "No se pudo actualizar la imagen", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    private void openGallery(int request_code) {

        Intent intentGallery = new Intent(Intent.ACTION_GET_CONTENT);
        intentGallery.setType("image/*");
        startActivityForResult(intentGallery, request_code);

    }


    private void updateUserData(User user) {
        if (alertDialog.isShowing()) {

            alertDialog.show();
        }
        alertDialog.show();
        usersProvider.update(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                alertDialog.dismiss();
                if (task.isSuccessful()) {
                    Toast.makeText(EditProfileActivity.this, "Usuario actualizado", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EditProfileActivity.this, "No se ha podido actualizar el usuario", Toast.LENGTH_SHORT).show();

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
                civ_profile.setImageBitmap(BitmapFactory.decodeFile(imageFile.getAbsolutePath()));
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
            Picasso.with(EditProfileActivity.this).load(photoPath).into(civ_profile);
        }
    }

    private void getUser() {
        usersProvider.getUserTask(authProvider.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    if (documentSnapshot.contains("username")) {
                        userName = documentSnapshot.getString("username");
                        ti_userName.setText(userName);

                    }

                    if (documentSnapshot.contains("imageProfile")) {
                        imageProfile = documentSnapshot.getString("imageProfile");

                        Picasso.with(EditProfileActivity.this).load(imageProfile).into(civ_profile);

                    }


                }
            }
        });
    }

    private void deleteAccount(String password) {

        deleteAllUserImages();
        deleteAllUserBookReviews();
        deleteAllUserComments();
        deleteAllUserBooks();
        deleteAllUserLikes();
        deleteAllUserReads();
        deleteAllUserPost();
        deleteAllUserInformation();


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        for (int i = 0; i < user.getProviderData().size(); i++) {

            if (user.getProviderData().get(i).getProviderId().equals("password")) {
                AuthCredential emailAuthCredential = EmailAuthProvider
                        .getCredential(user.getEmail(), password);
                user.reauthenticate(emailAuthCredential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                user.delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d("erny", "User account deleted.");
                                                }
                                            }
                                        });

                            }
                        });

            } else if (user.getProviderData().get(i).getProviderId().equals("google.com")) {
                AuthCredential googleAuthCredential = GoogleAuthProvider
                        .getCredential(user.getEmail(), password);

                user.reauthenticate(googleAuthCredential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                user.delete()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d("erny", "User account deleted.");
                                                }
                                            }
                                        });

                            }
                        });

            }

        }


    }

    private void deleteAllUserInformation() {


        usersProvider.getUserQuery(authProvider.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (int i = 0; i < queryDocumentSnapshots.size(); i++) {

                    String idUser = queryDocumentSnapshots.getDocuments().get(i).getId();

                    usersProvider.delete(idUser);


                }

            }
        });
    }


    private void deleteAllUserBooks() {


        booksProvider.getBooksByUserQuery(authProvider.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (int i = 0; i < queryDocumentSnapshots.size(); i++) {

                    String idBook = queryDocumentSnapshots.getDocuments().get(i).getId();


                    booksProvider.delete(idBook);


                }

            }
        });
    }


    private void deleteAllUserPost() {


        postProvider.getPostByUserQuery(authProvider.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (int i = 0; i < queryDocumentSnapshots.size(); i++) {

                    String idPost = queryDocumentSnapshots.getDocuments().get(i).getId();
                    Log.i("erny", idPost);

                    postProvider.delete(idPost);


                }

            }
        });
    }

    private void deleteAllUserBookReviews() {


        bookReviewsProvider.getBookReviewsByUser(authProvider.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (int i = 0; i < queryDocumentSnapshots.size(); i++) {

                    String idBookReview = queryDocumentSnapshots.getDocuments().get(i).getId();

                    bookReviewsProvider.delete(idBookReview);

                }

            }
        });
    }

    private void deleteAllUserComments() {


        commentsProvider.getCommentsByUserQuery(authProvider.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (int i = 0; i < queryDocumentSnapshots.size(); i++) {

                    String idComment = queryDocumentSnapshots.getDocuments().get(i).getId();


                    commentsProvider.delete(idComment);


                }

            }
        });
    }

    private void deleteAllUserLikes() {


        likesProvider.getLikeByUser(authProvider.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (int i = 0; i < queryDocumentSnapshots.size(); i++) {

                    String idLike = queryDocumentSnapshots.getDocuments().get(i).getId();


                    likesProvider.delete(idLike);


                }

            }
        });
    }


    private void deleteAllUserReads() {


        readsProvider.getReadByUser(authProvider.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (int i = 0; i < queryDocumentSnapshots.size(); i++) {

                    String idRead = queryDocumentSnapshots.getDocuments().get(i).getId();


                    readsProvider.delete(idRead);


                }

            }
        });
    }

    private void deleteAllUserImages() {

        postProvider.getPostByUserQuery(authProvider.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {


                for (int i = 0; i < queryDocumentSnapshots.size(); i++) {

                    String imageUrl = queryDocumentSnapshots.getDocuments().get(i).getString("imagePost");
                    imageProvider.getStorage().getStorage().getReferenceFromUrl(imageUrl).delete();

                }
            }
        });

        usersProvider.getUserTask(authProvider.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {

                    if (documentSnapshot.contains("imageProfile")) {

                        if(!documentSnapshot.getString("imageProfile").isEmpty()){

                            String imageUrl = documentSnapshot.getString("imageProfile");

                            imageProvider.getStorage().getStorage().getReferenceFromUrl(imageUrl).delete();
                        }



                    }
                }

            }
        });


    }


    private void showAlertDialogInputPassword() {
        androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(EditProfileActivity.this);
        alertDialogBuilder.setTitle("Eliminar cuenta");
        alertDialogBuilder.setMessage("Ingresa tu contraseña para eliminar tu cuenta");
        EditText editText = new EditText(EditProfileActivity.this);
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        editText.setHint("Contraseña");
        alertDialogBuilder.setView(editText);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        layoutParams.setMargins(30, 0, 30, 30);
        editText.setLayoutParams(layoutParams);

        RelativeLayout relativeLayout = new RelativeLayout(EditProfileActivity.this);
        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        relativeLayout.setLayoutParams(layoutParams1);
        relativeLayout.addView(editText);
        alertDialogBuilder.setView(relativeLayout);


        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String password = editText.getText().toString();
                if (!password.isEmpty()) {


                    deleteAccount(password);

                } else {
                    Toast.makeText(EditProfileActivity.this, "Debe ingresar su contraseña", Toast.LENGTH_LONG).show();

                }
            }
        });
        alertDialogBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        alertDialogBuilder.show();
    }
}







