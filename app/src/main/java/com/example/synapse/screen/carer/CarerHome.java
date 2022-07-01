package com.example.synapse.screen.carer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.example.synapse.R;
import com.example.synapse.screen.util.ReadWriteUserDetails;
import com.example.synapse.screen.util.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CarerHome extends AppCompatActivity {

    private ImageView ivProfilePic;
    private TextView tvFullName;
    private String fullName;
    private BottomNavigationView bottomNavigationView;

    private String userID;
    private FirebaseUser user;
    private DatabaseReference referenceProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carer_home);

        ivProfilePic = findViewById(R.id.ivCarerProfilePic);
        tvFullName = (TextView) findViewById(R.id.tvCarerSeniorFullName);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // get instance of the current user
        user = FirebaseAuth.getInstance().getCurrentUser();
        referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
        userID = user.getUid();

        // set bottomNavigationView to transparent
        bottomNavigationView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));

        // showAlertDialog();

            showUserProfile(userID);



    }

    // new registered carer needs to send request to senior user to start push notifications
 //   private void showAlertDialog(){

 //       // setup the alert builder
 //       AlertDialog.Builder builder = new AlertDialog.Builder(CarerHome.this);
 //       builder.setTitle("Welcome! Thank you for signing up.");
 //       builder.setMessage("Please send request to your senior loved ones to start sending notification reminders.");

 //       // open email app if user clicks/taps continue
 //       builder.setPositiveButton("Continue", (dialog, which) -> {
 //           startActivity(new Intent(CarerHome.this, SearchSenior.class));
 //           finish();
 //       });

 //       AlertDialog alertDialog = builder.create();
 //       alertDialog.show();

 //   }

   // display carer profile pic
    private void showUserProfile(String firebaseUser){
        referenceProfile.child(firebaseUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails userProfile = snapshot.getValue(ReadWriteUserDetails.class);

                if(userProfile != null){
                        String fullName = userProfile.fullName;
                        tvFullName.setText(fullName);

                        Uri uri = user.getPhotoUrl();
                        //ImageView setImagerURI() should not be used with regular URIs. so we are using picasso
                        Picasso.get().load(uri).transform(new CircleTransform()).into(ivProfilePic);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CarerHome.this, "Something went wrong! Please login again.", Toast.LENGTH_LONG).show();
            }
        });
   }

    // circular image with picasso
    public class CircleTransform implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());

            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
            if (squaredBitmap != source) {
                source.recycle();
            }

            Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            BitmapShader shader = new BitmapShader(squaredBitmap,
                    Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            paint.setShader(shader);
            paint.setAntiAlias(true);

            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);

            squaredBitmap.recycle();
            return bitmap;
        }

        @Override
        public String key() {
            return "circle";
        }
    }
}