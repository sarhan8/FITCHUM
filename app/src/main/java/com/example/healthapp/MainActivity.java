package com.example.healthapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.healthapp.fragments.HeartRateFragment;
import com.example.healthapp.fragments.HomeFragment;
import com.example.healthapp.fragments.SleepFragment;
import com.example.healthapp.fragments.StepFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.checkerframework.checker.nullness.qual.NonNull;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Fragment selectorFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        selectorFragment = new SleepFragment();  // Set the HomeFragment as the initial fragment

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.nav_home:
                        selectorFragment = new HomeFragment();
                        break;

                    case R.id.nav_sleep:
                        selectorFragment = new SleepFragment();
                        break;

                    case R.id.heart:
                        selectorFragment = null;
                        startActivity(new Intent(MainActivity.this, HeartActivity.class));
                        break;

                    //case R.id.heart:
                    //selectorFragment = new HeartFragment();
                    //break;

                    case R.id.nav_step:
                        selectorFragment = new StepFragment();
                        break;

                    case R.id.nav_heart:
                        selectorFragment = new HeartRateFragment();
                        break;
                }

                if (selectorFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectorFragment).commit();
                }

                return true;

            }
        });

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectorFragment).commit();
    }
}
