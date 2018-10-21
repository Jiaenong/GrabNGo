package com.example.user.grabngo.Class;

public class Customer {
    private String email;
    private String password;
    private String name;
    private String gender;
    private String address;
    private String profilePic;

    public Customer(){

    }

    public Customer(String email, String password, String name, String gender, String address, String profilePic){
        this.email = email;
        this.password = password;
        this.name = name;
        this.gender = gender;
        this.address = address;
        this.profilePic = profilePic;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }
}
