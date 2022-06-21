package com.example.synapse.screen;

import androidx.appcompat.app.AppCompatActivity;
import com.example.synapse.R;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

public class PickRole extends AppCompatActivity {

    public ImageButton ivBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_role);

        ivBack = findViewById(R.id.ibBack);

        ivBack.setOnClickListener(view -> {
            startActivity(new Intent(PickRole.this, Login.class));
        });
    }
}