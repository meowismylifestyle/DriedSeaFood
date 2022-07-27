package com.example.myapplication;

public class User {
    public String fullName, day,month, year, email;
    public User(){

    }
    public User(String fullname, String day,String month,String year, String email){
        this.fullName = fullname;
        this.day = day;
        this.month = month;
        this.year = year;
        this.email = email;
    }
}
