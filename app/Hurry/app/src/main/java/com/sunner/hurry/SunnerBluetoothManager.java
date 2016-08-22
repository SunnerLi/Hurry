package com.sunner.hurry;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;

/**
 * Created by sunner on 2015/11/26.
 */
public class SunnerBluetoothManager {
    //bluetooth object
    public BluetoothSocket socket = null;
    public BluetoothDevice device = null;

    //read or write
    public byte[] read_Msg_Byte = {0};
    public String msg_Read = "";

    //main function of manager
    public int BtConnect(BluetoothAdapter a) {
        a.startDiscovery();

        // 讀取socket連線創建
        try {
            socket = a.getRemoteDevice(Constants.belt_Address)
                    .createInsecureRfcommSocketToServiceRecord(Constants.UUID1);

            if (socket == null)
                Log.e(Constants.SUNNERMANAGER_LOG, "socket為空");
            else
                Log.i(Constants.SUNNERMANAGER_LOG, "socket不為空");
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }

        //開始連線
        a.cancelDiscovery();
        try {
            socket.connect();
            Log.i(Constants.SUNNERMANAGER_LOG, "連線成功！");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(Constants.SUNNERMANAGER_LOG, "連線失敗...");
            return -1;
        }
        return 0;
    }

    //Implement reading the data
    public int read() {
        read_Msg_Byte = new byte[5];

        try {
            socket.getInputStream().read(read_Msg_Byte);
            msg_Read = new String(read_Msg_Byte);
            Log.v(Constants.SUNNERMANAGER_LOG, "藍牙讀取: " + msg_Read);
        } catch (IOException e) {
            Log.e(Constants.SUNNERMANAGER_LOG, "讀取失敗...");
            return -1;
        }
        return 0;
    }

    //Implement stopping the bluetooth
    public int stop() {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                return -1;
            }
        }
        return 0;
    }
}