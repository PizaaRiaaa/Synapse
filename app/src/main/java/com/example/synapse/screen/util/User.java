package com.example.synapse.screen.util;

public class User {
    public String fullName;
    public String phoneNumber;
    public String email;
    public String userType;

    public User() { } // dont remove

    public User(String fullName, String phoneNumber, String email, String userType) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.userType = userType;
    }

    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }
}

