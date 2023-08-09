package com.example.homemanagement.model;

public class Channel
{
    int id;
    String name;
    String description;
    String latitude;
    String longitude;
    String field1;
    String field2;
    String field3;
    String created_at;
    String updated_at;
    int last_entry_id;

    public Channel() {
    }

    public Channel(int id, String name, String description, String latitude, String longitude, String field1, String field2, String field3, String created_at, String updated_at, int last_entry_id) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.field1 = field1;
        this.field2 = field2;
        this.field3 = field3;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.last_entry_id = last_entry_id;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
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

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public int getLast_entry_id() {
        return last_entry_id;
    }

    public void setLast_entry_id(int last_entry_id) {
        this.last_entry_id = last_entry_id;
    }
}
