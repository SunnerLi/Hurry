package com.sunner.hurry;

import com.google.android.gms.maps.model.LatLng;

import java.util.UUID;

/**
 * Created by sunner on 2015/11/26.
 */
public class Constants {
    //log
    public static final String BROADCASTSERVICE_LOG = "背景觸發log";
    public static final String SUNNERMANAGER_LOG = "Sunner BT manager之log";
    public static final String BTCONNECTSERVICE_LOG = "藍牙背景log";
    public static final String MAPS_ACTIVITY_LOG = "地圖log";
    public static final String DATABASE_HANDLER_LOG = "資料庫log";
    public static final String UPDATE_ACTIVITY_LOG = "更新Activity之log";
    public static final String SOURCE_READER_LOG = "讀取文字檔之log";
    public static final String ROOMPLACE_LOG = "roomPlace物件之log";
    public static final String DIRECTION_PROCESS_LOG = "導航處理log";
    public static final String MARKER_HANDLER_LOG="處理標記之log";

    //透過廣播接收器直接啟動Activity相關名子(boradcastService使用)
    public static final String packageWholeName = "com.sunner.hurry";
    public static final String googleActivitywholeName = "com.sunner.hurry.MapsActivity";

    //廣播接收器之過濾器
    public static final String broadcastServiceFilter = "com.sunner.hurry.BTfilter";
    public static final String forceEndFilter = "com.sunner.hurry.forceEnd";

    //藍牙位址
        //public static final String belt_Address = "48:51:B7:D6:62:2A";    //lenovo
        public static final String belt_Address = "00:1A:7D:DA:71:04";      //BBB

    //藍牙通用識別碼(UUID)
        //public static final UUID UUID1 = UUID.fromString("07293DB4-A323-4E07-8B8B-250B340E42A4");
        public static final UUID UUID1 = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    //腰帶廣播Bundle相關信息
    public static final String BELT_MSG_TYPE = "message type";
    public static final String HURRY_MSG = "hurry message";
    public static final String END_MSG = "release message";

    //腰帶傳送信令
    public static final String BELT_HURRY_MSG = "hurry";
    public static final String BELT_END_MSG = "enddd";

    //藍牙失敗最大重連次數
    public static final int BT_RECONNNECT_LIMIT = 5;

    //資料表個數
    public static final int numberOfTableSum = 7;

    //判斷整數或小數(DirectionFileProcess使用)
    public static final int IS_INTEGER = 0;
    public static final int IS_DOUBLE = 1;

    //尋找編號(DirectionFileProcess使用)
    public static final int FIND_START = 0;
    public static final int FIND_END = 1;

    //測試地點
    public static final LatLng testcoor1 = new LatLng(22.631386, 120.301951);
    public static final LatLng testcoor2 = new LatLng(22.629902, 120.301661);
    public static final LatLng testcoor3 = new LatLng(22.629253, 120.304873);
}
