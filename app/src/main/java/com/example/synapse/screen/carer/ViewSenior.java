package com.example.synapse.screen.carer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.synapse.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class ViewSenior extends AppCompatActivity {

    DatabaseReference mUserRef;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    ImageButton ibBack;

    String imageURL,fullName;
    ImageView ivSeniorProfilePic;
    TextView seniorFullName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_senior);

        // bring back user to SearchSenior screen
        ibBack = findViewById(R.id.ibBack);
        ibBack.setOnClickListener(view -> startActivity(new Intent(ViewSenior.this, SearchSenior.class)));

        // get the clicked user's id
        String userID = getIntent().getStringExtra( "userKey");
        Toast.makeText(this,"" + userID, Toast.LENGTH_SHORT).show();

        ivSeniorProfilePic = findViewById(R.id.ivRequestSeniorProfilePic);
        seniorFullName = findViewById(R.id.tvSearchSeniorFulName);


        mUserRef = FirebaseDatabase.getInstance().getReference().child("Registered Users").child(userID);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();


        LoadUser();
    }

    private void LoadUser(){
        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    imageURL = snapshot.child("imageURL").getValue().toString();
                    fullName = snapshot.child("fullName").getValue().toString();

                    Picasso.get()
                           .load(imageURL)
                           .fit()
                           .transform(new CropCircleTransformation())
                           .into(ivSeniorProfilePic);

                    seniorFullName.setText(fullName);

                }else{
                    Toast.makeText(ViewSenior.this, "Data not found", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(ViewSenior.this, "" + error.getMessage().toString(), Toast.LENGTH_SHORT).show();

             }
        });
    }
}