package com.example.synapse.screen.carer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.synapse.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

public class CarerHome extends AppCompatActivity {


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carer_home);

        // set bottomNavigationView to transparent
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));

        showAlertDialog();

    }

    // new registered carer needs to send request to senior user to start push notifications
    private void showAlertDialog(){

        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(CarerHome.this);
        builder.setTitle("Welcome! Thank you for signing up.");
        builder.setMessage("Please send request to your senior loved ones to start sending notification reminders.");

        // open email app if user clicks/taps continue
        builder.setPositiveButton("Continue", (dialog, which) -> {
            startActivity(new Intent(CarerHome.this, SendRequest.class));
            finish();
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }


}