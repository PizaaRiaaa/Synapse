package com.example.synapse.screen.util;

public class ReadWriteUserDetails {
    public String fullName, email, mobileNumber, password, userType, profileImage;

    public ReadWriteUserDetails() { }

    public ReadWriteUserDetails(String textFullName, String textEmail, String textMobileNumber, String textPassword, String userType, String profileImage){
        this.fullName = textFullName;
        this.email = textEmail;
        this.mobileNumber = textMobileNumber;
        this.password = textPassword;
        this.userType = userType;
        this.profileImage = profileImage;
    }

    public String getProfileImage(){
        return profileImage;
    }

    public void setProfileImage(String imageUrl){
        this.profileImage = profileImage;
    }

}
