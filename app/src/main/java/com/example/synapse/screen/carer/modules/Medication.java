package com.example.synapse.screen.carer.modules;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.synapse.R;
import com.example.synapse.screen.carer.CarerHome;
import com.example.synapse.screen.carer.SearchPeople;
import com.example.synapse.screen.carer.ViewPeople;
import com.example.synapse.screen.util.AlertReceiver;
import com.example.synapse.screen.util.MedicationViewHolder;
import com.example.synapse.screen.util.ReadWriteMedication;
import com.example.synapse.screen.util.ReadWriteUserDetails;
import com.example.synapse.screen.util.SearchSeniorViewHolder;
import com.example.synapse.screen.util.TimePickerFragment;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class Medication extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener{

    private final String MEDICATION = "Medication";
    private DatabaseReference referenceProfile, referenceCompanion, referenceReminders;
    private FirebaseUser mUser;
    private Dialog dialog;
    private TextView tvTime;
    private int count = 0;
    private String seniorID;

    private RecyclerView recyclerView;
    private ImageView pill1, pill2, pill3, pill4;
    private TextView tv1,tv2,tv3,tv4,tv5,tv6, etName, etDose;
    public  String pillShape = "", color = "", time = "";
    private ShapeableImageView color1, color2, color3, color4, color5, color6;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication);

        referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
        referenceCompanion = FirebaseDatabase.getInstance().getReference().child("Companion");
        referenceReminders = FirebaseDatabase.getInstance().getReference().child("Reminders");
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        FloatingActionButton fabAddMedicine;
        BottomNavigationView bottomNavigationView;
        ImageButton ibBack, btnClose, ibMinus, ibAdd;
        AppCompatImageButton buttonTimePicker;
        AppCompatButton btnAddSchedule;

        // set layout for recyclerview
        recyclerView = findViewById(R.id.recyclerview_medication);
        recyclerView.setLayoutManager(new GridLayoutManager(Medication.this,2));

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
        tvTime = dialog.findViewById(R.id.tvTime);
        etName = dialog.findViewById(R.id.etName);
        buttonTimePicker = dialog.findViewById(R.id.ibTimePicker);
        btnAddSchedule = dialog.findViewById(R.id.btnAddSchedule);

        pill1 = dialog.findViewById(R.id.ivPill1); pill2 = dialog.findViewById(R.id.ivPill2);
        pill3 = dialog.findViewById(R.id.ivPill3); pill4 = dialog.findViewById(R.id.ivPill4);

        color1 = dialog.findViewById(R.id.color1); color2 = dialog.findViewById(R.id.color2);
        color3 = dialog.findViewById(R.id.color3); color4 = dialog.findViewById(R.id.color4);
        color5 = dialog.findViewById(R.id.color5); color6 = dialog.findViewById(R.id.color6);

        tv1 = dialog.findViewById(R.id.tvGreen); tv2 = dialog.findViewById(R.id.tvRed);
        tv3 = dialog.findViewById(R.id.tvBrown); tv4 = dialog.findViewById(R.id.tvPink);
        tv5 = dialog.findViewById(R.id.tvBlue); tv6 = dialog.findViewById(R.id.tvWhite);

        // set bottomNavigationView to transparent
        bottomNavigationView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));

        // Show status bar
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

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
            pillShape = "Pill1";
        });

        pill2.setOnClickListener(v -> {
            pill2.setBackground(AppCompatResources.getDrawable(Medication.this, R.drawable.rounded_button_pick_role));
            pill1.setBackground(null);
            pill3.setBackground(null);
            pill4.setBackground(null);
            pillShape = "Pill2";
        });
        pill3.setOnClickListener(v -> {
            pill3.setBackground(AppCompatResources.getDrawable(Medication.this, R.drawable.rounded_button_pick_role));
            pill1.setBackground(null);
            pill2.setBackground(null);
            pill4.setBackground(null);
            pillShape = "Pill3";
        });
        pill4.setOnClickListener(v -> {
            pill4.setBackground(AppCompatResources.getDrawable(Medication.this, R.drawable.rounded_button_pick_role));
            pill1.setBackground(null);
            pill2.setBackground(null);
            pill3.setBackground(null);
            pillShape = "Pill4";
        });

        // check what color was clicked
        color1.setOnClickListener(v -> {
            tv1.setTextColor(getColor(R.color.dark_grey));
            tv2.setTextColor(getColor(R.color.et_stroke));
            tv3.setTextColor(getColor(R.color.et_stroke));
            tv4.setTextColor(getColor(R.color.et_stroke));
            tv5.setTextColor(getColor(R.color.et_stroke));
            tv6.setTextColor(getColor(R.color.et_stroke));
            color = "Green";
        });
        color2.setOnClickListener(v -> {
            tv2.setTextColor(getColor(R.color.dark_grey));
            tv1.setTextColor(getColor(R.color.et_stroke));
            tv3.setTextColor(getColor(R.color.et_stroke));
            tv4.setTextColor(getColor(R.color.et_stroke));
            tv5.setTextColor(getColor(R.color.et_stroke));
            tv6.setTextColor(getColor(R.color.et_stroke));
            color = "Red";
        });
        color3.setOnClickListener(v -> {
            tv3.setTextColor(getColor(R.color.dark_grey));
            tv1.setTextColor(getColor(R.color.et_stroke));
            tv2.setTextColor(getColor(R.color.et_stroke));
            tv4.setTextColor(getColor(R.color.et_stroke));
            tv5.setTextColor(getColor(R.color.et_stroke));
            tv6.setTextColor(getColor(R.color.et_stroke));
            color = "Brown";
        });
        color4.setOnClickListener(v -> {
            tv4.setTextColor(getColor(R.color.dark_grey));
            tv1.setTextColor(getColor(R.color.et_stroke));
            tv2.setTextColor(getColor(R.color.et_stroke));
            tv3.setTextColor(getColor(R.color.et_stroke));
            tv5.setTextColor(getColor(R.color.et_stroke));
            tv6.setTextColor(getColor(R.color.et_stroke));
            color = "Pink";
        });
        color5.setOnClickListener(v -> {
            tv5.setTextColor(getColor(R.color.dark_grey));
            tv1.setTextColor(getColor(R.color.et_stroke));
            tv2.setTextColor(getColor(R.color.et_stroke));
            tv3.setTextColor(getColor(R.color.et_stroke));
            tv4.setTextColor(getColor(R.color.et_stroke));
            tv6.setTextColor(getColor(R.color.et_stroke));
            color = "Blue";
        });
        color6.setOnClickListener(v -> {
            tv6.setTextColor(getColor(R.color.dark_grey));
            tv1.setTextColor(getColor(R.color.et_stroke));
            tv2.setTextColor(getColor(R.color.et_stroke));
            tv3.setTextColor(getColor(R.color.et_stroke));
            tv4.setTextColor(getColor(R.color.et_stroke));
            tv5.setTextColor(getColor(R.color.et_stroke));
            color = "White";
        });

        // display time picker
        buttonTimePicker.setOnClickListener(v -> {
            DialogFragment timePicker = new TimePickerFragment();
            timePicker.show(getSupportFragmentManager(), "time picker");
        });

         btnAddSchedule.setOnClickListener(v -> {
              addSchedule();
         });

         LoadScheduleForMedication();

    }

    @SuppressLint("SetTextI18n")
    public void increment(View v){
        count++;
        etDose.setText("" + count);
    }

    @SuppressLint("SetTextI18n")
    public void decrement(View v){
        if(count <= 0) count = 0;
        else count--;
        etDose.setText("" + count);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY, hourOfDay);
            c.set(Calendar.MINUTE, minute);
            c.set(Calendar.SECOND, 0);
            updateTimeText(c);
            startAlarm(c);
    }

    private void updateTimeText(Calendar c) {
            String timeText = "Alarm set for: ";
            timeText += DateFormat.getTimeInstance(DateFormat.SHORT).format(c.getTime());
            tvTime.setText(timeText);
            time = DateFormat.getTimeInstance(DateFormat.SHORT).format(c.getTime());
    }

    private void startAlarm(Calendar c) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1);
        }
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
    }

    // display all schedules for medication
    private void LoadScheduleForMedication(){
        referenceReminders.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot ds1 : snapshot.getChildren()){
                        for(DataSnapshot ds2 : snapshot.getChildren()){
                            Query query = ds2.getRef();
                            FirebaseRecyclerOptions<ReadWriteMedication> options = new FirebaseRecyclerOptions.Builder<ReadWriteMedication>().setQuery(query, ReadWriteMedication.class).build();
                            FirebaseRecyclerAdapter<ReadWriteMedication, MedicationViewHolder> adapter = new FirebaseRecyclerAdapter<ReadWriteMedication, MedicationViewHolder>(options) {
                                @Override
                                protected void onBindViewHolder(@NonNull MedicationViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull ReadWriteMedication model) {

                                    // display medication reminders
                                    holder.Name.setText(model.getName());
                                    holder.Dose.setText(model.getDose() + " times today");

                                    // // open user's profile and send user's userKey to another activity
                                    // holder.itemView.setOnClickListener(new View.OnClickListener() {
                                    //     @Override
                                    //     public void onClick(View v) {
                                    //         Intent intent = new Intent(SearchPeople.this, ViewPeople.class);
                                    //         intent.putExtra("userKey", getRef(position).getKey());
                                    //         startActivity(intent);
                                    //     }
                                    // });
                                }


                                @NonNull
                                @Override
                                public MedicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_medication_schedule, parent, false);
                                    return new MedicationViewHolder(view);
                                }
                            };
                            adapter.startListening();
                            recyclerView.setAdapter(adapter);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // store schedule for medicine
    private void addSchedule() {

           HashMap hashMap = new HashMap();
           hashMap.put("Name",etName.getText().toString());
           hashMap.put("Dose",etDose.getText().toString());
           hashMap.put("Time",tvTime.getText().toString());
           hashMap.put("Shape",pillShape);
           hashMap.put("PillColor", color);
           hashMap.put("Type", MEDICATION);
           hashMap.put("userID",mUser.getUid());

           referenceCompanion.child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot snapshot) {
                  if(snapshot.exists()){
                      for(DataSnapshot ds : snapshot.getChildren()){
                         seniorID = ds.getKey();
                         assert  seniorID != null;

                         referenceReminders.child(seniorID).child(mUser.getUid()).push().updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                             @Override
                             public void onComplete(@NonNull Task task) {
                                 if(task.isSuccessful()){
                                       referenceReminders.child(mUser.getUid()).child(seniorID).push().updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                           @Override
                                           public void onComplete(@NonNull Task task) {
                                               if(task.isSuccessful()){
                                                   //etSMS.setText(null);
                                                   Toast.makeText(Medication.this, "You have successfully send the message", Toast.LENGTH_SHORT).show();
                                               }
                                           }
                                       });
                                 }
                             }
                         });
                      }
                  }
               }
               @Override
               public void onCancelled(@NonNull DatabaseError error) {
                   Toast.makeText(Medication.this, "Something went wrong! Please try again.", Toast.LENGTH_SHORT).show();
               }
           });
       }
}