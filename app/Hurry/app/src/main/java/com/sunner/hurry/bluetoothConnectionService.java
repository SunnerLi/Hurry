package com.sunner.hurry;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class bluetoothConnectionService extends Service {
    public bluetoothConnectionService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    //藍牙相關物件
    public SunnerBluetoothManager sunnerBluetoothManager = null;
    public BluetoothAdapter bluetoothAdapter;

    //連接狀態
    public boolean isConnect = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        onResume();
        return START_STICKY;
    }

    //自行定義的藍牙創建過程
    public void onResume() {
        //初始化藍牙
        bluetoothInitialize();

        //和腰帶連接
        bluetoothConnect();

        //如果連接成功，開始接收訊息
        if (isConnect) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    super.run();
                    recvMsg();

                    //結束接收訊息(斷線)，重新連接
                    sunnerBluetoothManager.stop();
                    sunnerBluetoothManager = null;
                    bluetoothAdapter = null;
                    isConnect = false;
                    onResume();
                }
            };
            thread.start();
        }
    }

    //實作藍牙初始化
    public void bluetoothInitialize() {
        bluetoothAdapter = WaitActivity.bluetoothAdapter;
        sunnerBluetoothManager = new SunnerBluetoothManager();
    }

    //實作藍牙連接過程
    public void bluetoothConnect() {
        //如果多次連接失敗，提醒使用者重啟藍牙(最多5次)
        for (int i = 0; i < Constants.BT_RECONNNECT_LIMIT; i++) {
            int connectStatus = sunnerBluetoothManager.BtConnect(bluetoothAdapter);
            if (connectStatus >= 0) {
                isConnect = true;
                break;
            } else if (i == Constants.BT_RECONNNECT_LIMIT - 1) {
                //廣播強制關閉service
                Toast.makeText(this, "藍牙多次連線失敗，請重啟藍牙。", Toast.LENGTH_LONG).show();
                broadcastForceEnd();
            }

            //等待1秒後重新連接
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    //實作接收藍牙訊息
    public void recvMsg() {
        while (true) {
            if (sunnerBluetoothManager.read() < 0) {
                isConnect = false;
                break;
            }

            //判斷是否為智慧型腰帶信令
            if (sunnerBluetoothManager.msg_Read.equals(Constants.BELT_HURRY_MSG)) {
                //接收並廣播智慧型腰帶尿急信令
                Log.v(Constants.BTCONNECTSERVICE_LOG, "start to broadcast hurry!");
                broadcastHurry();
            } else if (sunnerBluetoothManager.msg_Read.equals(Constants.BELT_END_MSG)) {
                //接收並廣播智慧型腰帶結束信令
                Log.v(Constants.BTCONNECTSERVICE_LOG, "start to broadcast end...");
                broadcastEnd();
            } else {
                Log.v(Constants.BTCONNECTSERVICE_LOG, "接收其他訊息: " + sunnerBluetoothManager.msg_Read);
            }
        }
    }

    //實作廣播智慧型腰帶尿急信令
    public void broadcastHurry() {
        Intent intent = new Intent();
        intent.putExtra(Constants.BELT_MSG_TYPE, Constants.HURRY_MSG);
        intent.setAction(Constants.broadcastServiceFilter);
        sendBroadcast(intent);
    }

    //實作廣播智慧型腰帶結束信令
    public void broadcastEnd() {
        Intent intent = new Intent();
        intent.putExtra(Constants.BELT_MSG_TYPE, Constants.END_MSG);
        intent.setAction(Constants.broadcastServiceFilter);
        sendBroadcast(intent);
    }

    //實作廣播多次連接失敗強制關閉信令
    public void broadcastForceEnd() {
        Intent intent = new Intent();
        intent.setAction(Constants.forceEndFilter);
        sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (sunnerBluetoothManager!=null)
            sunnerBluetoothManager.stop();
        sunnerBluetoothManager = null;
    }
}
