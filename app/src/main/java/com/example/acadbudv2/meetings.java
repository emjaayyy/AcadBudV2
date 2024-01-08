package com.example.acadbudv2;

import android.icu.text.SimpleDateFormat;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
    public long getMeetingDateTimeMillis() {
        // Assuming your date and time are in a format like "yyyy-MM-dd HH:mm"
        String dateTimeString = date + " " + time;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        try {
            Date meetingDateTime = sdf.parse(dateTimeString);
            if (meetingDateTime != null) {
                return meetingDateTime.getTime();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0; // Default value if parsing fails
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
    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }
    public void addParticipant(String participant) {
        if (participants == null) {
            participants = new ArrayList<>();
        }
        participants.add(participant);
    }
}
