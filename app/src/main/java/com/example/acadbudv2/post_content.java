package com.example.acadbudv2;

public class post_content {
    private String posts, name, date;

    public post_content(String name, String date, String posts) {
        this.name = name;
        this.date = date;
        this.posts = posts;
    }

    // Empty constructor for Firebase
    public post_content() {
    }

    public String getPosts() {
        return posts;
    }

    public void setPosts(String posts) {
        this.posts = posts;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
