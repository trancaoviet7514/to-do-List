package com.example.trancaoviet.myhelper;

import java.io.Serializable;
import java.sql.Time;
import java.util.Date;

public class Task implements Serializable{

    private int Id;
    private String Content;
    private Date date;
    private Time time;
    private boolean isComplete;
    private boolean hasNotifycation;

    public Task(int id, String content, Date date, Time time, boolean isComplete, boolean hasNotifycation) {
        Id = id;
        Content = content;
        this.date = date;
        this.time = time;
        this.isComplete = isComplete;
        this.hasNotifycation = hasNotifycation;
    }

    public Task(String content, Date date, Time time, boolean isComplete, boolean hasNotifycation) {
        Content = content;
        this.date = date;
        this.time = time;
        this.isComplete = isComplete;
        this.hasNotifycation = hasNotifycation;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setComplete(boolean complete) {
        isComplete = complete;
    }

    public boolean isHasNotifycation() {
        return hasNotifycation;
    }

    public void setHasNotifycation(boolean hasNotifycation) {
        this.hasNotifycation = hasNotifycation;
    }
}
