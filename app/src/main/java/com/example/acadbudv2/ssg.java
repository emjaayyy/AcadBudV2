package com.example.acadbudv2;

public class ssg {
    private String position;
    private String lrn;
    private String name;
    private String email;

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    private String password;

    public ssg() {
        // Default constructor required for Firebase
    }

    public ssg(String position, String lrn, String name, String email, String password) {
        this.position = position;
        this.lrn = lrn;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public String getLrn() {
        return lrn;
    }

    public void setLrn(String lrn) {
        this.lrn = lrn;
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

    public String getPassword() {
        return password;
    }


    public void setPassword(String password) {
        this.password = password;
    }
    // Add getters and setters for each field
}
