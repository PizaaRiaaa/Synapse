package com.example.synapse.screen.carer.modules;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.example.synapse.R;
import com.example.synapse.screen.carer.CarerHome;
import com.example.synapse.screen.util.ReadWriteUserDetails;
import com.example.synapse.screen.util.notifications.AlertReceiver;
import com.example.synapse.screen.util.MedicationViewHolder;
import com.example.synapse.screen.util.ReadWriteMedication;
import com.example.synapse.screen.util.TimePickerFragment;
import com.example.synapse.screen.util.notifications.FcmNotificationsSender;
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
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import org.aviran.cookiebar2.CookieBar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class Medication extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    private DatabaseReference
            referenceProfile,
            referenceCompanion,
            referenceRequest,
            referenceReminders;

    private FirebaseUser mUser;
    private String seniorID;
    private int count = 0;
    private String token;

    Intent intent;
    RequestQueue requestQueue;
    private final Calendar calendar = Calendar.getInstance();

    private AppCompatButton btnWeek;
    private AppCompatButton btnMonth;
    private AppCompatButton btnYear;

    private Dialog dialog;
    private TextView tvTime, etName;
    RecyclerView recyclerView;
    private ImageView pill1, pill2, pill3, pill4;
    private TextView tv1, tv2, tv3, tv4, tv5, tv6, etDose;
    private ImageView profilePic;
    private String imageURL;
    private String pillShape = "", color = "", time = "";
    private boolean isClicked = false;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication);
        referenceProfile = FirebaseDatabase.getInstance().getReference("Users");
        referenceCompanion = FirebaseDatabase.getInstance().getReference().child("Companion");
        referenceReminders = FirebaseDatabase.getInstance().getReference().child("Reminders");
        referenceRequest = FirebaseDatabase.getInstance().getReference().child("Request");
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        requestQueue = Volley.newRequestQueue(Medication.this);

        btnWeek = findViewById(R.id.btnThisWeek);
        btnMonth = findViewById(R.id.btnThisMonth);
        btnYear = findViewById(R.id.btnThisYear);

        FloatingActionButton fabAddMedicine;
        BottomNavigationView bottomNavigationView;
        ImageButton ibBack, btnClose, ibMinus, ibAdd;
        AppCompatImageButton buttonTimePicker;
        AppCompatButton btnAddSchedule;

        dialog = new Dialog(Medication.this);
        dialog.setContentView(R.layout.custom_dialog_box_add_medication);
        dialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_background2));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        dialog.getWindow().getAttributes().gravity = Gravity.BOTTOM;
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation1;

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        fabAddMedicine = findViewById(R.id.btnAddMedicine);
        profilePic = findViewById(R.id.ivCarerProfilePic);
        btnClose = dialog.findViewById(R.id.btnClose);
        ibMinus = dialog.findViewById(R.id.ibMinus);
        ibAdd = dialog.findViewById(R.id.ibAdd);
        etDose = dialog.findViewById(R.id.etDose);
        tvTime = dialog.findViewById(R.id.tvTime);
        etName = dialog.findViewById(R.id.etName);
        buttonTimePicker = dialog.findViewById(R.id.ibTimePicker);
        btnAddSchedule = dialog.findViewById(R.id.btnAddSchedule);

        pill1 = dialog.findViewById(R.id.ivPill1);
        pill2 = dialog.findViewById(R.id.ivPill2);
        pill3 = dialog.findViewById(R.id.ivPill3);
        pill4 = dialog.findViewById(R.id.ivPill4);

        ShapeableImageView color1 = dialog.findViewById(R.id.color1);
        ShapeableImageView color2 = dialog.findViewById(R.id.color2);
        ShapeableImageView color3 = dialog.findViewById(R.id.color3);
        ShapeableImageView color4 = dialog.findViewById(R.id.color4);
        ShapeableImageView color5 = dialog.findViewById(R.id.color5);
        ShapeableImageView color6 = dialog.findViewById(R.id.color6);

        tv1 = dialog.findViewById(R.id.tvGreen);
        tv2 = dialog.findViewById(R.id.tvRed);
        tv3 = dialog.findViewById(R.id.tvBrown);
        tv4 = dialog.findViewById(R.id.tvPink);
        tv5 = dialog.findViewById(R.id.tvBlue);
        tv6 = dialog.findViewById(R.id.tvWhite);

        // call broadcast
        registerReceiver(broadcastReceiver, new IntentFilter("NOTIFY"));

        recyclerView = findViewById(R.id.recyclerview_medication);
        recyclerView.setLayoutManager(new LinearLayoutManager(Medication.this));
        //recyclerView.setLayoutManager(new GridLayoutManager(Medication.this, 2));

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
            startActivity(new Intent(getIntent()));
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
            DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    DialogFragment timePicker = new TimePickerFragment();
                    timePicker.show(getSupportFragmentManager(), "time picker");
                    isClicked = true;
                }
            };
            new DatePickerDialog(Medication.this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        // perform add schedule
        btnAddSchedule.setOnClickListener(v -> {
            // check if carer has already assigned senior in companion node
            referenceCompanion.child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.S)
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String pillName = etName.getText().toString();
                        String pillDose = etDose.getText().toString();

                        if (TextUtils.isEmpty(pillName)) {
                            Toast.makeText(Medication.this, "Please enter the name of the medicine", Toast.LENGTH_SHORT).show();
                        } else if (TextUtils.isEmpty(pillDose)) {
                            Toast.makeText(Medication.this, "Please enter the dose of the medicine", Toast.LENGTH_SHORT).show();
                        } else if (Objects.equals(pillShape, "")) {
                            Toast.makeText(Medication.this, "Please pick the shape the medicine", Toast.LENGTH_SHORT).show();
                        } else if (Objects.equals(color, "")) {
                            Toast.makeText(Medication.this, "Please pick the color the medicine", Toast.LENGTH_SHORT).show();
                        } else if (!isClicked) {
                            Toast.makeText(Medication.this, "Please pick a schedule for the medicine", Toast.LENGTH_SHORT).show();
                        } else {
                            startAlarm(calendar);
                            addSchedule();
                            // clear or refresh inputs in dialog box
                            startActivity(new Intent(getIntent()));
                        }

                    } else {
                        dialog.dismiss();
                        CookieBar.build(Medication.this)
                                .setTitle("Failed to set a medicine")
                                .setMessage("Wait for your senior to accept your request before sending notifications")
                                .setCookiePosition(CookieBar.TOP)
                                .setBackgroundColor(R.color.red_decline_request)
                                .setDuration(6000)
                                .show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(Medication.this, "Something went wrong! Please try again.", Toast.LENGTH_SHORT).show();
                }
            });

        });

        //show senior profile picture
        showCarerProfilePic(mUser.getUid());

        // load recyclerview
        LoadScheduleForMedication();
        


   }

    public void setBtnBackground(AppCompatButton btn_1, AppCompatButton btn_2, AppCompatButton btn_3){
       btn_1.setBackgroundDrawable(getDrawable(R.drawable.rounded_button_medication_clicked));
       btn_1.setTextColor(getResources().getColor(R.color.light_green));

       btn_2.setBackgroundDrawable(getDrawable(R.drawable.rounded_button_medication));
       btn_2.setTextColor(getResources().getColor(R.color.white));

       btn_3.setBackgroundDrawable(getDrawable(R.drawable.rounded_button_medication));
       btn_3.setTextColor(getResources().getColor(R.color.white));
   }


    // decrement and increment for dose input
    @SuppressLint("SetTextI18n")
    public void increment(View v) {
        count++;
        etDose.setText("" + count);
    }

    @SuppressLint("SetTextI18n")
    public void decrement(View v) {
        if (count <= 0) count = 0;
        else count--;
        etDose.setText("" + count);
    }

    // update textview based on user's timepicker input
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        updateTimeText(calendar);
    }

    @SuppressLint("SetTextI18n")
    private void updateTimeText(Calendar c) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM dd yyyy hh:mm a", Locale.ENGLISH);
        tvTime.setText("Alarm set for " + simpleDateFormat.format(calendar.getTime()));
        time = simpleDateFormat.format(calendar.getTime());
    }

    // set the alarm manager and listen for broadcast
    private void startAlarm(Calendar c) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        intent = new Intent(this, AlertReceiver.class);

        PendingIntent pendingIntent;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_MUTABLE);

        } else {
            pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
        }
        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1);
        }

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
    }

    // instantiate broadcast to listen if alarm is currently running so we can send notification to senior
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                referenceCompanion.child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            seniorID = ds.getKey();

                            referenceProfile.child(seniorID).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    ReadWriteUserDetails seniorProfile = snapshot.getValue(ReadWriteUserDetails.class);
                                    token = seniorProfile.getToken();
                                    FcmNotificationsSender notificationsSender = new FcmNotificationsSender(token,
                                            "Medicine Reminder",
                                            "It's time to take your medicine",
                                            Medication.this);
                                    notificationsSender.SendNotifications();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(Medication.this, "Something went wrong! Please try again.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(Medication.this, "Something went wrong! Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    // display all schedules for medication
    private void LoadScheduleForMedication() {
        referenceReminders.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ignored : snapshot.getChildren()) {
                        for (DataSnapshot ds2 : snapshot.getChildren()) {
                            Query query = ds2.getRef();

                            // display all schedule for this current year
                            btnYear.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    setBtnBackground(btnYear,btnMonth,btnWeek);
                                    FirebaseRecyclerOptions<ReadWriteMedication> options = new FirebaseRecyclerOptions.Builder<ReadWriteMedication>().setQuery(query, ReadWriteMedication.class).build();
                                    FirebaseRecyclerAdapter<ReadWriteMedication, MedicationViewHolder> adapter = new FirebaseRecyclerAdapter<ReadWriteMedication, MedicationViewHolder>(options) {
                                        @RequiresApi(api = Build.VERSION_CODES.O)
                                        @Override
                                        protected void onBindViewHolder(@NonNull MedicationViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull ReadWriteMedication model) {
                                            int year = calendar.get(Calendar.YEAR);
                                            String current_year = String.valueOf(year);
                                            String scheduleYear = model.getTime().split(" ")[2];
                                            if(current_year.equals(scheduleYear)){
                                                String pill_shape = model.getShape();
                                                switch (pill_shape) {
                                                    case "Pill1":
                                                        holder.pill_shape.setBackground(getDrawable(R.drawable.ic_pill1));
                                                        break;
                                                    case "Pill2":
                                                        holder.pill_shape.setBackground(getDrawable(R.drawable.ic_pill2));
                                                        break;
                                                    case "Pill3":
                                                        holder.pill_shape.setBackground(getDrawable(R.drawable.ic_pill3));
                                                        break;
                                                    case "Pill4":
                                                        holder.pill_shape.setBackground(getDrawable(R.drawable.ic_pill4));
                                                        break;
                                                }
                                                holder.name.setText(model.getName());
                                                holder.dose.setText(model.getDose() + " times today");

                                            }else{
                                                holder.itemView.setVisibility(View.GONE);
                                                holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                                            }
                                        }
                                        @NonNull
                                        @Override
                                        public MedicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_carer_medication_schedule, parent, false);
                                            return new MedicationViewHolder(view);
                                        }
                                    };
                                    adapter.startListening();
                                    recyclerView.setAdapter(adapter);
                                }
                           });

                            // display all schedule for this current month
                            btnMonth.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    setBtnBackground(btnMonth,btnWeek,btnYear);
                                    FirebaseRecyclerOptions<ReadWriteMedication> options = new FirebaseRecyclerOptions.Builder<ReadWriteMedication>().setQuery(query, ReadWriteMedication.class).build();
                                    FirebaseRecyclerAdapter<ReadWriteMedication, MedicationViewHolder> adapter = new FirebaseRecyclerAdapter<ReadWriteMedication, MedicationViewHolder>(options) {
                                        @RequiresApi(api = Build.VERSION_CODES.O)
                                        @Override
                                        protected void onBindViewHolder(@NonNull MedicationViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull ReadWriteMedication model) {
                                            String current_month = LocalDate.now().getMonth().name().toUpperCase(Locale.ROOT);
                                            String scheduleMonth = model.getTime().split(" ")[0].toUpperCase(Locale.ROOT);

                                            if(current_month.equals(scheduleMonth)){
                                                String pill_shape = model.getShape();
                                                switch (pill_shape) {
                                                    case "Pill1":
                                                        holder.pill_shape.setBackground(getDrawable(R.drawable.ic_pill1));
                                                        break;
                                                    case "Pill2":
                                                        holder.pill_shape.setBackground(getDrawable(R.drawable.ic_pill2));
                                                        break;
                                                    case "Pill3":
                                                        holder.pill_shape.setBackground(getDrawable(R.drawable.ic_pill3));
                                                        break;
                                                    case "Pill4":
                                                        holder.pill_shape.setBackground(getDrawable(R.drawable.ic_pill4));
                                                        break;
                                                }
                                                holder.name.setText(model.getName());
                                                holder.dose.setText(model.getDose() + " times today");

                                            }else{
                                                holder.itemView.setVisibility(View.GONE);
                                                holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                                            }
                                        }
                                        @NonNull
                                        @Override
                                        public MedicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_carer_medication_schedule, parent, false);
                                            return new MedicationViewHolder(view);
                                        }
                                    };
                                    adapter.startListening();
                                    recyclerView.setAdapter(adapter);
                                }
                            });

                            // display all schedule for this current week
                            btnWeek.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    setBtnBackground(btnWeek,btnMonth,btnYear);
                                    FirebaseRecyclerOptions<ReadWriteMedication> options = new FirebaseRecyclerOptions.Builder<ReadWriteMedication>().setQuery(query, ReadWriteMedication.class).build();
                                    FirebaseRecyclerAdapter<ReadWriteMedication, MedicationViewHolder> adapter = new FirebaseRecyclerAdapter<ReadWriteMedication, MedicationViewHolder>(options) {
                                        @RequiresApi(api = Build.VERSION_CODES.O)
                                        @Override
                                        protected void onBindViewHolder(@NonNull MedicationViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull ReadWriteMedication model) {
                                           int current_week = calendar.get(Calendar.WEEK_OF_YEAR);
                                           int year = calendar.get(Calendar.YEAR);
                                           String scheduleYear = model.getTime();
                                           SimpleDateFormat format = new SimpleDateFormat("MMM dd yyyy", Locale.ENGLISH);
                                           try {
                                               calendar.setTime(Objects.requireNonNull(format.parse(scheduleYear)));
                                           } catch (ParseException e) {
                                               e.printStackTrace();
                                           }
                                           int targetWeek = calendar.get(Calendar.WEEK_OF_YEAR);
                                           int targetYear = calendar.get(Calendar.YEAR);

                                           if(current_week == targetWeek && year == targetYear) {
                                               String pill_shape = model.getShape();
                                               switch (pill_shape) {
                                                   case "Pill1":
                                                       holder.pill_shape.setBackground(getDrawable(R.drawable.ic_pill1));
                                                       break;
                                                   case "Pill2":
                                                       holder.pill_shape.setBackground(getDrawable(R.drawable.ic_pill2));
                                                       break;
                                                   case "Pill3":
                                                       holder.pill_shape.setBackground(getDrawable(R.drawable.ic_pill3));
                                                       break;
                                                   case "Pill4":
                                                       holder.pill_shape.setBackground(getDrawable(R.drawable.ic_pill4));
                                                       break;
                                               }
                                               holder.name.setText(model.getName());
                                               holder.dose.setText(model.getDose() + " times today");

                                           }else{
                                               holder.itemView.setVisibility(View.GONE);
                                               holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                                           }
                                        }

                                        @NonNull
                                        @Override
                                        public MedicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_carer_medication_schedule, parent, false);
                                            return new MedicationViewHolder(view);
                                        }
                                    };
                                    adapter.startListening();
                                    recyclerView.setAdapter(adapter);
                                }
                            });


                           FirebaseRecyclerOptions<ReadWriteMedication> options = new FirebaseRecyclerOptions.Builder<ReadWriteMedication>().setQuery(query, ReadWriteMedication.class).build();
                           FirebaseRecyclerAdapter<ReadWriteMedication, MedicationViewHolder> adapter = new FirebaseRecyclerAdapter<ReadWriteMedication, MedicationViewHolder>(options) {
                                @RequiresApi(api = Build.VERSION_CODES.O)
                                @SuppressLint("SetTextI18n")
                                @Override
                                protected void onBindViewHolder(@NonNull MedicationViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull ReadWriteMedication model) {


                                           String pill_shape = model.getShape();
                                           switch (pill_shape) {
                                               case "Pill1":
                                                   holder.pill_shape.setBackground(getDrawable(R.drawable.ic_pill1));
                                                   break;
                                               case "Pill2":
                                                   holder.pill_shape.setBackground(getDrawable(R.drawable.ic_pill2));
                                                   break;
                                               case "Pill3":
                                                   holder.pill_shape.setBackground(getDrawable(R.drawable.ic_pill3));
                                                   break;
                                               case "Pill4":
                                                   holder.pill_shape.setBackground(getDrawable(R.drawable.ic_pill4));
                                                   break;
                                           }
                                           holder.name.setText(model.getName());
                                           holder.dose.setText(model.getDose() + " times today");



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
                                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_carer_medication_schedule, parent, false);
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
                Toast.makeText(Medication.this, "Something went wrong! Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // store schedule for medicine
    private void addSchedule() {

        String med = "Medication";
        HashMap hashMap = new HashMap();

        hashMap.put("Name", etName.getText().toString());
        hashMap.put("Dose", etDose.getText().toString());
        hashMap.put("Time", time);
        hashMap.put("Shape", pillShape);
        hashMap.put("PillColor", color);
        hashMap.put("Type", med);
        hashMap.put("userID", mUser.getUid());

        referenceCompanion.child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        seniorID = ds.getKey();
                        assert seniorID != null;

                        referenceReminders.child(seniorID).child(mUser.getUid()).push().updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if (task.isSuccessful()) {

                                    referenceReminders.child(mUser.getUid()).child(seniorID).push().updateChildren(hashMap).addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            dialog.dismiss();
                                        }

                                        CookieBar.build(Medication.this)
                                                .setTitle("Set Medicine")
                                                .setMessage("You have successfully schedule medicine")
                                                .setIcon(R.drawable.ic_cookie_check)
                                                .setBackgroundColor(R.color.green_send_request)
                                                .setCookiePosition(CookieBar.TOP)
                                                .setDuration(5000)
                                                .show();
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

    // display senior's profile pic
    private void showCarerProfilePic(String firebaseUser){
        // check if carer already send request
        referenceRequest.child(firebaseUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){
                    for(DataSnapshot ds : snapshot.getChildren()){
                        referenceProfile.child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    ReadWriteUserDetails seniorProfile = snapshot.getValue(ReadWriteUserDetails.class);
                                    imageURL = Objects.requireNonNull(snapshot.child("imageURL").getValue()).toString();
                                    Picasso.get()
                                            .load(imageURL)
                                            .fit()
                                            .transform(new CropCircleTransformation())
                                            .into(profilePic);
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(Medication.this, "Something went wrong! Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }else{
                    // check if senior accepted the carer request
                    referenceCompanion.child(firebaseUser).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if(snapshot.exists()){
                                for(DataSnapshot ds : snapshot.getChildren()){
                                    referenceProfile.child(mUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                            if(snapshot.exists()){
                                                ReadWriteUserDetails seniorProfile = snapshot.getValue(ReadWriteUserDetails.class);
                                                imageURL = Objects.requireNonNull(snapshot.child("imageURL").getValue()).toString();
                                                Picasso.get()
                                                        .load(imageURL)
                                                        .fit()
                                                        .transform(new CropCircleTransformation())
                                                        .into(profilePic);
                                            }
                                        }
                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(Medication.this, "Something went wrong! Please try again.", Toast.LENGTH_SHORT).show();
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
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Medication.this, "Something went wrong! Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}