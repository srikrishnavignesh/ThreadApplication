package com.example.threadapplication.user_validation;

//a model class
public class User {
    String name;
    String collegeName;
    int year;
    String dept;
    String phoneNumber;
    boolean payment;

    public User()
    {

    }
    public User(String name, String collegeName, int year, String dept,String phoneNumber) {
        this.name = name;
        this.collegeName = collegeName;
        this.year = year;
        this.dept = dept;
        this.phoneNumber = phoneNumber;
        this.payment=false;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public boolean isPayment() { return payment; }
    public void setPayment(boolean payment) { this.payment = payment; }
}
