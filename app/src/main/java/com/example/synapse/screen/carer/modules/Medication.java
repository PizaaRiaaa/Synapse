package com.example.synapse.screen.carer.modules;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;
import com.example.synapse.R;
import com.example.synapse.screen.PickRole;
import com.example.synapse.screen.carer.CarerHome;
import com.example.synapse.screen.carer.RegisterCarer;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Medication extends AppCompatActivity{

    //private EditText etSMS;
    //private AppCompatButton btnSend;
    private DatabaseReference referenceProfile, referenceCompanion, referenceSMS;
    private FirebaseUser mUser;
    private Dialog dialog;
    private AppCompatEditText etDose;
    private int count = 0;
    //private String seniorID;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication);

        FloatingActionButton fabAddMedicine;
        BottomNavigationView bottomNavigationView;
        ImageButton ibBack, btnClose, ibMinus, ibAdd;
        ImageView pill1, pill2, pill3, pill4;
        ShapeableImageView color1, color2, color3, color4, color5, color6;
        TextView tv1,tv2,tv3,tv4,tv5,tv6;

        dialog = new Dialog(Medication.this);
        dialog.setContentView(R.layout.custom_dialog_box_add_medication);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_background2));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        dialog.getWindow().getAttributes().gravity = Gravity.BOTTOM;
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation1;

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        fabAddMedicine = findViewById(R.id.btnAddMedicine);
        btnClose = dialog.findViewById(R.id.btnClose);
        ibMinus = dialog.findViewById(R.id.ibMinus);
        ibAdd = dialog.findViewById(R.id.ibAdd);
        etDose = dialog.findViewById(R.id.etDose);

        pill1 = dialog.findViewById(R.id.ivPill1);
        pill2 = dialog.findViewById(R.id.ivPill2);
        pill3 = dialog.findViewById(R.id.ivPill3);
        pill4 = dialog.findViewById(R.id.ivPill4);

        color1 = dialog.findViewById(R.id.color1);
        color2 = dialog.findViewById(R.id.color2);
        color3 = dialog.findViewById(R.id.color3);
        color4 = dialog.findViewById(R.id.color4);
        color5 = dialog.findViewById(R.id.color5);
        color6 = dialog.findViewById(R.id.color6);

        tv1 = dialog.findViewById(R.id.tvGreen);
        tv2 = dialog.findViewById(R.id.tvRed);
        tv3 = dialog.findViewById(R.id.tvBrown);
        tv4 = dialog.findViewById(R.id.tvPink);
        tv5 = dialog.findViewById(R.id.tvBlue);
        tv6 = dialog.findViewById(R.id.tvWhite);

        // set bottomNavigationView to transparent
        bottomNavigationView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));

        // etSMS = findViewById(R.id.etInputSms);
        // btnSend = findViewById(R.id.btnSend);

        // direct user to CareHome screen
        ibBack = findViewById(R.id.ibBack);
        ibBack.setOnClickListener(v -> {
            startActivity(new Intent(Medication.this, CarerHome.class));
            finish();
        });

        // display dialog box
        fabAddMedicine.setOnClickListener(v -> {
            dialog.show();
        });

        // close the dialog box
        btnClose.setOnClickListener(v -> {
            dialog.dismiss();
        });

        // increment and decrement for number picker
        ibMinus.setOnClickListener(this::decrement);
        ibAdd.setOnClickListener(this::increment);

        // check what shape was clicked
        pill1.setOnClickListener(v -> {
            pill1.setBackground(AppCompatResources.getDrawable(Medication.this, R.drawable.rounded_button_pick_role));
            pill2.setBackground(null);
            pill3.setBackground(null);
            pill4.setBackground(null);
        });
        pill2.setOnClickListener(v -> {
            pill2.setBackground(AppCompatResources.getDrawable(Medication.this, R.drawable.rounded_button_pick_role));
            pill1.setBackground(null);
            pill3.setBackground(null);
            pill4.setBackground(null);
        });
        pill3.setOnClickListener(v -> {
            pill3.setBackground(AppCompatResources.getDrawable(Medication.this, R.drawable.rounded_button_pick_role));
            pill1.setBackground(null);
            pill2.setBackground(null);
            pill4.setBackground(null);
        });
        pill4.setOnClickListener(v -> {
            pill4.setBackground(AppCompatResources.getDrawable(Medication.this, R.drawable.rounded_button_pick_role));
            pill1.setBackground(null);
            pill2.setBackground(null);
            pill3.setBackground(null);
        });


        // check what color was clicked
        color1.setOnClickListener(v -> {
            tv1.setTextColor(getColor(R.color.dark_grey));
            tv2.setTextColor(getColor(R.color.et_stroke));
            tv3.setTextColor(getColor(R.color.et_stroke));
            tv4.setTextColor(getColor(R.color.et_stroke));
            tv5.setTextColor(getColor(R.color.et_stroke));
            tv6.setTextColor(getColor(R.color.et_stroke));
        });
        color2.setOnClickListener(v -> {
            tv2.setTextColor(getColor(R.color.dark_grey));
            tv1.setTextColor(getColor(R.color.et_stroke));
            tv3.setTextColor(getColor(R.color.et_stroke));
            tv4.setTextColor(getColor(R.color.et_stroke));
            tv5.setTextColor(getColor(R.color.et_stroke));
            tv6.setTextColor(getColor(R.color.et_stroke));
        });
        color3.setOnClickListener(v -> {
            tv3.setTextColor(getColor(R.color.dark_grey));
            tv1.setTextColor(getColor(R.color.et_stroke));
            tv2.setTextColor(getColor(R.color.et_stroke));
            tv4.setTextColor(getColor(R.color.et_stroke));
            tv5.setTextColor(getColor(R.color.et_stroke));
            tv6.setTextColor(getColor(R.color.et_stroke));
        });
        color4.setOnClickListener(v -> {
            tv4.setTextColor(getColor(R.color.dark_grey));
            tv1.setTextColor(getColor(R.color.et_stroke));
            tv2.setTextColor(getColor(R.color.et_stroke));
            tv3.setTextColor(getColor(R.color.et_stroke));
            tv5.setTextColor(getColor(R.color.et_stroke));
            tv6.setTextColor(getColor(R.color.et_stroke));
        });
        color5.setOnClickListener(v -> {
            tv5.setTextColor(getColor(R.color.dark_grey));
            tv1.setTextColor(getColor(R.color.et_stroke));
            tv2.setTextColor(getColor(R.color.et_stroke));
            tv3.setTextColor(getColor(R.color.et_stroke));
            tv4.setTextColor(getColor(R.color.et_stroke));
            tv6.setTextColor(getColor(R.color.et_stroke));
        });
        color6.setOnClickListener(v -> {
            tv6.setTextColor(getColor(R.color.dark_grey));
            tv1.setTextColor(getColor(R.color.et_stroke));
            tv2.setTextColor(getColor(R.color.et_stroke));
            tv3.setTextColor(getColor(R.color.et_stroke));
            tv4.setTextColor(getColor(R.color.et_stroke));
            tv5.setTextColor(getColor(R.color.et_stroke));
        });


        // referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
       // referenceCompanion = FirebaseDatabase.getInstance().getReference().child("Companion");
       // referenceSMS = FirebaseDatabase.getInstance().getReference().child("Reminders");
       // mUser = FirebaseAuth.getInstance().getCurrentUser();

       // btnSend.setOnClickListener(v -> {
       //     sendSMS();
       // });
    }

    @SuppressLint("SetTextI18n")
    public void increment(View v){
        count++;
        etDose.setText("" + count);
    }

    // if count <= 0, then assign count to 0
    // else decrement
    @SuppressLint("SetTextI18n")
    public void decrement(View v){
        if(count <= 0) count = 0;
        else count--;
        etDose.setText("" + count);
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