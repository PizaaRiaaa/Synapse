package com.example.synapse.screen.carer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.synapse.R;
import com.example.synapse.screen.util.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import java.util.Locale;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class SearchSenior extends AppCompatActivity {

    private DatabaseReference mUserRef;
    private FirebaseUser mUser;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_senior);

        // retrieve database Registered Users
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Register Users");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        // set layout for recyclerview
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // set bottomNavigationView to transparent
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));

        // search senior user
        SearchView searchview = (SearchView)findViewById(R.id.search_field);
        searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                // TODO BE ABLE TO SEARCH WITH LOWERCASE AND UPPERCASE
                LoadUsers(newText);
                return false;
            }
        });

        // invoke to display all senior users
        LoadUsers("");

  }

    // display all senior users in find senior recycle view
    private void LoadUsers(String s){
       Query query = mUserRef.orderByChild("fullName").startAt(s).endAt(s+"\uf8ff");
       FirebaseRecyclerOptions<User> options = new FirebaseRecyclerOptions.Builder<User>().setQuery(query, User.class).build();

        // TODO WE NEED DISPLAY ONLY SENIOR USERS IN RECYCLER VIEW

        FirebaseRecyclerAdapter<User, SendRequestViewHolder> adapter = new FirebaseRecyclerAdapter<User, SendRequestViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull SendRequestViewHolder holder, int position, @NonNull User model) {

                // prevent current login user to display in recycle view
                if (!mUser.getUid().equals(getRef(position).getKey().toString())) {

                    Uri uri = mUser.getPhotoUrl();
                    //ImageView setImagerURI() should not be used with regular URIs. so we are using picasso
                  //  Picasso.get()
                  //          .load(uri)
                  //          .transform(new CropCircleTransformation())
                  //          .into(ivProfilePic);

                    Picasso.get()
                           .load(model.getProfileImage())
                           .into(holder.profileImage);
                    holder.fullname.setText(model.getFullName());
                } else {
                    holder.itemView.setVisibility(View.GONE);
                    holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                }
            }

            @NonNull
            @Override
            public SendRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_send_request, parent, false);
                return new SendRequestViewHolder(view);
            }
        };
       adapter.startListening();
       recyclerView.setAdapter(adapter);
    }

    public static String capitalizeString(String str) {
        String retStr = str;
        try { // We can face index out of bound exception if the string is null
            retStr = str.substring(0, 1).toUpperCase() + str.substring(1);
        }catch (Exception ignored){}
        return retStr;
    }
}