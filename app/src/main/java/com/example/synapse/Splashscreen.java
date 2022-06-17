package com.example.synapse;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.synapse.screen.Login;
import com.example.synapse.screen.MainActivity;
import com.example.synapse.screen.Onboarding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

@SuppressLint("CustomSplashScreen")
public class Splashscreen extends AppCompatActivity {

    // animation variables
    Animation topAnim;
    ImageView image;

    // initialize firebase auth

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_screen);

        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        image = findViewById(R.id.imageView);
        image.setAnimation(topAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                SharedPreferences settings = getSharedPreferences("prefs", 0);
                boolean firstRun = settings.getBoolean("firstRun", false);

                if (!firstRun) // if installed for the first time, then display on-boarding screen
                {
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("firstRun", true);
                    editor.apply();
                    startActivity(new Intent(Splashscreen.this, Onboarding.class));
                    finish();
                } else {  // prevent display on-boarding screen

                    Intent a = new Intent(Splashscreen.this, Login.class);
                    startActivity(a);
                    finish();
                }
            }
        }, 3500); // splash screen duration
    }
}
//else if (mAuth.getCurrentUser() != null) { // if user is currently login, direct to MainActivity
  //      Intent intent = new Intent(Splashscreen.this, MainActivity.class);
   //     startActivity(intent);
    //    finish();