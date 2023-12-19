package com.nnmo.app_perfume;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class menu_client extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_client);
        bottomNavigationView = findViewById(R.id.bottom_menu_client);
        // Log.d("MenuActivity", "onCreate() called");

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int itemId = menuItem.getItemId();
                if (itemId == R.id.bottom_home_client) {
                    replaceFragment(new HomeClientFragment());
                    return true;
                } else if (itemId == R.id.bottom_favorite_client) {
                    replaceFragment(new FavoriteClientFragment());
                    return true;
                } else if (itemId == R.id.bottom_cart_client) {
                    replaceFragment(new CartClientFragment());
                    return true;
                } else if (itemId == R.id.bottom_user_client) {
                    replaceFragment(new UserClientFragment());
                    return true;
                }

                return false;
            }
        });
        replaceFragment(new HomeClientFragment());
    }

    private void replaceFragment(Fragment fragment) {
        Log.d("FragmentTransaction", "Replacing fragment: " + fragment.getClass().getSimpleName());
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment).commit();
    }
}