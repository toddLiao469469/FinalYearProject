package com.example.caretaker;

public class User {
    public String name;
    public String address;
    public int height;
    public int weight;
    public String phone;


    public User(String name, int height, int weight, String phone, String address){
        this.name = name;
        this.height = height;
        this.weight = weight;
        this.address = address;
        this.phone = phone;
    }
}
