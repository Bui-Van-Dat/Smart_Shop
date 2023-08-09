package com.example.homemanagement;

import java.util.Date;

public class Week {

    private String dateCreate;
    private int numberWeek;

    public Week(String dateCreate, int numberWeek) {
        this.dateCreate = dateCreate;
        this.numberWeek = numberWeek;
    }

    public Week() {
    }

    public String getDateCreate() {
        return dateCreate;
    }

    public void setDateCreate(String dateCreate) {
        this.dateCreate = dateCreate;
    }

    public int getNumberWeek() {
        return numberWeek;
    }

    public void setNumberWeek(int numberWeek) {
        this.numberWeek = numberWeek;
    }

    @Override
    public String toString() {
        return "Week{" +
                "dateCreate='" + dateCreate + '\'' +
                ", numberWeek=" + numberWeek +
                '}';
    }
}
