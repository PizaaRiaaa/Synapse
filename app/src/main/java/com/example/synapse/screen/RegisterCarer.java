package com.example.synapse.screen;

import androidx.appcompat.app.AppCompatActivity;
import com.example.synapse.R;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

public class RegisterCarer extends AppCompatActivity {

    private ImageButton ibBack;
    private TextView tvAlreadyHaveAccount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_carer);

        // (ImageButton) bring user back to PickRole screen
        ibBack = findViewById(R.id.ibRegisterCarerBack);
        ibBack.setOnClickListener(view -> startActivity(new Intent(RegisterCarer.this,PickRole.class)));

        // (TextView) bring user back to Login screen
        tvAlreadyHaveAccount = findViewById(R.id.tvCarerHaveAccount);
        tvAlreadyHaveAccount.setOnClickListener(view -> startActivity(new Intent(RegisterCarer.this, Login.class)));


    }
}