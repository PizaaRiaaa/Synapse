package com.example.synapse.screen.senior;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import com.example.synapse.R;
import com.example.synapse.screen.Login;
import com.example.synapse.screen.PickRole;
import com.example.synapse.screen.carer.CarerVerifyEmail;
import com.example.synapse.screen.util.ReadWriteUserDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterSenior extends AppCompatActivity {
    private EditText etFullName,etEmail, etPassword, etMobileNumber;
    private static final String TAG = "RegisterActivity";
    private String userType;
    private ImageView ivProfilePic;
    private AppCompatImageView chooseProfilePic;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri uriImage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_senior);


        // (ImageButton) bring user back to PickRole screen
        ImageButton ibBack = findViewById(R.id.ibRegisterSeniorBack);
        ibBack.setOnClickListener(view -> startActivity(new Intent(RegisterSenior.this, PickRole.class)));

        // (TextView) bring user back to Login screen
        TextView tvAlreadyHaveAccount = findViewById(R.id.tvSeniorHaveAccount);
        tvAlreadyHaveAccount.setOnClickListener(view -> startActivity(new Intent(RegisterSenior.this, Login.class)));

        // open file dialog for profile pic
        ivProfilePic = findViewById(R.id.ibSeniorProfilePic);
        chooseProfilePic = findViewById(R.id.ic_senior_choose_profile_pic);
        chooseProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                openFileChooser();
            }
        });

        etFullName = findViewById(R.id.etSeniorFullName);
        etEmail = findViewById(R.id.etSeniorEmail);
        etPassword = findViewById(R.id.etRegisterSeniorPassword);
        etMobileNumber = findViewById(R.id.etSeniorMobileNumber);

        Button btnSignup = findViewById(R.id.btnSignupSenior);
        btnSignup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                // obtain the entered data
                String textFullName = etFullName.getText().toString();
                String textEmail = etEmail.getText().toString();
                String textPassword = etPassword.getText().toString();
                String textMobileNumber = etMobileNumber.getText().toString();
                userType = "Senior";


                if(TextUtils.isEmpty(textFullName)){
                    Toast.makeText(RegisterSenior.this, "Please enter your full name", Toast.LENGTH_LONG).show();
                    etFullName.requestFocus();
                }else if(!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()){
                    Toast.makeText(RegisterSenior.this, "Please re-enter your email", Toast.LENGTH_LONG).show();
                    etEmail.requestFocus();
                }else if(TextUtils.isEmpty(textMobileNumber)){
                    Toast.makeText(RegisterSenior.this, "Please re-enter your mobile number", Toast.LENGTH_LONG).show();
                    etMobileNumber.requestFocus();
                }else if(textMobileNumber.length() != 11){
                    Toast.makeText(RegisterSenior.this, "Please re-enter your mobile number", Toast.LENGTH_LONG).show();
                    etMobileNumber.setError("Mobile no. should be 11 digits");
                    etMobileNumber.requestFocus();
                }
                else if(TextUtils.isEmpty(textPassword)){
                    Toast.makeText(RegisterSenior.this, "Please re-enter your password", Toast.LENGTH_LONG).show();
                    etPassword.requestFocus();
                }else{
                    signupUser(textFullName,textEmail,textMobileNumber,textPassword,userType);
                }
            }
        });
    }

    // register User using the credentials given
    private void signupUser(String textFullName, String textEmail, String textMobileNumber, String textPassword, String userType){
        FirebaseAuth auth = FirebaseAuth.getInstance();

        // Create UserProfile
        auth.createUserWithEmailAndPassword(textEmail, textPassword).addOnCompleteListener(RegisterSenior.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            FirebaseUser firebaseUser = auth.getCurrentUser();

                            // enter user data into the firebase realtime database
                            ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textFullName, textEmail, textMobileNumber, textPassword, userType);

                            // extracting user reference from database for "registered user"
                            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");

                            // store profile picture of carer
                            storageReference = FirebaseStorage.getInstance().getReference("DisplayPics");

                            referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    // If user change upload profile pic
                                    if(uriImage != null){
                                        // save the image
                                        StorageReference fileReference = storageReference.child(auth.getCurrentUser().getUid() + "."
                                                + getFileExtension(uriImage));

                                        fileReference.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        Uri downloadUri = uri;

                                                        // finally set the display image of the user after upload
                                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                                .setPhotoUri(downloadUri).build();
                                                        firebaseUser.updateProfile(profileUpdates);

                                                    }
                                                });

                                            }
                                        });
                                    }

                                    // if all inputs are valid
                                    if(task.isSuccessful()){
                                        // send verification email
                                        firebaseUser.sendEmailVerification();

                                        // sign out the user to prevent automatic sign in, right after successful register
                                        auth.signOut();

                                        Toast.makeText(RegisterSenior.this, "Registered successfully. Please verify your email.", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(RegisterSenior.this, CarerVerifyEmail.class));
                                    }else{
                                        Toast.makeText(RegisterSenior.this, "User registered failed. Please try again",
                                                Toast.LENGTH_LONG).show();
                                    }
                                    finish();
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
                                Toast.makeText(RegisterSenior.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    private void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null & data.getData() != null){
            uriImage = data.getData();
            ivProfilePic.setImageURI(uriImage);
        }
    }


    // obtain file extension of the image
    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
}