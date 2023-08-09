package com.example.homemanagement;

public class Leave {

    String name;
    int id;
    String date;
    String reason;

    public Leave(String name, int id, String date, String reason) {
        this.name = name;
        this.id = id;
        this.date = date;
        this.reason = reason;
    }

    public Leave() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
