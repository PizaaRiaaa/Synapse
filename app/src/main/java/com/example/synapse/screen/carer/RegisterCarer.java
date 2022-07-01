package com.example.synapse.screen.carer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

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
import com.example.synapse.R;
import com.example.synapse.screen.Login;
import com.example.synapse.screen.PickRole;
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

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterCarer extends AppCompatActivity {

    private EditText etFullName,etEmail, etPassword, etMobileNumber;
    private String userType;
    private static final String TAG = "RegisterActivity";
    private final String profilePic = "";
    private ImageView ivProfilePic;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri uriImage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_carer);

        // (ImageButton) bring user back to PickRole screen
        ImageButton ibBack = findViewById(R.id.ibRegisterCarerBack);
        ibBack.setOnClickListener(view -> startActivity(new Intent(RegisterCarer.this, PickRole.class)));

        // (TextView) bring user back to Login screen
        TextView tvAlreadyHaveAccount = findViewById(R.id.tvCarerHaveAccount);
        tvAlreadyHaveAccount.setOnClickListener(view -> startActivity(new Intent(RegisterCarer.this, Login.class)));


        // open file dialog for profile pic
        ivProfilePic = findViewById(R.id.ivCarerProfilePic);
        AppCompatImageView chooseProfilePic = findViewById(R.id.ic_carer_choose_profile_pic);
        chooseProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                openFileChooser();
            }
        });

        etFullName = findViewById(R.id.etCarerFullName);
        etEmail = findViewById(R.id.etCarerEmail);
        etPassword = findViewById(R.id.etRegisterCarerPassword);
        etMobileNumber = findViewById(R.id.etCarerMobileNumber);


        Button btnSignup = findViewById(R.id.btnSignupCarer);
        btnSignup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                // obtain the entered data
                String textFullName = etFullName.getText().toString();
                String textEmail = etEmail.getText().toString();
                String textPassword = etPassword.getText().toString();
                String textMobileNumber = etMobileNumber.getText().toString();
                userType = "Carer";

                // validate mobile number using matcher and regex
                String mobileRegex = "^(09|\\+639)\\d{9}$"; // first no. can be {09 or +639} and rest 9 no. can be any no.
                Matcher mobileMatcher;
                Pattern mobilePattern = Pattern.compile(mobileRegex);
                mobileMatcher = mobilePattern.matcher(textMobileNumber);

                if(TextUtils.isEmpty(textFullName)){
                    Toast.makeText(RegisterCarer.this, "Please enter your full name", Toast.LENGTH_LONG).show();
                    etFullName.requestFocus();
                }else if(!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()){
                    Toast.makeText(RegisterCarer.this, "Please re-enter your email", Toast.LENGTH_LONG).show();
                    etEmail.requestFocus();
                }else if(TextUtils.isEmpty(textMobileNumber)){
                    Toast.makeText(RegisterCarer.this, "Please re-enter your mobile number", Toast.LENGTH_LONG).show();
                    etMobileNumber.requestFocus();
                }else if(textMobileNumber.length() != 11){
                    Toast.makeText(RegisterCarer.this, "Please re-enter your mobile number", Toast.LENGTH_LONG).show();
                    etMobileNumber.setError("Mobile no. should be 11 digits. e.g 09166882880");
                    etMobileNumber.requestFocus();
                }else if(!mobileMatcher.find()){
                    Toast.makeText(RegisterCarer.this, "Please re-enter your mobile number", Toast.LENGTH_LONG).show();
                    etMobileNumber.setError("Mobile no. is not valid.");
                    etMobileNumber.requestFocus();
                }
                else if(TextUtils.isEmpty(textPassword)){
                    Toast.makeText(RegisterCarer.this, "Please re-enter your password", Toast.LENGTH_LONG).show();
                    etPassword.requestFocus();
                }else if(uriImage == null){
                    Toast.makeText(RegisterCarer.this, "Please select your profile picture", Toast.LENGTH_LONG).show();
                }
                else{
                    signupUser(textFullName,textEmail,textMobileNumber,textPassword,userType, profilePic);
                }
            }
        });
    }

    // register user using the credentials given
    private void signupUser(String textFullName, String textEmail, String textMobileNumber, String textPassword, String userType, String profilePic){
        FirebaseAuth auth = FirebaseAuth.getInstance();

        // Create UserProfile
        auth.createUserWithEmailAndPassword(textEmail, textPassword).addOnCompleteListener(RegisterCarer.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            FirebaseUser firebaseUser = auth.getCurrentUser();

                            // enter user data into the firebase realtime database
                            ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textFullName, textEmail, textMobileNumber, textPassword, userType, profilePic);

                            // extracting user reference from database for "registered user"
                            // TODO RENAME DB TO "Users"
                            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");

                            // store profile picture of carer
                            // TODO RENAME DB TO "Profile Pictures"
                            storageReference = FirebaseStorage.getInstance().getReference("DisplayPics");

                            referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    // user upload profile pic
                                    if(uriImage != null){

                                        // save profile pic with userid filename
                                        StorageReference fileReference = storageReference.child(auth.getCurrentUser().getUid() + "."
                                                + getFileExtension(uriImage));

                                        fileReference.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {

                                                        Log.d(TAG, "Download URL = "+ uri.toString());

                                                        //Adding that URL to Realtime database
                                                        referenceProfile.child(firebaseUser.getUid()).child("imageURL").setValue(uri.toString());

                                                        // finally set the display image of the user after upload
                                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                                .setPhotoUri(uri).build();
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

                                        Toast.makeText(RegisterCarer.this, "Registered successfully. Please verify your email.", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(RegisterCarer.this, CarerVerifyEmail.class));
                                    }else{
                                        Toast.makeText(RegisterCarer.this, "User registered failed. Please try again",
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
                                Toast.makeText(RegisterCarer.this, e.getMessage(), Toast.LENGTH_LONG).show();
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

//    // creating ActionBar Menu
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu){
//        // inflate menu items
//        getMenuInflater().inflate(R.menu.common_menu, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    // when any menu item is selected
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item){
//        int id = item.getItemId();
//
//        if(id == R.id.menu_refresh){
//            // refresh activity
//            startActivity(getIntent());
//            finish();
//            overridePendingTransition(0, 0);
//        }
//        return super.onOptionsItemSelected(item);
//    }
}