package com.example.synapse.screen;

import androidx.appcompat.app.AppCompatActivity;
import com.example.synapse.R;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView tvFullName, tvEmail, tvPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
}