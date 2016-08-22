package com.sunner.hurry;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunner on 2015/9/27.
 */
public class DirectionFileProcess {
    //各個轉折點之list
    List<LatLng> directionPointList = new ArrayList<LatLng>();

    //總距離
    public int totalDistance = 0;

    //建構式
    public DirectionFileProcess(String whole) {
        //分解獲取所有步驟描述
        List<String> list = getStepString(whole);

        //對於每個步驟
        for (int i = 0; i < list.size(); i++) {
            //尋找起始點敘述
            String startLocationDescript = getStartLocationDescript(list.get(i));

            //獲取起始點經緯度
            LatLng startLatLng = getLatLngFromDescript(startLocationDescript);

            //加入起點
            addDirectionPoint(startLatLng);

            //尋找終點敘述
            String endLocationDescript = getEndLocationDescript(list.get(i));

            //獲取終點經緯度
            LatLng endLatLng = getLatLngFromDescript(endLocationDescript);

            //加入終點
            addDirectionPoint(endLatLng);

            //尋找距離敘述
            String distanceDescript = getDistanceDescript(list.get(i));

            //獲取距離
            int distance = getDistanceFromDescript(distanceDescript);
            totalDistance += distance;

            Log.v(Constants.DIRECTION_PROCESS_LOG, "."
                    + "\n起點: (" + startLatLng.latitude + " , " + startLatLng.longitude + " )"
                    + "\n終點: (" + endLatLng.latitude + " , " + endLatLng.longitude + " ) "
                    + "\n距離: " + distance);
        }

        //總距離陳述
        Log.v(Constants.DIRECTION_PROCESS_LOG, "總距離為: " + totalDistance);

        //處理結束
        Log.d(Constants.DIRECTION_PROCESS_LOG, "處理結束");
    }

    //定義獲取布驟描述
    public List<String> getStepString(String phpResponce) {
        //位置物件
        List<Integer> stepStartPosition = new ArrayList<Integer>();
        List<Integer> stepEndPosition = new ArrayList<Integer>();

        //敘述物件
        List<String> stepSmallList = new ArrayList<String>();

        //正在尋找開頭還是結尾
        int buf = Constants.FIND_START;

        //掃過整個string
        for (int i = 0; i < phpResponce.length(); i++) {
            //確定是否為s開頭
            if (phpResponce.charAt(i) == 's') {
                String substring;

                //試著抓取子字串
                try {
                    substring = phpResponce.substring(i, i + 4);
                } catch (StringIndexOutOfBoundsException e) {
                    break;
                }

                //確認是否為step關鍵字
                if (judgeSameString(substring, "step")) {

                    //確認為頭還是尾
                    switch (buf) {
                        //開頭
                        case Constants.FIND_START:
                            stepStartPosition.add(i + 5);
                            buf = Constants.FIND_END;
                            break;
                        case Constants.FIND_END:
                            stepEndPosition.add(i + 5);
                            buf = Constants.FIND_START;
                            break;
                        default:
                            Log.e(Constants.DIRECTION_PROCESS_LOG, "無法判斷是頭還是尾");
                    }
                }
            }
        }

        //擷取substring
        for (int i = 0; i < stepStartPosition.size(); i++) {
            String string = phpResponce.substring(stepStartPosition.get(i), stepEndPosition.get(i));
            stepSmallList.add(string);
        }

        //檢查list內容
        //showAllStepString(stepSmallList);

        return stepSmallList;
    }

    //定義秀出所有step子敘述內容
    public void showAllStepString(List<String> list) {
        //檢查list長度
        if (list == null)
            Log.e(Constants.DIRECTION_PROCESS_LOG, "list為空");
        else
            Log.v(Constants.DIRECTION_PROCESS_LOG, "list長度:" + list.size());

        for (int i = 0; i < list.size(); i++)
            Log.v(Constants.DIRECTION_PROCESS_LOG, "子敘述" + i + ": " + list.get(i));
    }

