package com.example.remindat;

public class Model {
    String Task;
    int status;
    double lat;
    double lon;

    int id;


    public Model(String task, int status, double lat, double lon) {
        Task = task;
        this.status = status;
        this.lat = lat;
        this.lon = lon;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Model(String task, int status, int id, double lat, double lon) {
        Task = task;
        this.status = status;
        this.id = id;
        this.lat = lat;
        this.lon = lon;
    }

    public String getTask() {
        return Task;
    }

    public void setTask(String task) {
        Task = task;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}