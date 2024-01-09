package com.example.acadbudv2;

public class post_content {
    private String posts, name, date, userId;

    public post_content(String name, String date, String posts, String userId) {
        this.name = name;
        this.date = date;
        this.posts = posts;
        this.userId = userId;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isCurrentUser(String currentUserId) {
        // Check if the post's userId is equal to the current user's id
        return this.userId != null && this.userId.equals(currentUserId);
    }
}