    public String getStartLocationDescript(String addressDescript) {
        //位置物件
        int startLocation_startIndex = 0, startLocation_endIndex = 0;
        String substring = "";

        //掃過整個string
        for (int i = 0; i < addressDescript.length(); i++) {
            //確定是否為s開頭
            if (addressDescript.charAt(i) == 's') {

                //試著抓取子字串
                try {
                    substring = addressDescript.substring(i, i + 14);
                } catch (StringIndexOutOfBoundsException e) {
                    break;
                }

                //確認是否為start location關鍵字
                if (judgeSameString(substring, "start_location")) {

                    //如果起頭index仍為0(代表正在搜尋的為起頭位置)
                    if (startLocation_startIndex == 0)
                        startLocation_startIndex = i + 15;
                    else
                        startLocation_endIndex = i - 3;
                }
            }
        }
        return addressDescript.substring(startLocation_startIndex, startLocation_endIndex);
    }

    public String getEndLocationDescript(String addressDescript) {
        //位置物件
        int endLocation_startIndex = 0, endLocation_endIndex = 0;
        String substring = "";

        //掃過整個string
        for (int i = 0; i < addressDescript.length(); i++) {
            //確定是否為e開頭
            if (addressDescript.charAt(i) == 'e') {
                //試著抓取子字串
                try {
                    substring = addressDescript.substring(i, i + 12);
                } catch (StringIndexOutOfBoundsException e) {
                    break;
                }

                //確認是否為end location關鍵字
                if (judgeSameString(substring, "end_location")) {

                    //如果起頭index仍為0(代表正在搜尋的為起頭位置)
                    if (endLocation_startIndex == 0)
                        endLocation_startIndex = i + 13;
                    else
                        endLocation_endIndex = i - 3;
                }
            }
        }
        return addressDescript.substring(endLocation_startIndex, endLocation_endIndex);
    }

    public String getDistanceDescript(String addressDescript) {
        //位置物件
        int distanceStartIndex = 0, distanceEndIndex = 0;
        String substring = "";

        //掃過整個string
        for (int i = 0; i < addressDescript.length(); i++) {
            //確定是否為d開頭
            if (addressDescript.charAt(i) == 'd') {
                //試著抓取子字串
                try {
                    substring = addressDescript.substring(i, i + 8);
                } catch (StringIndexOutOfBoundsException e) {
                    break;
                }

                //確認是否為end location關鍵字
                if (judgeSameString(substring, "distance")) {

                    //如果起頭index仍為0(代表正在搜尋的為起頭位置)
                    if (distanceStartIndex == 0)
                        distanceStartIndex = i + 9;
                    else
                        distanceEndIndex = i - 3;
                }
            }
        }
        return addressDescript.substring(distanceStartIndex, distanceEndIndex);
    }

