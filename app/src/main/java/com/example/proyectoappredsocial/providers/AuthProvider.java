package com.example.proyectoappredsocial.providers;

import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class AuthProvider {


    private FirebaseAuth fb_auth;

    public AuthProvider() {
        fb_auth = FirebaseAuth.getInstance();

    }

    public Task<AuthResult> login(String email, String password) {
        return fb_auth.signInWithEmailAndPassword(email, password);

    }

    public Task<AuthResult> register(String email, String password) {
        return fb_auth.createUserWithEmailAndPassword(email, password);



    }

    public Task<AuthResult> googleSignIn(GoogleSignInAccount googleSignInAccount) {
        AuthCredential credential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);

        return fb_auth.signInWithCredential(credential);

    }

    public String getEmail() {

        if (fb_auth != null) {
            return fb_auth.getCurrentUser().getEmail();
        } else {
            return null;
        }
    }

    public String getUid() {
        if (fb_auth.getCurrentUser() != null) {
            return fb_auth.getCurrentUser().getUid();
        } else {
            return null;

        }


    }

    public FirebaseUser getUserSession() {
        if (fb_auth.getCurrentUser() != null) {
            return fb_auth.getCurrentUser();
        } else {
            return null;

        }


    }

    public String getDisplayName() {
        if (fb_auth.getCurrentUser() != null) {
            return fb_auth.getCurrentUser().getDisplayName();
        } else {
            return null;

        }


    }

    public void logout() {

        if (fb_auth != null){
            fb_auth.signOut();

        }


    }


}
