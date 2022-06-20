package com.example.synapse.screen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.example.synapse.R;
import com.example.synapse.Splashscreen;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class Login extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private FirebaseAuth mAuth;
    private static final String TAG = "loginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etLoginPassword);

        //  initialize firebase auth
        mAuth = FirebaseAuth.getInstance();

        // login user
        Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view){
                    String textEmail = etEmail.getText().toString();
                    String textPassword = etPassword.getText().toString();

                    if(TextUtils.isEmpty(textEmail)){
                        Toast.makeText(Login.this, "Please enter your email", Toast.LENGTH_LONG).show();
                        etEmail.setError("Email is required");
                        etEmail.requestFocus();
                    }else if(!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()){
                        Toast.makeText(Login.this, "Please re-enter your email", Toast.LENGTH_LONG).show();
                        etEmail.setError("Valid email is required");
                        etPassword.requestFocus();
                   }else if(TextUtils.isEmpty(textPassword)){
                        Toast.makeText(Login.this, "Please enter your password", Toast.LENGTH_LONG).show();
                        etPassword.setError("Password is required");
                        etPassword.requestFocus();
                   }else{
                        loginUser(textEmail, textPassword);
                    }
             }
        });


        // proceed to register screen
        TextView tvSwitchToRegister = findViewById(R.id.btnRegister);
        tvSwitchToRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                switchToRegister();
            }
        });

        // CHANGE SUBSTRING COLOR
        TextView tvRegister = findViewById(R.id.tvWelcomeToSynapse);
        String text = "Welcome to Synapse";

        SpannableString ss = new SpannableString(text);
        SpannableStringBuilder ssb = new SpannableStringBuilder(text);

        ForegroundColorSpan light_green = new ForegroundColorSpan(ContextCompat.getColor(this, R.color.light_green));

        ssb.setSpan(light_green, 11, 18, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvRegister.setText(ssb);


        // show status bar
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // transparent status bar
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.mid_grey));
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.mid_grey));
        }
    }

    private void loginUser(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    // get instance of the current user
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();

                    // check if email is verified before user can access the MainActivity
                    if(firebaseUser.isEmailVerified()){
                        Toast.makeText(Login.this, "You are logged in now", Toast.LENGTH_LONG).show();

                        // open user's MainActivity
                    }else{
                        firebaseUser.sendEmailVerification();
                        mAuth.signOut();
                        showAlertDialog();
                    }

                }else{
                    try{
                         throw task.getException();
                    }catch(FirebaseAuthInvalidUserException e){
                         etEmail.setError("User does not exists or is not longer valid. Please register again.");
                         etEmail.requestFocus();
                    }catch(FirebaseAuthInvalidCredentialsException e){
                        etPassword.setError("Invalid credentials. Kindly, check and re-enter.");
                        etPassword.requestFocus();
                    }catch(Exception e){
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private void showAlertDialog(){
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
        builder.setTitle("Email Not Verified");
        builder.setMessage("Please verify your email now. You can not login without email verification.");

        // open email app if user clicks/taps continue
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // open email app in new window and not within our app
                startActivity(intent);
            }
        });

        // create the AlertDialog
        AlertDialog alertDialog = builder.create();

        // show the AlertDialog
        alertDialog.show();
    }

    // check if User is already logged in, then direct to the MainActivity
    @Override
    protected void onStart(){
        super.onStart();
        if(mAuth.getCurrentUser() != null){
            Toast.makeText(Login.this, "Already Logged In!", Toast.LENGTH_SHORT).show();

            // start the MainActivity
            startActivity(new Intent(Login.this, UserProfile.class));
            finish();
        }else{
            Toast.makeText(Login.this, "You can Login now!", Toast.LENGTH_SHORT).show();
        }
    }

    private void switchToRegister(){
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
        finish();
    }
 }