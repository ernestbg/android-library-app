package com.example.proyectoappredsocial.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyectoappredsocial.R;
import com.example.proyectoappredsocial.models.User;
import com.example.proyectoappredsocial.providers.AuthProvider;
import com.example.proyectoappredsocial.providers.UsersProvider;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dmax.dialog.SpotsDialog;

public class RegisterActivity extends AppCompatActivity {

    ImageView iv_arrowBack;

    TextInputEditText ti_name;
    TextInputEditText ti_email;
    TextInputEditText ti_password;
    TextInputEditText ti_confirmPassword;
    FirebaseAuth.AuthStateListener authListener;

    Button btn_register;

    AuthProvider authProvider;
    UsersProvider usersProvider;

    AlertDialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        iv_arrowBack = findViewById(R.id.iv_arrowBack);

        ti_name = findViewById(R.id.ti_username);
        ti_email = findViewById(R.id.ti_email);
        ti_password = findViewById(R.id.ti_password);
        ti_confirmPassword = findViewById(R.id.ti_confirmPassword);


        authProvider = new AuthProvider();
        usersProvider = new UsersProvider();

        alertDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere un momento...")
                .setCancelable(false)
                .build();


        btn_register = findViewById(R.id.btn_register);

        btn_register.setOnClickListener(view -> {

            register();

        });

        iv_arrowBack.setOnClickListener(view -> finish());





    }

    private void register() {
        String name = ti_name.getText().toString();
        String email = ti_email.getText().toString();
        String password = ti_password.getText().toString();
        String confirmPassword = ti_confirmPassword.getText().toString();

        if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty() && !confirmPassword.isEmpty()) {

            if (isEmailValid(email)) {

                if (password.equals(confirmPassword)) {

                    if (password.length() >= 6) {

                        createUser(name, email, password);


                    } else {

                        Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_LONG).show();
                }

            } else {


                Toast.makeText(this, "El email no es valido", Toast.LENGTH_LONG).show();
            }

        } else {

            Toast.makeText(this, "Debes rellenar los campos", Toast.LENGTH_LONG).show();


        }


    }

    public boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void createUser(String name, String email, String password) {
        alertDialog.show();
        authProvider.register(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {


                FirebaseAuth fb_auth = FirebaseAuth.getInstance();
                FirebaseUser firebaseUser = fb_auth.getCurrentUser();
                firebaseUser.sendEmailVerification();

                String id = authProvider.getUid();

                User user = new User();
                user.setId(id);
                user.setUsername(name);
                user.setEmail(email);
                user.setTimeStamp(new Date().getTime());


                usersProvider.create(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        alertDialog.dismiss();
                        if (task.isSuccessful()) {
                            Intent i = new Intent(RegisterActivity.this, HomeActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                        } else {
                            Toast.makeText(RegisterActivity.this, "No se pudo almacenar el usuario registrado en firestore", Toast.LENGTH_LONG).show();
                        }
                    }
                });


                Toast.makeText(RegisterActivity.this, "Usuario registrado correctamente", Toast.LENGTH_LONG).show();

            } else {

                alertDialog.dismiss();

                Toast.makeText(RegisterActivity.this, "No se pudo registrar el usuario", Toast.LENGTH_LONG).show();
            }

        });

    }


}