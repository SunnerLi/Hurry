package com.sunner.hurry;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class broadcastService extends Service {
    public broadcastService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //變數紀錄是否已開啟MapsActivity(若已開啟，則不重複啟動MapsActivity)
    public static boolean hadLaunchMapsActivity = false;

    //接收器物件
    public GoogleMapLaunchReceiver mReceiver = new GoogleMapLaunchReceiver();

    //實作建立廣播接收器之細節
    public void buildBroadcastFilter() {
        IntentFilter mFilter01 = new IntentFilter(Constants.broadcastServiceFilter);
        mReceiver = new GoogleMapLaunchReceiver(); //←實作一個BroadcastReceiver來篩選
        registerReceiver(mReceiver, mFilter01);
        //Log.i(Constants.LOG_BROADCASTSERVICE, "註冊廣播接收器成功");
    }

    //實作廣播接收器和override method(onReceive)
    private class GoogleMapLaunchReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //如果收到broadcastServiceFilter之過慮器訊息，則執行
            Log.i(Constants.BROADCASTSERVICE_LOG, "背景觸發接收訊息");

            if (intent.getExtras().getString(Constants.BELT_MSG_TYPE).equals(Constants.HURRY_MSG)) {
                if (hadLaunchMapsActivity == false) {
                    hadLaunchMapsActivity = true;

                    //啟動導航Activity
                    Intent i = new Intent();
                    i.setClassName(Constants.packageWholeName, Constants.googleActivitywholeName);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                }
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //註冊廣播接收器
        buildBroadcastFilter();
        Log.i(Constants.BROADCASTSERVICE_LOG, "背景觸發finish");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(Constants.BROADCASTSERVICE_LOG, "背景觸發destroy");
        unregisterReceiver(mReceiver);
    }
}
