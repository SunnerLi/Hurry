package com.sunner.hurry;

/*
    若有新增資料表，請修正onStart函式中的線程
 */

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.LinkedList;
import java.util.List;

public class DataBaseUpdateActivity extends Activity {
    //dialog更新常數
    public static int UPDATE_DIALOG = 1;

    //資料庫物件
    RoomPlaceDataBaseHandler rHandler = null;
    SourceReader sourceReader = new SourceReader(this);

    //已更新多少個資料庫
    public int numberOfTableHaveUpdate = 0;

    //已完成所有更新
    public boolean isComplete = false;

    //提醒物件
    ProgressDialog pd;

    //修正提醒物件
    //ref: http://blog.xuite.net/viplab/blog/241319945-%5BAndroid%5DAlertDialog(%E5%8A%A0%E5%85%A5%E6%8C%89%E9%88%95%E3%80%81%E5%8A%A0%E5%85%A5List%E4%BB%A5%E5%8F%8A%E5%A1%9E%E5%85%A5%E4%B8%80%E5%80%8BLayout)
    public Handler reviseProgressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (pd == null)
                Log.e(Constants.UPDATE_ACTIVITY_LOG, "提醒物件為空");
            else {
                /*alertDialog.setMessage("資料庫更新進度：( "
                        + numberOfTableHaveUpdate + " / "
                        + Constants.numberOfTableSum + " )");
                */

                //更新dialog
                pd.incrementProgressBy(1);
                if (pd.getProgress() >= Constants.numberOfTableSum) {
                    pd.dismiss();
                    DataBaseUpdateActivity.this.finish();
                }

                Log.v(Constants.UPDATE_ACTIVITY_LOG, "資料庫更新進度：( "
                        + numberOfTableHaveUpdate + " / "
                        + Constants.numberOfTableSum + " )");
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_base_update);
    }

    @Override
    public void onStart() {
        super.onStart();

        //刪除整個資料庫
        this.deleteDatabase(RoomPlaceDataBaseHandler.DATABASE_NAME);
        //LinkedList<RoomPlace> list = manySourceProcess();

        //獲取handler物件
        rHandler = new RoomPlaceDataBaseHandler(this);

        //跳出一個Dialog告訴使用者更新進度
        showDialog(UPDATE_DIALOG);
    }

    public void listAll() {
        //MRT
        List<RoomPlace> list;
        list = rHandler.getAllRoomPlace(TableSchema.TABLE_MRT);

        //顯示結果
        Log.v(Constants.UPDATE_ACTIVITY_LOG, "MRT表格");
        if (list != null) {
            for (RoomPlace r : list) {
                String result = "地點名稱： " + r.getName();
                result += "\nX 座標：" + r.getX();
                result += "\nY 座標：" + r.getY();
                result += "\n開門時間：" + r.getOpenTime().getHour() + " : " + r.getOpenTime().getMinute();
                result += "\n關閉時間：" + r.getOpenTime().getHour() + " : " + r.getCloseTime().getMinute();
                Log.v(Constants.UPDATE_ACTIVITY_LOG, result);
            }
        }
    }

    //定義讀取一個文字檔且插入資料庫中
    public void oneSourceProcessAndInsert(int sourceName, String tableName) {
        LinkedList<RoomPlace> listLoad = null;
        listLoad = sourceReader.sourceProcess(sourceName);
        for (int i = 0; i < listLoad.size(); i++) {
            rHandler.addNewRoom(listLoad.get(i), tableName);
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == UPDATE_DIALOG) {
            pd = new ProgressDialog(this);

            //dialog設置
            pd.setMax(Constants.numberOfTableSum);
            pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

            //設定標題
            pd.setTitle("資料庫更新");

            //設定不能使用退回鍵關閉
            pd.setCancelable(false);
        }
        return pd;
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        super.onPrepareDialog(id, dialog);

        //進度清為0
        pd.incrementProgressBy(-pd.getProgress());

        /*
            設定1執行序處理進度問題
            新增1執行序load file + 加入資料庫
            每次需重建Message:http://www.inote.tw/android-message-already-use
        */
        new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < TableSchema.TABLE_ARRAY.length; i++) {
                    //讀取文字檔
                    Message msg1 = new Message();
                    oneSourceProcessAndInsert(TableSchema.SOURCE_ARRAY[i], TableSchema.TABLE_ARRAY[i]);
                    msg1.what = numberOfTableHaveUpdate = 1;
                    reviseProgressHandler.sendMessage(msg1);
                }

                //列出所有資料點
                listAll();
            }
        }.start();
    }
}
