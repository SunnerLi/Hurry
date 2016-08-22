package com.sunner.hurry;

/*
    若有新增資料表，請修正addAllMarker函式
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

/*
    使用時務必開啟GPS或LTE，
    若皆關閉則會抓取上次位置。
    若距離目的太遠，google位置導航則會出錯。
 */

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    //資料庫物件
    RoomPlaceDataBaseHandler rHandler = new RoomPlaceDataBaseHandler(this);

    //標記操縱者物件
    MarkerHandler markerHandler;

    private HurryLocationListener hurryLocationListener;
    private LocationManager locationManager;
    public static Location myLocation = null;

    //廣播接收器物件
    private BeltReceiver beltReceiver = new BeltReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //初始化位置(取得上次位置)
        getLastPosition();

        //地圖處理
        setUpMapIfNeeded();

        //註冊廣播監聽器
        buildBroadcastFilter();
    }

    //定義上次位置取得
    public void getLastPosition() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        hurryLocationListener = new HurryLocationListener();

        //透過網路AP和GPS取得位置
        myLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (myLocation == null)
            myLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (myLocation == null)
            Log.e(Constants.MAPS_ACTIVITY_LOG, "無法取得上次位置...");
    }

    @Override
    protected void onResume() {
        super.onResume();

        //再次建構地圖
        setUpMapIfNeeded();

        //進入找尋廁所主函式
        findToiletMainFunction();
    }

    //實作找尋廁所主函式
    public void findToiletMainFunction(){
        //更新位置
        Log.v(Constants.MAPS_ACTIVITY_LOG, "更新位置資訊");
        updatePosition();

        //建立marker操縱者在地圖上加入標記
        //包括取得廁所list、找尋最近廁所、繪製路徑。
        markerHandler = new MarkerHandler(mMap, rHandler, MapsActivity.this);
    }

    //定義更新位置
    public void updatePosition() {
        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10, 1, hurryLocationListener);
        } catch (NullPointerException e) {
            try {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 1, hurryLocationListener);
            } catch (NullPointerException ee) {
                Log.e(Constants.MAPS_ACTIVITY_LOG, "更新位置失敗...");
            }
        }
        if (myLocation != null)
            Log.v(Constants.MAPS_ACTIVITY_LOG, "位置為: ( " + myLocation.getLatitude() + " , " + myLocation.getLongitude() + " ) ");
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        //建立位置的物件座標
        LatLng place = null;

        if (myLocation == null)
            place = new LatLng(Constants.testcoor2.latitude, Constants.testcoor2.longitude);
        else
            place = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());

        //移動地圖
        moveMap(place);
    }

    //定義移動地圖
    public void moveMap(LatLng place) {
        //建立地圖攝影機的位置物件
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(place)
                .zoom(17)
                .build();

        //使用動畫效果移動地圖
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.database_update:
                Intent intent1 = new Intent();
                intent1.setClass(MapsActivity.this, DataBaseUpdateActivity.class);
                startActivity(intent1);
                return true;
            case R.id.reload:
                reviseMap();
                return true;
            case R.id.direction:
                Intent intent2 = new Intent();
                intent2.setClass(MapsActivity.this, DirectionActivity.class);
                startActivity(intent2);
            case R.id.exit:
                MapsActivity.this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    //定義重新整理頁面
    public void reviseMap() {
        //清除線條
        if (MarkerHandler.polylines.size() != 0) {
            for (Polyline p : MarkerHandler.polylines)
                p.remove();
            MarkerHandler.polylines.clear();
        }

        //重新定位與繪製路徑
        /*
        updatePosition();
        setUpMap();
        */
        findToiletMainFunction();
        setUpMap();
    }

    //註冊廣播接收器
    public void buildBroadcastFilter() {
        IntentFilter beltFilter = new IntentFilter(Constants.broadcastServiceFilter);
        beltReceiver = new BeltReceiver(); //←實作一個BroadcastReceiver來篩選
        registerReceiver(beltReceiver, beltFilter);
        //Log.i(Constants.LOG_BROADCASTSERVICE, "註冊廣播接收器成功");
    }

    //實作廣播接收器，可結束hurry狀態或重新定位
    private class BeltReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //如果接收到belt filter進來的廣播，則判斷是哪種指令
            Log.i(Constants.MAPS_ACTIVITY_LOG, "背景觸發接收訊息");

            if (intent.getExtras().getString(Constants.BELT_MSG_TYPE).equals(Constants.HURRY_MSG))
                reviseMap();
            if (intent.getExtras().getString(Constants.BELT_MSG_TYPE).equals(Constants.END_MSG)) {
                broadcastService.hadLaunchMapsActivity = false;
                MapsActivity.this.finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(beltReceiver);
    }
}
