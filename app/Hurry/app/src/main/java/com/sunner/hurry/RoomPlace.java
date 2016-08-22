package com.sunner.hurry;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by sunner on 2015/8/28.
 */
public class RoomPlace {
    //儲存變數
    String name, timeDescript;
    double x, y;
    TimePair openTime, closeTime;

    //空建構式
    public RoomPlace() {
        x = y = 0;
        name = "none";
        timeDescript = "none";
        openTime = new TimePair();
        closeTime = new TimePair();
    }

    //指定座標建構式
    public RoomPlace(double x, double y) {
        this.x = x;
        this.y = y;
        name = "none";
        timeDescript = "none";
        openTime = new TimePair();
        closeTime = new TimePair();
    }

    //指定座標建構式(LagLng)
    public RoomPlace(LatLng latLng){
        this.x = latLng.latitude;
        this.y = latLng.longitude;
        name = "none";
        timeDescript = "none";
        openTime = new TimePair();
        closeTime = new TimePair();
    }

    //指定座標建構式(location)
    public RoomPlace(Location location){
        this.x = location.getLatitude();
        this.y = location.getLongitude();
        name = "none";
        timeDescript = "none";
        openTime = new TimePair();
        closeTime = new TimePair();
    }

    //簡易建構式
    public RoomPlace(String name, double x, double y) {
        this.x = x;
        this.y = y;
        this.name = name;
        timeDescript = "none";
        openTime = new TimePair();
        closeTime = new TimePair();
    }

    //指定時間詳細建構式(傳入string)
    public RoomPlace(String name, double x, double y, String timeDescript) {
        this.x = x;
        this.y = y;
        this.name = name;

        //整理時間敘述
        setTimeDescript(timeDescript);
        ArrayList<TimePair> list = timeDescriptToTimePair(timeDescript);
        openTime = list.get(0);
        closeTime = list.get(1);
    }

    //double簡易建構式
    public RoomPlace(String name, float x, float y) {
        this.x = (double) (x);
        this.y = (double) (y);
        this.name = name;
        timeDescript = "none";
        openTime = new TimePair();
        closeTime = new TimePair();
    }

    //指定時間詳細建構式(傳入string)
    public RoomPlace(String name, float x, float y, String timeDescript) {
        this.x = (double) x;
        this.y = (double) y;
        this.name = name;

        //整理時間敘述
        setTimeDescript(timeDescript);
        ArrayList<TimePair> list = timeDescriptToTimePair(timeDescript);
        openTime = list.get(0);
        closeTime = list.get(1);
    }

    //分解時間描述式
    public ArrayList<TimePair> timeDescriptToTimePair(String timeDescript) {
        //初始化尋找變數
        TimePair t = null;
        ArrayList<TimePair> list = new ArrayList<TimePair>();

        int firstColon = 0, secondColon = 0;

        //找出第一個"："號
        for (; firstColon < timeDescript.length(); firstColon++) {
            if (timeDescript.charAt(firstColon) == ':')
                break;
        }

        //找出第二個"："號
        for (secondColon = firstColon + 1; secondColon < timeDescript.length(); secondColon++) {
            if (timeDescript.charAt(secondColon) == ':')
                break;
        }

        //整理出第一個時間配對
        int hour1 = Character.getNumericValue(timeDescript.charAt(firstColon - 2)) * 10
                + Character.getNumericValue(timeDescript.charAt(firstColon - 1));
        int minute1 = Character.getNumericValue(timeDescript.charAt(firstColon + 1)) * 10
                + Character.getNumericValue(timeDescript.charAt(firstColon + 2));

        //整理出第二個時間配對
        int hour2 = Character.getNumericValue(timeDescript.charAt(secondColon - 2)) * 10
                + Character.getNumericValue(timeDescript.charAt(secondColon - 1));
        int minute2 = Character.getNumericValue(timeDescript.charAt(secondColon + 1)) * 10
                + Character.getNumericValue(timeDescript.charAt(secondColon + 2));

        //整理成回傳陣列
        t = new TimePair(hour1, minute1);
        list.add(t);
        t = new TimePair(hour2, minute2);
        list.add(t);

        return list;
    }

    //獲取
    public String getName() {
        return name;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public LatLng getPoint() {
        return new LatLng(x, y);
    }

    public String getTimeDescript() {
        return timeDescript;
    }

    public TimePair getOpenTime() {
        return openTime;
    }

    public TimePair getCloseTime() {
        return closeTime;
    }

    //指定
    public void setName(String name) {
        this.name = name;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setTimeDescript(String timeDescript) {
        this.timeDescript = timeDescript;

        //整理int
        ArrayList<TimePair> list = timeDescriptToTimePair(timeDescript);
        openTime = list.get(0);
        closeTime = list.get(1);
    }
}
