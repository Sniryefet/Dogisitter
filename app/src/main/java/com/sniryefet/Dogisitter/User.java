package com.sniryefet.Dogisitter;
import java.util.ArrayList;

public class User {

    private String name,email,permission;
    private ArrayList<String> trips;

    public User(String name, String email, String permission,ArrayList<String> trips) {
        this.name = name;
        this.email = email;
        this.permission = permission;
        this.trips = trips;
    }

    public User() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public void addTrip (String t){
        this.trips.add(t);
    }
    public void setTrips(ArrayList<String> trips) {
        this.trips = trips;
    }

}