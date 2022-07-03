package com.example.synapse.screen.carer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.synapse.screen.util.ReadWriteUserDetails;
import com.example.synapse.screen.util.SearchSeniorViewHolder;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.synapse.R;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class SearchSenior extends AppCompatActivity {

    private DatabaseReference mUserRef;
    private FirebaseUser mUser;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_senior);

        // extracting user reference from database "Registered Users"
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Registered Users");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

       // change substring color
       TextView tvSeniorResults = findViewById(R.id.tvSeniorResults);
       // String text = "senior citizen results";
       // SpannableStringBuilder ssb = new SpannableStringBuilder(text);
       // ForegroundColorSpan light_green = new ForegroundColorSpan(ContextCompat.getColor(this, R.color.light_green));
       // ssb.setSpan(light_green, 23, 32, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
       // tvSeniorResults.setText(ssb);

        // set layout for recyclerview
        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // set bottomNavigationView to transparent
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView); bottomNavigationView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));

        // count all senior users
        Query query =  mUserRef.orderByChild("userType").equalTo("Senior");
        query.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int counter = (int) snapshot.getChildrenCount();

                // convert counter to string
                String seniorCounter = String.valueOf(counter);

                // change substring color
                String str1 = " About " + seniorCounter;
                String str2 = " senior citizen";
                String str3 = " results";
                tvSeniorResults.setText(Html.fromHtml(str1 + "<font color=\"#049599\">" + str2 +  "</font> " + str3));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                throw error.toException();
            }
        });

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

    // display all senior users in recycle view
    private void LoadUsers(String s){

       // display all users that is senior
       Query query = mUserRef.orderByChild("userType").equalTo("Senior");

       FirebaseRecyclerOptions<ReadWriteUserDetails> options = new FirebaseRecyclerOptions.Builder<ReadWriteUserDetails>().setQuery(query, ReadWriteUserDetails.class).build();

        // TODO WE NEED DISPLAY ONLY SENIOR USERS IN RECYCLER VIEW

        FirebaseRecyclerAdapter<ReadWriteUserDetails, SearchSeniorViewHolder> adapter = new FirebaseRecyclerAdapter<ReadWriteUserDetails, SearchSeniorViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull SearchSeniorViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull ReadWriteUserDetails model) {

                if (!mUser.getUid().equals(getRef(position).getKey().toString())) { // prevent current login user to display in recycler view

                    // display profile pic of every user
                     Log.i("success",model.getImageURL());
                     Picasso.get()
                            .load(model.getImageURL())
                            .fit()
                            .transform(new CropCircleTransformation())
                            .into(holder.profileImage);

                     // display details of user
                    holder.fullName.setText(model.getFullName());

                } else {
                    holder.itemView.setVisibility(View.GONE);
                    holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                }
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(SearchSenior.this, ViewSenior.class);
                        intent.putExtra("userKey",getRef(position).getKey().toString());
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public SearchSeniorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_send_request, parent, false);
                return new SearchSeniorViewHolder(view);
            }
        };
       adapter.startListening();
       recyclerView.setAdapter(adapter);
    }

}