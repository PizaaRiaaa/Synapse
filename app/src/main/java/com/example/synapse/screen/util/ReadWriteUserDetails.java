package com.example.synapse.screen.util;

public class ReadWriteUserDetails {
    public String fullName, email, mobileNumber, password, userType;

    public ReadWriteUserDetails() { }

    public ReadWriteUserDetails(String textFullName, String textEmail, String textMobileNumber, String textPassword, String userType){
        this.fullName = textFullName;
        this.email = textEmail;
        this.mobileNumber = textMobileNumber;
        this.password = textPassword;
        this.userType = userType;
    }


}
