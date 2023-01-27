package com.example.proyectoappredsocial.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.proyectoappredsocial.R;
import com.example.proyectoappredsocial.fragments.ChallengeFragment;
import com.example.proyectoappredsocial.fragments.ChatFragment;
import com.example.proyectoappredsocial.fragments.FiltersFragment;
import com.example.proyectoappredsocial.fragments.HomeFragment;
import com.example.proyectoappredsocial.fragments.MyBooksFragment;
import com.example.proyectoappredsocial.fragments.ProfileFragment;
import com.example.proyectoappredsocial.providers.AuthProvider;
import com.example.proyectoappredsocial.providers.TokenProvider;
import com.example.proyectoappredsocial.providers.UsersProvider;
import com.example.proyectoappredsocial.utils.ViewedMessageHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigation;


    AuthProvider authProvider;
    TokenProvider tokenProvider;
    UsersProvider usersProvider;

    Fragment fragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if (savedInstanceState != null) {
            //Restore the fragment's instance
            fragment = getSupportFragmentManager().getFragment(savedInstanceState, "ChallengeFragment");

        }


        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);


        tokenProvider = new TokenProvider();
        authProvider = new AuthProvider();
        usersProvider = new UsersProvider();

        openFragment(new HomeFragment());
        createToken();


    }


    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {

                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.item_home:
                            openFragment(new HomeFragment());
                            return true;
                        case R.id.item_myBooks:
                            openFragment(new MyBooksFragment());
                            return true;
                        case R.id.item_chat:
                            openFragment(new ChatFragment());
                            return true;
                        case R.id.item_profile:
                            openFragment(new ProfileFragment());
                            return true;


                    }
                    return false;
                }
            };

    private void createToken() {

        tokenProvider.create(authProvider.getUid());
    }

    @Override
    protected void onStart() {
        super.onStart();
        ViewedMessageHelper.updateOnline(true,HomeActivity.this);


    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false,HomeActivity.this);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null && resultCode == RESULT_OK) {

            String scanIsbn = result.getContents();

            Intent intent = new Intent(HomeActivity.this, BookScanResultActivity.class);
            intent.putExtra("isbn", scanIsbn);
            startActivity(intent);


        } else {


            onBackPressed();

        }


    }


    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        Intent b = new Intent(HomeActivity.this, HomeActivity.class);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(b);


    }


}
