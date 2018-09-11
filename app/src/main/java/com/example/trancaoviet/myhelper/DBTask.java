package com.example.trancaoviet.myhelper;

public class DBTask {

    private int Id;
    private String Content;
    private String date;
    private String time;
    private boolean isComplete;
    private boolean hasNotifycation;

    public DBTask() {
    }

    public DBTask(int id, String content, String date, String time, boolean isComplete, boolean hasNotifycation) {
        Id = id;
        Content = content;
        this.date = date;
        this.time = time;
        this.isComplete = isComplete;
        this.hasNotifycation = hasNotifycation;
    }

    public DBTask(Task task){
        Id = task.getId();
        Content = task.getContent();
        date = task.getDate().toString();
        time = task.getTime().toString();
        isComplete = task.isComplete();
        hasNotifycation = task.isHasNotifycation();
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
