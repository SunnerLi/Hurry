package com.sunner.hurry;

/*
    若有新增資料表，請新增對應資料表名稱、資料表陣列和來源表陣列
 */
public class TableSchema {
    //資料表名稱
    public static final String TABLE_MRT = "MRT ";
    public static final String TABLE_DEPARTMENT = "department ";
    public static final String TABLE_GAS = "gas_station ";
    public static final String TABLE_GAS_TI = "gas_station_ti ";
    public static final String TABLE_KFC = "KFC ";
    public static final String TABLE_MCDONALD = "mcdonald ";
    public static final String TABLE_SEVEN = "seven ";

    //資料表陣列
    public static final String[] TABLE_ARRAY = {
            TABLE_MRT,
            TABLE_DEPARTMENT,
            TABLE_GAS,
            TABLE_GAS_TI,
            TABLE_KFC,
            TABLE_MCDONALD,
            TABLE_SEVEN
    };

    //來源表陣列
    public static final int[] SOURCE_ARRAY = {
            R.raw.kaohsiung_metro,
            R.raw.department,
            R.raw.gas_station,
            R.raw.gas_station_ti,
            R.raw.kfc,
            R.raw.mcdonald,
            R.raw.seven
    };

    //MRT資料表名子和欄位名稱
    public static final String KEY_NAME = "name";
    public static final String KEY_X = "x";
    public static final String KEY_Y = "y";
    public static final String TIME = "time_descript";
}
