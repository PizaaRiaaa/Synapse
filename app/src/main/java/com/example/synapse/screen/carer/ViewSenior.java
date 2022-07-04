package com.example.synapse.screen.carer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.synapse.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class ViewSenior extends AppCompatActivity {

    DatabaseReference mUserRef, requestRef, assignedCompanionRef;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    ImageButton ibBack;

    String imageURL,fullName;
    ImageView ivSeniorProfilePic;
    TextView seniorFullName;
    Button btnRequest, btnDecline;
    String currentState = "nothing_happen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_senior);

        // bring back user to SearchSenior screen
        ibBack = findViewById(R.id.ibBack);
        ibBack.setOnClickListener(view -> startActivity(new Intent(ViewSenior.this, SearchSenior.class)));

        // get the clicked user's id
        final String userID = getIntent().getStringExtra( "userKey");
        Toast.makeText(this,"" + userID, Toast.LENGTH_SHORT).show();

        mUserRef = FirebaseDatabase.getInstance().getReference().child("Registered Users").child(userID);
        requestRef = FirebaseDatabase.getInstance().getReference().child("Request");
        assignedCompanionRef = FirebaseDatabase.getInstance().getReference().child("Companion");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        ivSeniorProfilePic = findViewById(R.id.ivRequestSeniorProfilePic);
        seniorFullName = findViewById(R.id.tvSearchSeniorFulName);

        btnRequest = findViewById(R.id.btnSendRequest);
        btnDecline = findViewById(R.id.btnDeclineRequest);

        // send request to senior
        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PerformAction(userID);
            }
        });

        // invoke to display user info
        LoadUser();

        checkUserExistence(userID);
    }

    // prevent display request button if it is alarady clicked/tap
    private void checkUserExistence(String userID){
        assignedCompanionRef.child(mUser.getUid()).child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        })
    }

    // display user info
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

    // send request
    private void PerformAction(String userID){
        if(currentState.equals("nothing_happen")){
            HashMap hashMap = new HashMap();
            hashMap.put("status","pending");
            requestRef.child(mUser.getUid()).child(userID).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        Toast.makeText(ViewSenior.this, "You have send friend request", Toast.LENGTH_SHORT).show();
                        btnDecline.setVisibility(View.GONE);
                        currentState = "I_sent_pending";
                        btnRequest.setText("Cancel request");
                    }else{
                        Toast.makeText(ViewSenior.this, "" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        if(currentState.equals("I_sent_pending") || currentState.equals("I_sent_decline")){
            requestRef.child(mUser.getUid()).child(userID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(ViewSenior.this, "You have cancelled request", Toast.LENGTH_SHORT).show();
                        currentState = "nothing_happen";
                        btnRequest.setText("Send request");
                        btnDecline.setVisibility(View.GONE);
                    }else{
                        Toast.makeText(ViewSenior.this, "" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        if(currentState.equals("sent_pending")){
             requestRef.child(mUser.getUid()).child(userID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                 @Override
                 public void onComplete(@NonNull Task<Void> task) {
                     if(task.isSuccessful()){
                         HashMap hashMap = new HashMap();
                         hashMap.put("status","companion");
                         hashMap.put("fullName",fullName);
                         hashMap.put("imageURL",imageURL);

                         assignedCompanionRef.child(mUser.getUid()).child(userID).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                             @Override
                             public void onComplete(@NonNull Task task) {
                                 if(task.isSuccessful()){
                                     assignedCompanionRef.child(userID).child(mUser.getUid()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                         @Override
                                         public void onComplete(@NonNull Task task) {
                                            Toast.makeText(ViewSenior.this, "You added your senior companion", Toast.LENGTH_SHORT).show();
                                            currentState = "Companion";
                                            btnRequest.setText("Send SMS");
                                            btnDecline.setText("remove as a companion");
                                            btnDecline.setVisibility(View.VISIBLE);
                                         }
                                     });
                                 }

                             }
                         });
                     }
                 }
             });
        }
       // if(currentState.equals("Companion")){
       //     //
       // }
    }
}