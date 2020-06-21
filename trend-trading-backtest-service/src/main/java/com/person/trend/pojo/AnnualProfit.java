package com.person.trend.pojo;

//每年收益实体类
public class AnnualProfit {

    //年份
    private int year;
    //每年的指数投资收益
    private float indexIncome;
    //每年的趋势投资收益
    private float trendIncome;
    public int getYear() {
        return year;
    }
    public void setYear(int year) {
        this.year = year;
    }
    public float getIndexIncome() {
        return indexIncome;
    }
    public void setIndexIncome(float indexIncome) {
        this.indexIncome = indexIncome;
    }
    public float getTrendIncome() {
        return trendIncome;
    }
    public void setTrendIncome(float trendIncome) {
        this.trendIncome = trendIncome;
    }
    @Override
    public String toString() {
        return "AnnualProfit [year=" + year + ", indexIncome=" + indexIncome + ", trendIncome=" + trendIncome + "]";
    }
     
}