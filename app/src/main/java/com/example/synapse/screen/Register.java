package com.example.synapse.screen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.synapse.screen.util.ReadWriteUserDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.example.synapse.R;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    private EditText etFullName,etEmail, etPassword, etMobileNumber;
    private static final String TAG = "RegisterActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmailRegister);
        etPassword = findViewById(R.id.etRegisterPassword);
        etMobileNumber = findViewById(R.id.etMobileNumber);

        Button btnSignup = findViewById(R.id.btnSignup);
        btnSignup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
            // obtain the entered data
                String textFullName = etFullName.getText().toString();
                String textEmail = etEmail.getText().toString();
                String textPassword = etPassword.getText().toString();
                String textMobileNumber = etMobileNumber.getText().toString();

                if(TextUtils.isEmpty(textFullName)){
                    Toast.makeText(Register.this, "Please enter your full name", Toast.LENGTH_LONG).show();
                    etFullName.requestFocus();
                }else if(!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()){
                    Toast.makeText(Register.this, "Please re-enter your email", Toast.LENGTH_LONG).show();
                    etEmail.requestFocus();
                }else if(TextUtils.isEmpty(textMobileNumber)){
                    Toast.makeText(Register.this, "Please re-enter your mobile number", Toast.LENGTH_LONG).show();
                    etMobileNumber.requestFocus();
                }else if(textMobileNumber.length() != 11){
                    Toast.makeText(Register.this, "Please re-enter your mobile number", Toast.LENGTH_LONG).show();
                    etMobileNumber.setError("Mobile no. should be 11 digits");
                    etMobileNumber.requestFocus();
                }
                else if(TextUtils.isEmpty(textPassword)){
                    Toast.makeText(Register.this, "Please re-enter your password", Toast.LENGTH_LONG).show();
                    etPassword.requestFocus();
                }else{
                    signupUser(textFullName,textEmail,textMobileNumber,textPassword);
                }
            }
        });
    }

    // register User using the credentials given
    private void signupUser(String textFullName, String textEmail, String textMobileNumber, String textPassword){
        FirebaseAuth auth = FirebaseAuth.getInstance();

        // Create UserProfile
        auth.createUserWithEmailAndPassword(textEmail, textPassword).addOnCompleteListener(Register.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                      if(task.isSuccessful()){

                          FirebaseUser firebaseUser = auth.getCurrentUser();

                           // enter user data into the firebase realtime database
                          ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textFullName, textEmail, textMobileNumber);

                          // extracting user reference from database for "registered user"
                          DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");


                          referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                              @Override
                              public void onComplete(@NonNull Task<Void> task) {

                                  if(task.isSuccessful()){
                                      // send verification email
                                      firebaseUser.sendEmailVerification();

                                      Toast.makeText(Register.this, "Registered successfully. Please Verify your email", Toast.LENGTH_LONG).show();

                                      // open MainActivity  after successful signup
                                      //Intent intent = new Intent(Register.this, UserProfile.class);

                                      //prevent user from returning back to register activity
                                      //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                       //       | Intent.FLAG_ACTIVITY_NEW_TASK);
                                     // startActivity(intent);
                                      //finish();
                                  }else{
                                      Toast.makeText(Register.this, "User registered failed. Please try again",
                                              Toast.LENGTH_LONG).show();
                                  }
                              }
                          });
                      }else{
                          try{
                              throw task.getException();
                          }catch(FirebaseAuthWeakPasswordException e){
                              etPassword.setError("Your password is to weak. Please use a-z alphabets and numbers");
                              etPassword.requestFocus();
                          }catch(FirebaseAuthInvalidCredentialsException e){
                              etPassword.setError("Your email is invalid or already in use. Kindly re-enter.");
                              etPassword.requestFocus();
                          }catch(FirebaseAuthUserCollisionException e){
                              etPassword.setError("Email is already registered. User another email.");
                              etPassword.requestFocus();
                          }catch(Exception e){
                              Log.e(TAG, e.getMessage());
                              Toast.makeText(Register.this, e.getMessage(), Toast.LENGTH_LONG).show();
                          }
                      }
                    }
                });
    }
}