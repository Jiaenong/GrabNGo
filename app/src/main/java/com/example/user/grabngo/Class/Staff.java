package com.example.user.grabngo.Class;

public class Staff {
    private String address;
    private String email;
    private String name;
    private String password;
    private String phone;
    private String profileUrl;

    public Staff(){

    }

    public Staff(String address, String email, String name, String password, String phone, String profileUrl) {
        this.address = address;
        this.email = email;
        this.name = name;
        this.password = password;
        this.phone = phone;
        this.profileUrl = profileUrl;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }
}
