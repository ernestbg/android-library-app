package com.example.proyectoappredsocial.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyectoappredsocial.R;
import com.example.proyectoappredsocial.models.User;
import com.example.proyectoappredsocial.providers.AuthProvider;
import com.example.proyectoappredsocial.providers.UsersProvider;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.DocumentSnapshot;

import dmax.dialog.SpotsDialog;

public class LoginActivity extends AppCompatActivity {

    TextView tv_register;


    TextInputEditText ti_email;
    TextInputEditText ti_password;
    Button btn_login;

    AuthProvider auth_provider;
    private GoogleSignInClient googleSignInClient;

    SignInButton btn_google;
    UsersProvider usersProvider;
    AlertDialog alertDialog;


    private static final int REQUEST_CODE_GOOGLE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tv_register = findViewById(R.id.tv_register);


        ti_email = findViewById(R.id.ti_email);
        ti_password = findViewById(R.id.ti_password);


        auth_provider = new AuthProvider();
        usersProvider = new UsersProvider();

        alertDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere un momento...")
                .setCancelable(false)
                .build();


        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);


        btn_login = findViewById(R.id.btn_login);


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        tv_register.setOnClickListener(view -> {
            Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(i);
        });



        btn_google = findViewById(R.id.btn_google);
        btn_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInGoogle();

            }
        });




    }

    @Override
    protected void onStart() {
        super.onStart();

        if(auth_provider.getUserSession() != null){

            Intent i=new Intent(LoginActivity.this, HomeActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);


        }
    }

    private void login() {
        String email = ti_email.getText().toString();
        String password = ti_password.getText().toString();
        alertDialog.show();
        auth_provider.login(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                alertDialog.dismiss();
                if (task.isSuccessful()) {

                    Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(i);

                    Toast.makeText(LoginActivity.this, "Bienvenido", Toast.LENGTH_LONG).show();


                } else {

                    Toast.makeText(LoginActivity.this, "Email o contraseña incorrectos", Toast.LENGTH_LONG).show();


                }
            }
        });

        Log.d("CAMPO", "EMAIL: " + email);
        Log.d("CAMPO", "PASSWORD: " + password);
    }

    private void signInGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, REQUEST_CODE_GOOGLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == REQUEST_CODE_GOOGLE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {

                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("ERROR", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account);

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("ERROR", "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        alertDialog.show();
        auth_provider.googleSignIn(acct).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    String id = auth_provider.getUid();


                    checkUserExist(id);


                    Toast.makeText(LoginActivity.this, "Bienvenido", Toast.LENGTH_LONG).show();

                } else {
                    alertDialog.dismiss();
                    // If sign in fails, display a message to the user.
                    Log.w("ERROR", "signInWithCredential:failure", task.getException());
                    Toast.makeText(LoginActivity.this, "No se pudo iniciar sesión con Google", Toast.LENGTH_LONG).show();

                }
            }

            private void checkUserExist(String id) {

                usersProvider.getUserTask(id).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        if (documentSnapshot.exists()) {
                            alertDialog.dismiss();

                            Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(i);


                        } else {
                            String email = auth_provider.getEmail();
                            User user = new User();

                            user.setEmail(email);
                            user.setId(id);
                            user.setUsername(auth_provider.getDisplayName());



                            usersProvider.create(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    alertDialog.dismiss();
                                    if (task.isSuccessful()) {

                                        Intent i = new Intent(LoginActivity.this, HomeActivity.class);
                                        startActivity(i);
                                    } else {
                                        Toast.makeText(LoginActivity.this, "No se pudo almacenar el usuario en la base de datos", Toast.LENGTH_LONG).show();

                                    }

                                }
                            });
                        }

                    }
                });
            }
        });
    }
}