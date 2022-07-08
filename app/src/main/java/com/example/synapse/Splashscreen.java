package com.example.synapse;

import androidx.annotation.NonNull;
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
import com.example.synapse.screen.Onboarding;
import com.example.synapse.screen.carer.CarerHome;
import com.example.synapse.screen.carer.SendRequest;
import com.example.synapse.screen.senior.SeniorHome;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

@SuppressLint("CustomSplashScreen")
public class Splashscreen extends AppCompatActivity {

    // firebase reference
    private FirebaseAuth mAuth;
    private DatabaseReference referenceUser, referenceRequest;
    private String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // display on fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_screen);

        referenceUser = FirebaseDatabase.getInstance().getReference("Registered Users");
        referenceRequest = FirebaseDatabase.getInstance().getReference("Request");
        mAuth = FirebaseAuth.getInstance();

        // initialize animation variables
        Animation topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        ImageView image = findViewById(R.id.imageView);
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
                }else if(mAuth.getCurrentUser() == null) {  // prevent display on-boarding screen
                    startActivity(new Intent(Splashscreen.this, Login.class));
                    finish();
                }
            }
        }, 2000); // splash screen duration
    }

    // check if User is already logged in, then direct to their respective home screen
    @Override
    protected void onStart(){
        super.onStart();

        if (mAuth.getCurrentUser() != null) {
            referenceUser.child(Objects.requireNonNull(mAuth.getUid())).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    // check if current user is senior, carer or admin
                    userType = snapshot.child("userType").getValue().toString();

                    if(userType.equals("Senior")){
                        Toast.makeText(Splashscreen.this, "Already Logged In!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Splashscreen.this, SeniorHome.class));
                        finish();

                    }else if(userType.equals("Carer")) {

                        referenceRequest.child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                               if(snapshot.exists()){
                                   Toast.makeText(Splashscreen.this, "Already Logged In!", Toast.LENGTH_SHORT).show();
                                   startActivity(new Intent(Splashscreen.this, CarerHome.class));
                                   finish();
                               }else{
                                   startActivity(new Intent(Splashscreen.this, SendRequest.class));
                                   finish();
                               }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(Splashscreen.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
 }

