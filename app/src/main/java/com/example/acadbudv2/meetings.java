package com.example.acadbudv2;

import java.util.ArrayList;
import java.util.List;

public class meetings {
    private String key;
    private String subject;
    private String topic;
    private String date;
    private String time;
    private List<String> participants;

    // No-argument constructor required for Firebase
    public meetings() {
        // Default constructor required for Firebase
    }

    public meetings(String subject, String topic, String date, String time, List<String> participants) {
        this.subject = subject;
        this.topic = topic;
        this.date = date;
        this.time = time;
        this.participants = participants;
    }

    // Add getter and setter methods for the new 'key' field
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
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

    public List<String> getParticipants() {
        return participants;
    }

    public void addParticipant(String participant) {
        if (participants == null) {
            participants = new ArrayList<>();
        }
        participants.add(participant);
    }
}
