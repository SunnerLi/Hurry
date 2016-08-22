package com.sunner.hurry;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.LinkedList;

/**
 * Created by sunner on 2015/9/11.
 */
public class SourceReader {
    //隨時變動參數
    String placeName, timeDescript;
    double placeX, placeY;

    //總和鏈結
    LinkedList<RoomPlace> linkedList = new LinkedList<RoomPlace>();

    Context mContext = null;

    //建構式
    public SourceReader(Context context) {
        mContext = context;
    }

    public LinkedList<RoomPlace> sourceProcess(int index) {
        Resources resources;    //資源檔物件
        InputStream inputStream; //輸入串流物件

        //藉由資源檔初始化輸入串流物件
        resources = mContext.getResources();

        //讀取test檔案
        inputStream = resources.openRawResource(index);
        readFile(inputStream);

        //最後必定做檔案關閉
        try {
            inputStream.close();
            Log.i("main", "檔案成功關閉");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (linkedList == null)
            Log.e(Constants.SOURCE_READER_LOG, "linked list為空");
        return linkedList;
    }

    //定義讀取檔案
    public void readFile(InputStream inputStream) {
        String s;

        //讀取檔案
        try {
            //估計有幾個字
            byte[] buffer = new byte[inputStream.available()];

            //讀取
            inputStream.read(buffer);

            //byte[]轉字串
            s = new String(buffer);

            Log.i("main", "讀取內容：" + s);

            //字串細部處理
            stringSplit(s);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //字串細部處理
    public void stringSplit(String string) {
        int emptyPosition = 0, commaPosition = 0, lastPostion = 0, colonPostion = 0, last = 0;

        //轉換參考：http://examples.javacodegeeks.com/core-java/character/how-to-convert-character-to-string-and-a-string-to-character-array-in-java/
        char[] stringToChar = string.toCharArray();

        //多個地點迴圈處理
        for (int i = 0; i < numberOfLine(stringToChar); i++) {
            //判斷第一個空白位置
            for (emptyPosition = last; stringToChar[emptyPosition] != ' '; emptyPosition++) ;
            Log.v(Constants.SOURCE_READER_LOG, "起始位置：" + emptyPosition);

            //決定地點
            placeName = string.substring(last, emptyPosition);

            //判斷逗號位置
            for (commaPosition = emptyPosition + 1; stringToChar[commaPosition] != ','; commaPosition++) ;
            Log.v(Constants.SOURCE_READER_LOG, "逗號位置：" + commaPosition);

            //判斷位置結尾
            for (lastPostion = emptyPosition + 1; stringToChar[lastPostion] != ' '; lastPostion++) ;
            Log.v(Constants.SOURCE_READER_LOG, "位置結尾：" + lastPostion);

            //判斷第一個時間冒號
            for (colonPostion = last; stringToChar[colonPostion] != ':'; colonPostion++) ;

            //判斷換行位置
            for (last = last + 1; stringToChar[last] != '\n'; last++) ;

            //決定座標
            placeX = Double.valueOf(string.substring(emptyPosition + 1, commaPosition));
            placeY = Double.valueOf(string.substring(commaPosition + 1, lastPostion));

            //決定時間描述
            timeDescript = string.substring(lastPostion + 1, last);

            Log.v(Constants.SOURCE_READER_LOG, "\n地點:" + placeName + "\n座標：" + " ( " + placeX + " , " + placeY + " ) ");
            Log.v(Constants.SOURCE_READER_LOG, "\n時間描述:" + timeDescript);
            Log.v(Constants.SOURCE_READER_LOG, "最大長度為：" + last++);

            /*
                取經準度
                ref: http://bioankeyang.blogspot.tw/2014/03/jjava.html
             */
            placeX = new BigDecimal(placeX)
                    .setScale(6, BigDecimal.ROUND_HALF_UP)
                    .doubleValue();
            placeY = new BigDecimal(placeY)
                    .setScale(6, BigDecimal.ROUND_HALF_UP)
                    .doubleValue();

            linkedList.add(new RoomPlace(placeName, placeX, placeY, timeDescript));

        }
    }

    //判斷有幾行
    public int numberOfLine(char[] chars) {
        int i = 0, n = 0;
        for (; i < chars.length; i++) {
            if (chars[i] == '\n')
                n++;
        }
        return n;
    }
}
