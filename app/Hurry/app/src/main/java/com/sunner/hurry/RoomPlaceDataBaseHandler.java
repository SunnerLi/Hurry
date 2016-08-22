package com.sunner.hurry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.format.Time;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/*
    若有新增資料表，請修正onCreate函式
 */
public class RoomPlaceDataBaseHandler extends SQLiteOpenHelper {
    //資料庫版本號碼
    private static final int DATABASE_VERSION = 1;

    //資料庫名稱
    public static final String DATABASE_NAME = "toiletDatabase";


    //建構式
    public RoomPlaceDataBaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    //預設建構式
    public RoomPlaceDataBaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //新建所有表單
        createTable(db, TableSchema.TABLE_MRT);
        createTable(db, TableSchema.TABLE_DEPARTMENT);
        createTable(db, TableSchema.TABLE_GAS);
        createTable(db, TableSchema.TABLE_GAS_TI);
        createTable(db, TableSchema.TABLE_KFC);
        createTable(db, TableSchema.TABLE_MCDONALD);
        createTable(db, TableSchema.TABLE_SEVEN);
    }

    //新增特定名稱表單
    public void createTable(SQLiteDatabase db, String tableName) {
        //新增MRT表單
        String CREATE_COMMAND = "CREATE TABLE "
                + tableName + " ( "
                + TableSchema.KEY_NAME + " TEXT, "
                + TableSchema.KEY_X + " REAL NOT NULL, "
                + TableSchema.KEY_Y + " REAL NOT NULL, "
                + TableSchema.TIME + " REAL NOT NULL " + " ) ";
        db.execSQL(CREATE_COMMAND);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //尚未實做更新
    }

    //新增新項目
    public void addNewRoom(RoomPlace roomPlace, String tableName) {
        //獲取更改權
        SQLiteDatabase db = this.getWritableDatabase();

        //檢查是否已存在，若存在則刪除
        if (ifRoomExist(roomPlace, tableName)) {
            deleteRoomPlace(roomPlace, tableName);
        }

        //根據不同資料表名稱配置插入物件
        ContentValues contentValues = new ContentValues();
        contentValues.put(TableSchema.KEY_NAME, roomPlace.getName());
        contentValues.put(TableSchema.KEY_X, roomPlace.getX());
        contentValues.put(TableSchema.KEY_Y, roomPlace.getY());
        contentValues.put(TableSchema.TIME, roomPlace.getTimeDescript());

        db.insert(tableName, null, contentValues);
        db.close();
    }


    public boolean ifRoomExist(RoomPlace roomPlace, String tableName) {
        RoomPlace r = null;
        r = getRoomPlace(roomPlace.getName(), tableName);
        if (r != null) {
            Log.v(Constants.DATABASE_HANDLER_LOG, "資料已存在");
            return true;
        }
        Log.i(Constants.DATABASE_HANDLER_LOG, "資料未存在");
        return false;
    }

    //藉由廁所名子和資料表名稱獲取完整廁所資料
    public RoomPlace getRoomPlace(String name, String tableName) {
        //初始化物件
        Cursor cursor = null;

        //獲取讀取權
        SQLiteDatabase db = this.getReadableDatabase();

        try {
            //判斷資料表，並建構詢問物件
            cursor = db.rawQuery("select * from "
                    + tableName
                    + " where "
                    + TableSchema.KEY_NAME
                    + " = ? ", new String[]{name});

            //如果游標有找到符合之row
            if (cursor != null) {
                //移到第一個候選資料之位置
                cursor.moveToFirst();

                //如果第一個候選資料之index仍為-1，則判為錯誤
                if (cursor.isBeforeFirst()) {
                    Log.i(Constants.DATABASE_HANDLER_LOG, "Cursor指標在第一個前面...");
                    return null;
                } else {

                    //創建一個新的RoomPlace物件來乘載這些數值並回傳
                    RoomPlace roomPlace = new RoomPlace(
                            cursor.getString(0),
                            Float.parseFloat(cursor.getString(1)),
                            Float.parseFloat(cursor.getString(2)),
                            cursor.getString(3));
                    return roomPlace;
                }
            } else {
                Log.e(Constants.DATABASE_HANDLER_LOG, "Cursor為空...");
                return null;
            }
        } finally {
            // 最後一定要關閉游標
            if (cursor != null)
                cursor.close();
        }
    }

    //回傳所有row
    public List<RoomPlace> getAllRoomPlace(String name) {
        List<RoomPlace> roomPlaceList = new ArrayList<RoomPlace>();

        //根據資料表名稱決定查詢目標
        String command = "SELECT * FROM " + name;

        //獲取更改權
        SQLiteDatabase db = this.getReadableDatabase();

        //一次query一整個row
        Cursor cursor = db.rawQuery(command, null);

        //紀錄所有row資訊
        if (cursor.moveToFirst()) {
            do {
                RoomPlace roomPlace = new RoomPlace();
                roomPlace.setName(cursor.getString(0));
                roomPlace.setX(Float.parseFloat(cursor.getString(1)));
                roomPlace.setY(Float.parseFloat(cursor.getString(2)));
                roomPlace.setTimeDescript(cursor.getString(3));

                //加入list中
                roomPlaceList.add(roomPlace);
            } while (cursor.moveToNext());
        }

        return roomPlaceList;
    }

    //回傳所有範圍內之row
    /*
        ref:
        1. http://stackoverflow.com/questions/20838233/sqliteexception-unrecognized-token-when-reading-from-database
        2. http://kxitnote.blogspot.tw/2011/10/sql.html
        3. https://www.sqlite.org/cli.html
     */
    public List<RoomPlace> getAllRoomPlaceInRange_1table(String tableName, double xCor, double yCor) {
        //正常範圍參數
        //double xRange = 0.002;
        //double yRange = 0.001;

        //測試範圍參數
        double xRange = 0.01;
        double yRange = 0.01;

        List<RoomPlace> roomPlaceList = new ArrayList<RoomPlace>();

        //查詢特定資料表符合的資料
        String command = "SELECT * FROM " + tableName
                + " where " + TableSchema.KEY_X + " > ? "
                + " and " + TableSchema.KEY_X + " < ? "
                + " and " + TableSchema.KEY_Y + " > ? "
                + " and " + TableSchema.KEY_Y + " < ? ";

        //獲取更改權
        SQLiteDatabase db = this.getReadableDatabase();

        //一次query一整個row
        Cursor cursor = db.rawQuery(command,
                new String[]{Double.toString(xCor - xRange),
                        Double.toString(xCor + xRange),
                        Double.toString(yCor - yRange),
                        Double.toString(yCor + yRange)});

        //紀錄所有row資訊
        if (cursor.moveToFirst()) {
            do {
                RoomPlace roomPlace = new RoomPlace();
                roomPlace.setName(cursor.getString(0));
                roomPlace.setX(Float.parseFloat(cursor.getString(1)));
                roomPlace.setY(Float.parseFloat(cursor.getString(2)));
                roomPlace.setTimeDescript(cursor.getString(3));

                //加入list中
                roomPlaceList.add(roomPlace);
            } while (cursor.moveToNext());
        }

        return roomPlaceList;
    }

    //回傳有開店的地點
    public List<RoomPlace> getAllRoomPlaceIfOpen_1table(String tableName) {
        //獲取目前時間
        Time time = new Time(Time.getCurrentTimezone());
        time.setToNow();
        int hour = time.hour;
        int minute = time.minute;

        //測試時間
        //int hour = 1;
        //int minute = 0;

        List<RoomPlace> roomPlaceList = new ArrayList<RoomPlace>();

        //根據資料表名稱決定查詢目標
        String command = "SELECT * FROM " + tableName;

        //獲取更改權
        SQLiteDatabase db = this.getReadableDatabase();

        //一次query一整個row
        Cursor cursor = db.rawQuery(command, null);

        //紀錄所有row資訊
        if (cursor.moveToFirst()) {
            do {
                //先將時間分析出來
                RoomPlace roomPlace = new RoomPlace();
                roomPlace.setTimeDescript(cursor.getString(3));

                //判斷是否在開店時間內
                if (((hour > roomPlace.getOpenTime().getHour()) || (hour == roomPlace.getOpenTime().getHour() && minute >= roomPlace.getOpenTime().getMinute()))
                        &&
                        ((hour < roomPlace.getCloseTime().getHour()) || (hour == roomPlace.getCloseTime().getHour() && minute <= roomPlace.getCloseTime().getMinute()))
                        ) {
                    //補上其他資訊
                    roomPlace.setName(cursor.getString(0));
                    roomPlace.setX(Float.parseFloat(cursor.getString(1)));
                    roomPlace.setY(Float.parseFloat(cursor.getString(2)));

                    //加入list中
                    roomPlaceList.add(roomPlace);
                    /*
                    Log.v(Constants.DATABASE_HANDLER_LOG,
                            "\n目前時間：" + hour + ":" + minute
                                    + "\n開店時間：" + roomPlace.getOpenTime().getHour() + ":" + roomPlace.getOpenTime().getMinute()
                                    + "\n關閉時間：" + roomPlace.getCloseTime().getHour() + ":" + roomPlace.getCloseTime().getMinute()
                                    + "\n可以光顧！");
                    */
                } else {
                    /*
                    Log.v(Constants.DATABASE_HANDLER_LOG,
                            "\n目前時間：" + hour + ":" + minute
                                    + "\n開店時間：" + roomPlace.getOpenTime().getHour() + ":" + roomPlace.getOpenTime().getMinute()
                                    + "\n關閉時間：" + roomPlace.getCloseTime().getHour() + ":" + roomPlace.getCloseTime().getMinute()
                                    + "\n請離開！");
                    */
                }
            }
            while (cursor.moveToNext());
        }

        return roomPlaceList;
    }

    //回傳所有範圍內之row並列出(測試用)
    public List<RoomPlace> ListAllRoomPlaceInRange_1table(String tableName, double xCor,
                                                          double yCor) {
        List<RoomPlace> list = getAllRoomPlaceInRange_1table(tableName, xCor, yCor);
        Log.i(Constants.DATABASE_HANDLER_LOG, "範圍中的地點：\n");
        for (RoomPlace r : list) {
            String result = "地點名稱： " + r.getName();
            result += "\nX 座標：" + r.getX();
            result += "\nY 座標：" + r.getY();
            result += "\n開門時間：" + r.getOpenTime().getHour() + " : " + r.getOpenTime().getMinute();
            result += "\n關閉時間：" + r.getCloseTime().getHour() + " : " + r.getCloseTime().getMinute();
            Log.i(Constants.DATABASE_HANDLER_LOG, result);
        }
        return list;
    }

    //刪除一個row(傳入RoomPlace物件)

    public void deleteRoomPlace(RoomPlace roomPlace, String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();

        //SQLite刪除指令
        db.delete(tableName, TableSchema.KEY_NAME + " = ? ",
                new String[]{roomPlace.getName()});
    }

    //刪除整個table
    public void deleteAll(String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();

        Log.i(Constants.DATABASE_HANDLER_LOG,
                "\ntableName:" + tableName + "\norigin:" + tableName);

        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + tableName);

        // Create tables again
        onCreate(db);
    }
}
