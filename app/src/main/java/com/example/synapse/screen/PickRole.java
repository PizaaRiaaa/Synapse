package com.example.synapse.screen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.synapse.R;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

public class PickRole extends AppCompatActivity {

    public ImageButton ivBack;
    public TextView tvBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_role);


        // (back arrow) bring user back to login screen
        ivBack = findViewById(R.id.ibBack);
        ivBack.setOnClickListener(view -> startActivity(new Intent(PickRole.this, Login.class)));

        // (textview) bring back user to login screen
        tvBack = findViewById(R.id.tvAlreadyHaveAnAccount);
        tvBack.setOnClickListener(view -> startActivity(new Intent(PickRole.this, Login.class)));


        // show status bar
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // transparent status bar
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.mid_grey));
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.mid_grey));
        }
    }
}