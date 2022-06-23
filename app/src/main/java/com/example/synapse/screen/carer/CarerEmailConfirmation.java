package com.example.synapse.screen.carer;

import androidx.appcompat.app.AppCompatActivity;
import com.example.synapse.R;
import com.example.synapse.screen.Login;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class CarerEmailConfirmation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carer_email_confirmation);

        Button btnLogin = findViewById(R.id.btnLoginNow);
        btnLogin.setOnClickListener(view -> startActivity(new Intent(CarerEmailConfirmation.this, Login.class)));

    }
}