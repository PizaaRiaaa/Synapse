package com.example.synapse.screen.util;

public class ReadWriteUserDetails {
    public String fullName, email, mobileNumber, password, userType, imageURL;

    public ReadWriteUserDetails() { }

    public ReadWriteUserDetails(String textFullName, String textEmail, String textMobileNumber, String textPassword, String userType, String imageURL){
        this.fullName = textFullName;
        this.email = textEmail;
        this.mobileNumber = textMobileNumber;
        this.password = textPassword;
        this.userType = userType;
        this.imageURL = imageURL;
    }

    public String getFullName(){ return fullName; }
    public void setFullName(String fullName){ this.fullName = fullName; }

    public String getEmail(){ return email; }
    public void setEmail(String email){ this.email = email; }

    public String getMobileNumber(){ return mobileNumber; }
    public void setMobileNumber(String mobileNumber){ this.mobileNumber = mobileNumber; }

    public String getUserType(){ return userType; }
    public void setUserType(String userType){ this.userType = userType; }

    public String getImageURL(){ return imageURL; }
    public void setImageURL(String imageUrl){ this.imageURL = imageUrl; }

}
