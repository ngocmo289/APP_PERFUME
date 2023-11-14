package com.nnmo.app_perfume;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class menu extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        bottomNavigationView = findViewById(R.id.bottom_menu);
        Log.d("MenuActivity", "onCreate() called");

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int itemId = menuItem.getItemId();
                if (itemId == R.id.bottom_home) {
                    replaceFragment(new HomeFragment());
                    return true;
                } else if (itemId == R.id.bottom_manage) {
                    replaceFragment(new ManageFragment());
                    return true;
                } else if (itemId == R.id.bottom_statistic) {
                    replaceFragment(new StatisticFragment());
                    return true;
                } else if (itemId == R.id.bottom_user) {
                    replaceFragment(new UserFragment());
                    return true;
                }

                return false;
            }
        });
        replaceFragment(new HomeFragment());
    }

    private void replaceFragment(Fragment fragment) {
        Log.d("FragmentTransaction", "Replacing fragment: " + fragment.getClass().getSimpleName());
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, fragment).commit();
    }


}