package com.sniryefet.Dogisitter;
import java.util.ArrayList;

public class User {

    private String name,email,permission;
    //private ArrayList<Trip> trips;

    public User(String name, String email, String permission) {
        this.name = name;
        this.email = email;
        this.permission = permission;
        //trips = new ArrayList<Trip>();
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

    //public void addTrip (Trip t){this.trips.add(t);}
    //public void setTrips(ArrayList<Trip> trips) { this.trips = trips; }

}
