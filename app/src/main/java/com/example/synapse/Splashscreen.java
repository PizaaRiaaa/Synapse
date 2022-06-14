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

import com.example.synapse.screen.Login;
import com.example.synapse.screen.Onboarding;

@SuppressLint("CustomSplashScreen")
public class Splashscreen extends AppCompatActivity {

    // ANIMATION VARIABLES
    Animation topAnim;
    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_screen);

        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        image = findViewById(R.id.imageView);
        image.setAnimation(topAnim);

        new Handler().postDelayed(new Runnable(){
            @Override
                public void run(){

                SharedPreferences settings=getSharedPreferences("prefs",0);
                boolean firstRun=settings.getBoolean("firstRun",false);

                /* IF INSTALLED FOR THE FIRST TIME
                   DISPLAY ON-BOARDING SCREEN
                */
                if(!firstRun)
                {
                    SharedPreferences.Editor editor=settings.edit();
                    editor.putBoolean("firstRun",true);
                    editor.apply();
                    startActivity(new Intent(Splashscreen.this, Onboarding.class));
                    finish();
                }
                // PREVENT DISPLAY ON-BOARDING SCREEN
                else {

                    Intent a=new Intent(Splashscreen.this, Login.class);
                    startActivity(a);
                    finish();
                }
            }
            // SPLASH SCREEN DURATION
        },3500);
    }
}