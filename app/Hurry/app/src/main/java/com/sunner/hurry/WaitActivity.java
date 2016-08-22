package com.sunner.hurry;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class WaitActivity extends Activity {
    //按鈕物件
    Button aboutBtn, groupBtn, superviseBtn, closeBtn, updateBtn;

    //藍牙接收器物件
    public static BluetoothAdapter bluetoothAdapter = null;

    //廣播接收器物件
    public ForceEndReceiver mReceiver = new ForceEndReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait);

        //開啟藍牙
        openBluetooth();

        //建立強制關閉廣播接收器
        buildForceEndBroadcastFilter();

        //啟動broadcastService
        ctrlBroadcastService(true);

        //啟動bluetoothConnectionService
        ctrlBluetoothConnectionService(true);

        //設定畫面物件
        setViewByIdAndListener();
    }

    //實作開關BroadcastService
    public void ctrlBroadcastService(boolean o) {
        // true為開啟，false為關閉
        if (o == true) {
            Intent intent = new Intent();
            intent.setClass(this, broadcastService.class);
            startService(intent);
        } else {
            Intent intent = new Intent();
            intent.setClass(this, broadcastService.class);
            stopService(intent);
        }
    }

    //實作開關bluetoothConnectionService
    public void ctrlBluetoothConnectionService(boolean o) {
        // true為開啟，false為關閉
        if (o == true) {
            Intent intent = new Intent();
            intent.setClass(this, bluetoothConnectionService.class);
            startService(intent);
        } else {
            Intent intent = new Intent();
            intent.setClass(this, bluetoothConnectionService.class);
            stopService(intent);
        }
    }

    //實作設定畫面物件
    public void setViewByIdAndListener() {
        closeBtn = (Button) findViewById(R.id.closeBtn);
        aboutBtn = (Button) findViewById(R.id.aboutBtn);
        groupBtn = (Button) findViewById(R.id.groupBtn);
        updateBtn = (Button) findViewById(R.id.updateBtn);
        superviseBtn = (Button) findViewById(R.id.superviseBtn);

        aboutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(WaitActivity.this, AboutHurry.class);
                startActivity(intent);
            }
        });


        groupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(WaitActivity.this, GroupActivity.class);
                startActivity(intent);
            }
        });


        superviseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveTaskToBack(false);
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ctrlBroadcastService(false);
                WaitActivity.this.finish();
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(WaitActivity.this, DataBaseUpdateActivity.class);
                startActivity(intent);
            }
        });

    }

    //實作藍牙開啟和初始化接收器
    public void openBluetooth() {
        //初始化接收器
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 0);
        }
    }

    //實作建立強制關閉廣播接收器
    public void buildForceEndBroadcastFilter() {
        IntentFilter mFilter01 = new IntentFilter(Constants.forceEndFilter);
        mReceiver = new ForceEndReceiver(); //←實作一個BroadcastReceiver來篩選
        registerReceiver(mReceiver, mFilter01);
    }

    //實作廣播接收器和override method(onReceive)
    private class ForceEndReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //如果收到強制關閉信令，則結束所有service
            ctrlBroadcastService(false);
            ctrlBluetoothConnectionService(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ctrlBluetoothConnectionService(false);
        ctrlBroadcastService(false);
        unregisterReceiver(mReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_wait, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            //啟動導航Activity
            Intent i = new Intent();
            i.setClassName(Constants.packageWholeName, Constants.googleActivitywholeName);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(i);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
