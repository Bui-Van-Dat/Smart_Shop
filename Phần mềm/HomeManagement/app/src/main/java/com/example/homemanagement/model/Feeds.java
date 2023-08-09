package com.example.homemanagement.model;

public class Feeds {
    String created_at;
    int entry_id;
    String field1;
    String field2;
    String field3;

    public Feeds() {
    }

    public Feeds(String created_at, int entry_id, String field1, String field2, String field3) {
        this.created_at = created_at;
        this.entry_id = entry_id;
        this.field1 = field1;
        this.field2 = field2;
        this.field3 = field3;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public int getEntry_id() {
        return entry_id;
    }

    public void setEntry_id(int entry_id) {
        this.entry_id = entry_id;
    }

    public String getField1() {
        return field1;
    }

    public void setField1(String field1) {
        this.field1 = field1;
    }

    public String getField2() {
        return field2;
    }

    public void setField2(String field2) {
        this.field2 = field2;
    }

    public String getField3() {
        return field3;
    }

    public void setField3(String field3) {
        this.field3 = field3;
    }
}