    public LatLng getLatLngFromDescript(String addressDescript) {
        //經緯度index
        int latStartIndex = -1, latEndIndex = -1;
        int lngStartIndex = -1, lngEndIndex = -1;
        String substring = "";

        //尋找lat開頭
        for (int i = 0; i < addressDescript.length(); i++) {
            //確定是否為l開頭
            if (addressDescript.charAt(i) == 'l') {
                //試著抓取子字串
                try {
                    substring = addressDescript.substring(i, i + 3);
                } catch (StringIndexOutOfBoundsException e) {
                    break;
                }

                //確認是否為lat關鍵字
                if (judgeSameString(substring, "lat")) {
                    latStartIndex = i + 4;
                    break;
                }
            }
        }

        //尋找lat結尾
        for (int i = latStartIndex; i < addressDescript.length(); i++) {
            //確定是否為l開頭
            if (addressDescript.charAt(i) == 'l') {
                //試著抓取子字串
                try {
                    substring = addressDescript.substring(i, i + 3);
                } catch (StringIndexOutOfBoundsException e) {
                    break;
                }

                //確認是否為lat關鍵字
                if (judgeSameString(substring, "lat")) {
                    latEndIndex = i - 2;
                    break;
                }
            }
        }

        //分析出經度
        double lat = Double.valueOf(addressDescript.substring(latStartIndex, latEndIndex));

        //尋找lng開頭
        for (int i = 0; i < addressDescript.length(); i++) {
            //確定是否為l開頭
            if (addressDescript.charAt(i) == 'l') {
                //試著抓取子字串
                try {
                    substring = addressDescript.substring(i, i + 3);
                } catch (StringIndexOutOfBoundsException e) {
                    break;
                }

                //確認是否為lng關鍵字
                if (judgeSameString(substring, "lng")) {
                    lngStartIndex = i + 4;
                    break;
                }
            }
        }

        //尋找lng結尾
        for (int i = lngStartIndex; i < addressDescript.length(); i++) {
            //確定是否為l開頭
            if (addressDescript.charAt(i) == 'l') {
                //試著抓取子字串
                try {
                    substring = addressDescript.substring(i, i + 3);
                } catch (StringIndexOutOfBoundsException e) {
                    break;
                }

                //確認是否為end location關鍵字
                if (judgeSameString(substring, "lng")) {
                    lngEndIndex = i - 2;
                    break;
                }
            }
        }

        //分析出緯度
        double lng = Double.valueOf(addressDescript.substring(lngStartIndex, lngEndIndex));

        //檢查結果是否有誤
        LatLng res = new LatLng(lat, lng);
        if (res == null)
            Log.e(Constants.DIRECTION_PROCESS_LOG, "經緯度為空");

        return res;
    }

    public int getDistanceFromDescript(String addressDescript) {
        //經緯度index
        int distanceStartIndex = -1, distanceEndIndex = -1;
        String substring = "";

        //尋找距離開頭
        for (int i = 0; i < addressDescript.length(); i++) {
            //確定是否為t開頭
            if (addressDescript.charAt(i) == 't') {
                //試著抓取子字串
                try {
                    substring = addressDescript.substring(i, i + 4);
                } catch (StringIndexOutOfBoundsException e) {
                    break;
                }

                //確認是否為text關鍵字
                if (judgeSameString(substring, "text")) {
                    distanceStartIndex = i + 5;
                    break;
                }
            }
        }

        //尋找距離結尾(空白)
        for (int i = distanceStartIndex; i < addressDescript.length(); i++) {
            if (addressDescript.charAt(i) == 'm')
                distanceEndIndex = i - 1;
        }

        //檢查結果是否有誤
        int distance = judgeIfIntegerAndRevise(addressDescript.substring(distanceStartIndex, distanceEndIndex));
        if (distance == -1)
            Log.e(Constants.DIRECTION_PROCESS_LOG, "距離仍為-1");

        return distance;
    }

    //定義將位置點加入list
    public void addDirectionPoint(LatLng latLng) {
        boolean isExist = false;

        //檢查是否已存在此位置點
        for (LatLng l : directionPointList) {
            if (l == latLng)
                isExist = true;
        }

        //如果不存在，則加入
        if (!isExist)
            directionPointList.add(latLng);
    }

    //定義相等字串判斷
    public boolean judgeSameString(String a, String b) {
        if (a.length() != b.length())
            return false;
        for (int i = 0; i < a.length(); i++) {
            if (a.charAt(i) != b.charAt(i))
                return false;
        }
        return true;
    }

    //定義判斷小數或整數，並調整後輸出
    public int judgeIfIntegerAndRevise(String numberDescript){
        int res = Constants.IS_INTEGER;
        for (int i=0; i<numberDescript.length(); i++){
            if (numberDescript.charAt(i) == '.') {
                res = Constants.IS_DOUBLE;
            }
        }

        if (res == Constants.IS_INTEGER)
            return Integer.valueOf(numberDescript);
        else {
            double temp = Double.valueOf(numberDescript);
            temp *= 1000;
            return (int)temp;
        }
    }
}
