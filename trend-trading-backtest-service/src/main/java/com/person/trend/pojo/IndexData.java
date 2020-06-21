package com.person.trend.pojo;

//指数数据
public class IndexData {

    String date;
    float closePoint;

    public IndexData() {
    }

    public IndexData(String date, float closePoint) {
        this.date = date;
        this.closePoint = closePoint;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getClosePoint() {
        return closePoint;
    }

    public void setClosePoint(float closePoint) {
        this.closePoint = closePoint;
    }

}