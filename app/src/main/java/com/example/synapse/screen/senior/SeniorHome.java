package com.example.synapse.screen.senior;

import androidx.appcompat.app.AppCompatActivity;
import com.example.synapse.R;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class SeniorHome extends AppCompatActivity {

    // initialize game button
    Button gameBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_senior_home);

        // game button id reference
        gameBtn = findViewById(R.id.gameBtn);

        // onclick listener for game button
        gameBtn.setOnClickListener(view -> {
            // open game dashboard where all the games reside
            Intent intent = new Intent(this, GameDashboard.class);
            startActivity(intent);
        });


    }
}