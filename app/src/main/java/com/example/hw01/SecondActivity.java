package com.example.hw01;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;
import android.os.Bundle;
import android.view.View;


public class SecondActivity extends AppCompatActivity {
    View fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        fragment = findViewById(R.id.nav_host_fragment);
    }


    @Override
    public boolean onSupportNavigateUp(){
        return Navigation.findNavController(fragment).navigateUp();
    }
}