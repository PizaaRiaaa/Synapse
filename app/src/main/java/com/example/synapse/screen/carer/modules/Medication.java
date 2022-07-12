package com.example.synapse.screen.carer.modules;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import com.example.synapse.R;
import com.example.synapse.screen.carer.CarerHome;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import java.util.HashMap;

public class Medication extends AppCompatActivity {

    //private EditText etSMS;
    //private AppCompatButton btnSend;
    private DatabaseReference referenceProfile, referenceCompanion, referenceSMS;
    private FirebaseUser mUser;
    //private String seniorID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication);

        // set bottomNavigationView to transparent
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));

       // etSMS = findViewById(R.id.etInputSms);
       // btnSend = findViewById(R.id.btnSend);

        // direct user to CareHome screen
        ImageButton ibBack = findViewById(R.id.ibBack);
        ibBack.setOnClickListener(v -> {
            startActivity(new Intent(Medication.this, CarerHome.class));
            finish();
        });

       // referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
       // referenceCompanion = FirebaseDatabase.getInstance().getReference().child("Companion");
       // referenceSMS = FirebaseDatabase.getInstance().getReference().child("Reminders");
       // mUser = FirebaseAuth.getInstance().getCurrentUser();

       // btnSend.setOnClickListener(v -> {
       //     sendSMS();
       // });
    }

   // private void sendSMS() {
   //     String sms = etSMS.getText().toString();
   //     if(sms.isEmpty()){
   //         Toast.makeText(Medication.this, "Please write something", Toast.LENGTH_SHORT).show();
   //     }else{
   //         HashMap hashMap = new HashMap();
   //         hashMap.put("sms",sms);
   //         hashMap.put("status","unseen");
   //         hashMap.put("userID",mUser.getUid());

   //         referenceCompanion.child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
   //             @Override
   //             public void onDataChange(@NonNull DataSnapshot snapshot) {
   //                if(snapshot.exists()){
   //                    for(DataSnapshot ds : snapshot.getChildren()){
   //                       seniorID = ds.getKey();
   //                       assert  seniorID != null;

   //                       referenceSMS.child(seniorID).child(mUser.getUid()).push().updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
   //                           @Override
   //                           public void onComplete(@NonNull Task task) {
   //                               if(task.isSuccessful()){
   //                                     referenceSMS.child(mUser.getUid()).child(seniorID).push().updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
   //                                         @Override
   //                                         public void onComplete(@NonNull Task task) {
   //                                             if(task.isSuccessful()){
   //                                                 etSMS.setText(null);
   //                                                 Toast.makeText(Medication.this, "You have successfully send the message", Toast.LENGTH_SHORT).show();
   //                                             }
   //                                         }
   //                                     });
   //                               }
   //                           }
   //                       });
   //                    }
   //                }
   //             }
   //             @Override
   //             public void onCancelled(@NonNull DatabaseError error) {
   //                 Toast.makeText(Medication.this, "Something went wrong! Please try again.", Toast.LENGTH_SHORT).show();
   //             }
   //         });
   //     }
   // }
}