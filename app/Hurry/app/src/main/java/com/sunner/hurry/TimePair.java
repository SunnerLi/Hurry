package com.sunner.hurry;

/**
 * Created by sunner on 2015/9/23.
 */
public class TimePair {
    //儲存變數
    private int hour;
    private int minute;

    public TimePair(){
        setHour(0);
        setMinute(0);
    }

    public TimePair(int hour, int minute){
        this.hour = hour;
        this.minute = minute;
    }

    //提取參數
    public int getHour(){ return hour; }
    public int getMinute(){ return minute; }

    //設定參數
    public void setHour(int hour){ this.hour = hour; }
    public void setMinute(int minute){ this.minute = minute; }
}
