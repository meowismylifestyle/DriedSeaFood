package com.example.myapplication;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class User {
    public String fullName, day,month, year, email;
    private ArrayList<String> favouriteFishes;

    public User() {
        this.favouriteFishes = new ArrayList<>();
    }

    public User(String fullname, String day,String month,String year, String email){
        this.fullName = fullname;
        this.day = day;
        this.month = month;
        this.year = year;
        this.email = email;
        this.favouriteFishes = new ArrayList<>();
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getBirthday() {
        return String.format("%s/%s/%s", day, month, year);
    }

    public void setBirthday(String birthday) {
        String[] parts = birthday
                .replaceAll("\\s+", "")
                .split("/");

        if (parts.length == 3) {
            this.day = parts[0];
            this.month = parts[1];
            this.year = parts[2];
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<String> getFavouriteFishes() {
        return favouriteFishes;
    }

    public void setFavouriteFishes(ArrayList<String> favouriteFishes) {
        this.favouriteFishes = favouriteFishes;
    }

    @Override
    public String toString() {
        return "User{" +
                "fullName='" + fullName + '\'' +
                ", day='" + day + '\'' +
                ", month='" + month + '\'' +
                ", year='" + year + '\'' +
                ", email='" + email + '\'' +
                ", favouriteFishes=" + favouriteFishes +
                '}';
    }

    public void addFavouriteFish(String fish) {
        if (!this.favouriteFishes.contains(fish)) {
            this.favouriteFishes.add(fish);
        }
    }
}
