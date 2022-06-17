package com.example.synapse.screen;

public class ReadWriteUserDetails {
    public String fullName, email, mobileNumber, password;

    public ReadWriteUserDetails(String textFullName,String textEmail, String textMobileNumber){
        this.fullName = textFullName;
        this.email = textEmail;
        this.mobileNumber = textMobileNumber;
    }
}
