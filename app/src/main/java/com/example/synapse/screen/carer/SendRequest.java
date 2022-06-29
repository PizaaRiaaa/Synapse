package com.example.synapse.screen.carer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.synapse.R;
import com.example.synapse.screen.util.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SendRequest extends AppCompatActivity {
    FirebaseRecyclerAdapter<User, SendRequestViewHolder>adapter;

    // database reference
    DatabaseReference mUserRef;

    FirebaseAuth mAuth;
    FirebaseUser mUser;

    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_request);


        // retrieve database Registered Users
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Registered Users");
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        LoadUsers("");
    }

    private void LoadUsers(String s) {
        Query query = mUserRef.orderByChild("fullName").startAt(s).endAt(s+"\uf8ff");
        FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>().setQuery(query, User.class).build();
        adapter = new FirebaseRecyclerAdapter<User, SendRequestViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull SendRequestViewHolder holder, int position, @NonNull User model) {
                holder.fullname.setText(model.getFullName());

            }

            @NonNull
            @Override
            public SendRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_send_reqest, parent, false);
                return new SendRequestViewHolder(view);
            }
        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }
}