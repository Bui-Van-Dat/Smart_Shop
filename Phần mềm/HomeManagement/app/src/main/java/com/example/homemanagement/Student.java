package com.example.homemanagement;

import java.util.HashMap;
import java.util.Map;

public class Student {
    private int id;
    private String name;
    private int team;
    private int absent;

    public Student() {
    }

    public Student(int id, String name, int team, int absent) {
        this.id = id;
        this.name = name;
        this.team = team;
        this.absent = absent;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTeam() {
        return team;
    }

    public void setTeam(int team) {
        this.team = team;
    }

    public int getAbsent() {
        return absent;
    }

    public void setAbsent(int absent) {
        this.absent = absent;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", team=" + team +
                ", absent=" + absent +
                '}';
    }
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("absent", absent);
        return result;
    }

}
