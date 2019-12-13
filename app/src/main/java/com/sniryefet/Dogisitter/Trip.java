package com.sniryefet.Dogisitter;

public class Trip {
    private String tripName;
    private String meetingPlace;
    private String date;
    private String time;
    private String duration;
    private String capacity;
    private String description;
    private String adminID;

    public Trip(){}
    public Trip(String name,String place,String date,String time,String duration,
                String capacity, String description, String adminID){
        this.tripName=name;
        this.meetingPlace=place;
        this.date=date;
        this.time=time;
        this.duration=duration;
        this.capacity=capacity;
        this.description=description;
        this.adminID = adminID;
    }

    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public String getMeetingPlace() {
        return meetingPlace;
    }

    public void setMeetingPlace(String meetingPlace) {
        this.meetingPlace = meetingPlace;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public String getAdminID() {
        return adminID;
    }

    public void setAdminID(String adminID) {
        this.adminID = adminID;
    }
}