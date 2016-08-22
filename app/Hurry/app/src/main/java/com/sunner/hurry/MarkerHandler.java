package com.sunner.hurry;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunner on 2015/9/27.
 */
public class MarkerHandler {
    //google map物件
    private GoogleMap mMap;

    //資料庫物件
    RoomPlaceDataBaseHandler rHandler;

    //Context
    Context context;

    //導航處理物件
    DirectionFileProcess mDirectionFileProcess = null;

    //bundle key
    public static final String ADDRESS_CODE = "整份PHP檔案";

    //處理文件
    Handler textHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            //建立網頁處理物件
            mDirectionFileProcess = new DirectionFileProcess(msg.getData().getString(ADDRESS_CODE));

            //處理完後開始畫線
            drawDirection();
        }
    };

    //儲存所有polyline(方便清除)
    public static ArrayList<Polyline> polylines = new ArrayList<Polyline>();

    //建構式
    public MarkerHandler(GoogleMap map, RoomPlaceDataBaseHandler handler, Context c) {
        this.rHandler = handler;
        this.mMap = map;
        this.context = c;

        //加入marker
        addMarker();
    }

    /*
        定義加入marker的操作
     */
    public void addMarker() {
        List<RoomPlace> list = new ArrayList<>();

        //找出有開店的所有地點
        //list = getAllRoomPlaceInRange();

        list = getAllRoomPlaceIfOpen();

        //加入符合的地點
        addMarkerForSpecificList(list);

        //找出最近的地點
        RoomPlace near = null;
        if (MapsActivity.myLocation != null) {
            near = getNearPlaceInList(list, new RoomPlace(MapsActivity.myLocation));
        } else {
            near = getNearPlaceInList(list, new RoomPlace(Constants.testcoor3));
        }


        //開新線程處理繪製程序
        if (MapsActivity.myLocation != null)
            newThreadDirectionProcess(near, new RoomPlace(MapsActivity.myLocation));
        else
            newThreadDirectionProcess(near, new RoomPlace(Constants.testcoor3));
    }

    //定義加入所有marker
    public void addAllMarker() {
        //獲取所有麥當勞地點
        addMarkerForSpecificTable(TableSchema.TABLE_MCDONALD);

        //獲取所有7-11地點
        addMarkerForSpecificTable(TableSchema.TABLE_SEVEN);

        //獲取所有KFC地點
        addMarkerForSpecificTable(TableSchema.TABLE_KFC);

        //獲取所有台塑地點
        addMarkerForSpecificTable(TableSchema.TABLE_GAS_TI);

        //獲取所有中油地點
        addMarkerForSpecificTable(TableSchema.TABLE_GAS);

        //獲取所有百貨公司地點
        addMarkerForSpecificTable(TableSchema.TABLE_DEPARTMENT);

        //獲取所有捷運地點
        addMarkerForSpecificTable(TableSchema.TABLE_MRT);

    }

    //定義加入特定一個資料表之maker
    public void addMarkerForSpecificTable(String tableName) {
        List<RoomPlace> list = rHandler.getAllRoomPlace(tableName);
        for (RoomPlace roomPlace : list)
            mMap.addMarker(new MarkerOptions().
                    position(new LatLng(roomPlace.getX(), roomPlace.getY())));
    }

    //定義加入特定一個list之maker
    public void addMarkerForSpecificList(List<RoomPlace> list) {
        for (RoomPlace roomPlace : list)
            mMap.addMarker(new MarkerOptions().
                    position(new LatLng(roomPlace.getX(), roomPlace.getY())));
    }

    //定義加入所有表中符合特定範圍的地點
    public List<RoomPlace> getAllRoomPlaceInRange() {
        List<RoomPlace> listSum = new ArrayList<>();
        for (int i = 0; i < TableSchema.TABLE_ARRAY.length; i++) {
            List<RoomPlace> list = rHandler.getAllRoomPlaceInRange_1table(TableSchema.TABLE_ARRAY[i], 22.631386, 120.301951);
            listSum.addAll(list);
        }
        return listSum;
    }

    //定義加入所有表中有開店的地點
    public List<RoomPlace> getAllRoomPlaceIfOpen() {
        List<RoomPlace> listSum = new ArrayList<>();
        for (int i = 0; i < TableSchema.TABLE_ARRAY.length; i++) {
            List<RoomPlace> list = rHandler.getAllRoomPlaceIfOpen_1table(TableSchema.TABLE_ARRAY[i]);
            listSum.addAll(list);
        }
        return listSum;
    }

    //定義找出最近的點
    public RoomPlace getNearPlaceInList(List<RoomPlace> list, RoomPlace here) {
        double enlargeX = (here.getX() - list.get(0).getX()) * 1000;
        double enlargeY = (here.getY() - list.get(0).getY()) * 1000;
        double distance = Math.pow(enlargeX, 2) + Math.pow(enlargeY, 2);
        RoomPlace minPlace = list.get(0);

        //掃過整個list檢查
        for (RoomPlace r : list) {
            //放大調整
            enlargeX = (here.getX() - r.getX()) * 1000;
            enlargeY = (here.getY() - r.getY()) * 1000;

            /*
            Log.v(Constants.MARKER_HANDLER_LOG, "原始座標：(" + r.getX() + " , " + r.getY() + ")"
                    + "放大調整，(" + enlargeX + " , " + enlargeY + ")");
            */

            //判斷是否更小
            if (Math.pow(enlargeX, 2) + Math.pow(enlargeY, 2) < distance) {
                minPlace = r;
                distance = Math.pow(enlargeX, 2) + Math.pow(enlargeY, 2);
                //Log.v(Constants.MARKER_HANDLER_LOG, "替代，距離為：" + distance);
            }
        }
        //Log.v(Constants.MARKER_HANDLER_LOG, "最短距離為：" + distance);

        if (distance < 2)
            Toast.makeText(context, "您的附近即有廁所\n將不繪製導航路線", Toast.LENGTH_LONG).show();

        return minPlace;
    }

    //定義開新線程處理繪製程序
    public void newThreadDirectionProcess(final RoomPlace roomPlace, final RoomPlace here) {
        Thread httpThread = new Thread() {
            @Override
            public void run() {
                super.run();
                String string = request("http://maps.googleapis.com/maps/api/directions/xml?"
                        + "origin="
                        + roomPlace.getX() + "," + roomPlace.getY()
                        + "&destination="
                        + here.getX() + "," + here.getY()
                        + "&sensor=false&units=metric&mode=" + "walking").toString();
                //Log.v("冗長", string);

                //將所有訊息送至handler處理
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putString(ADDRESS_CODE, string);
                message.setData(bundle);
                textHandler.sendMessage(message);
            }
        };
        httpThread.start();
    }

    //http://stackoverflow.com/questions/29294479/android-deprecated-apache-module-httpclient-httpresponse-etc
    private StringBuffer request(String urlString) {
        // TODO Auto-generated method stub

        //初始化物件
        StringBuffer chaine = new StringBuffer("");

        try {
            //對PHP伺服器發出連線請求
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "");
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.connect();

            //獲取連線串流
            InputStream inputStream = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";

            //讀取回應訊息
            while ((line = rd.readLine()) != null) {
                chaine.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return chaine;
    }

    //定義繪製路徑
    public void drawDirection() {
        if (mDirectionFileProcess == null)
            Log.e(Constants.MARKER_HANDLER_LOG, "導航資料處理物件為空");
        else {
            for (int i = 0; i < mDirectionFileProcess.directionPointList.size() - 1; i++) {
                Polyline polyline = mMap.addPolyline(
                        new PolylineOptions()
                                .add(mDirectionFileProcess.directionPointList.get(i), mDirectionFileProcess.directionPointList.get(i + 1))
                                .width(5)
                                .color(Color.GREEN)
                );
                polylines.add(polyline);
            }
        }
    }
}
