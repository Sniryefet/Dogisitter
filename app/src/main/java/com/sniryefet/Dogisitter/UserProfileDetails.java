package com.sniryefet.Dogisitter;

public class UserProfileDetails {
   private String Name;
   private String birthDate;
   private String Email;
   private String phoneNumber;
   private String Address;
   private String instagram;


   public UserProfileDetails(){

   }
    public UserProfileDetails(String name,String birthDate ,String email, String phoneNumber, String address, String instagram) {
        Name = name;
        this.birthDate=birthDate;
        Email = email;
        this.phoneNumber = phoneNumber;
        Address = address;
        this.instagram = instagram;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getInstagram() {
        return instagram;
    }

    public void setInstagram(String instegram) {
        this.instagram = instegram;
    }
}
