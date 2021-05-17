package com.example.fatiguedriverdetection;

public class userfunc {

    String name;
    String address;
    String phone;
    String dl;

    public userfunc(String name, String address, String phone, String dl) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.dl = dl;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getDl() {
        return dl;
    }
}
