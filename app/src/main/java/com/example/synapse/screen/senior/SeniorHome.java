package com.example.synapse.screen.senior;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import com.example.synapse.R;
import com.example.synapse.screen.senior.dashboard.GameDashboard;
import com.example.synapse.screen.senior.dashboard.MedicationDashboard;
import com.example.synapse.screen.util.ReadWriteUserDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;
import java.util.HashMap;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class SeniorHome extends AppCompatActivity {

    private static final String TAG = "";
    private DatabaseReference referenceProfile;
    private FirebaseUser mUser;
    private ImageView ivProfilePic;
    private TextView tvSeniorName;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_senior_home);

        MaterialCardView medicationBtn = findViewById(R.id.cardMedication);
        MaterialCardView gameBtn = findViewById(R.id.cardGames);
        AppCompatButton btnSearch = findViewById(R.id.searchBtn);
        TextClock currentTime = findViewById(R.id.tcTime);
        ivProfilePic = findViewById(R.id.ivSeniorProfilePic);
        tvSeniorName = findViewById(R.id.tvSeniorName);

        referenceProfile = FirebaseDatabase.getInstance().getReference("Users");
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        String userID = mUser.getUid();

        FirebaseMessaging.getInstance().subscribeToTopic("hello");

        // direct user to medication dashboard
        medicationBtn.setOnClickListener(view -> startActivity(new Intent(SeniorHome.this, MedicationDashboard.class)));

        // direct user to games dashboard
        gameBtn.setOnClickListener(view -> startActivity(new Intent(SeniorHome.this, GameDashboard.class)));

        // direct user to search people dashboard
        btnSearch.setOnClickListener(view -> startActivity(new Intent(SeniorHome.this, SearchPeople.class)));

        // display current time
        currentTime.setFormat12Hour("hh:mm a");

        // Show status bar
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        showUserProfile(userID);

    }

    // retrieve and update token
    @Override
    public void onStart() {
        super.onStart();
        HashMap hashMap = new HashMap();
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        // Get new FCM registration token
                        token = task.getResult();
                        hashMap.put("token", token);
                        referenceProfile.child(mUser.getUid()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(SeniorHome.this, "success",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        // Log and toast
                        String msg = token;
                        Log.d("Token:", msg);
                    }
                });
    }

    public void showUserProfile(String firebaseUser){
        referenceProfile.child(firebaseUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    ReadWriteUserDetails userProfile = snapshot.getValue(ReadWriteUserDetails.class);
                    if(userProfile != null){
                        String name = userProfile.fullName;
                        int firstName = name.indexOf(" ");
                        tvSeniorName.setText("Hi, " + name.substring(0, firstName ));

                        // display carer profile pic
                        Uri uri = mUser.getPhotoUrl();
                        Picasso.get()
                                .load(uri)
                                .transform(new CropCircleTransformation())
                                .into(ivProfilePic);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SeniorHome.this, "Something went wrong! Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}