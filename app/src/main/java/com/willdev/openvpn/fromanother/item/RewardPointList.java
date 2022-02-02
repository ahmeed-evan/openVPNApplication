package com.willdev.openvpn.fromanother.item;

import java.io.Serializable;

public class RewardPointList implements Serializable {

    private String id, title, status_thumbnail, user_id, activity_type, points, date,time;

    public RewardPointList(String id, String title, String status_thumbnail, String user_id, String activity_type, String points, String date, String time) {
        this.id = id;
        this.title = title;
        this.status_thumbnail = status_thumbnail;
        this.user_id = user_id;
        this.activity_type = activity_type;
        this.points = points;
        this.date = date;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus_thumbnail() {
        return status_thumbnail;
    }

    public void setStatus_thumbnail(String status_thumbnail) {
        this.status_thumbnail = status_thumbnail;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getActivity_type() {
        return activity_type;
    }

    public void setActivity_type(String activity_type) {
        this.activity_type = activity_type;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
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
}
