package com.example.acadbudv2;

public class notif_item {
    private String title;
    private String message;

    public notif_item(String title, String message) {
        this.title = title;
        this.message = message;
    }


    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }
}