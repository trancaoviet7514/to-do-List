package com.example.trancaoviet.myhelper;

public class Task {

    boolean Complete;
    int Id;
    String Content;
    String Date;
    String Time;
    String Notifycation;

    public Task(int id,  String content, String date, String time, String notifycation , boolean complete) {

        Complete = complete;
        Id = id;
        Content = content;
        Date = date;
        Time = time;
        Notifycation = notifycation;
    }

    public boolean isComplete() {
        return Complete;
    }

    public void setComplete(boolean complete) {
        Complete = complete;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public Task(String content, String date, String time, String notifycation, boolean complete) {

        Content = content;
        Date = date;
        Time = time;
        Notifycation = notifycation;
        Complete = complete;
    }

    public Task( String Title, String content, String date, String time) {

        Content = content;
        Date = date;
        Time = time;

    }

    public Task() {

    }

    public void setContent(String content) {
        Content = content;
    }

    public void setDate(String date) {
        Date = date;
    }

    public void setTime(String time) {
        Time = time;
    }

    public void setNotifycation(String notifycation) {
        Notifycation = notifycation;
    }

    public String getContent() {
        return Content;
    }

    public String getDate() {
        return Date;
    }

    public String getTime() {
        return Time;
    }

    public String getNotifycation() {
        return Notifycation;
    }


}
