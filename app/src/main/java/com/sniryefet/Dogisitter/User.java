package com.sniryefet.Dogisitter;

public class User {

    private String name,email,permission;

    public User(String name, String email, String permission) {
        this.name = name;
        this.email = email;
        this.permission = permission;
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
}
