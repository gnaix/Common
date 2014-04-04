package com.gnaix.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.SystemClock;
import android.util.Log;

public class TimeUtil {

    private static final String TAG = "TimeUtil";
    
    public static String getTimeDHMS(long second) {
        int day = 0, hour = 0, min = 0, sec = 0;
        day = (int) (second / (24 * 60 * 60));
        int left = (int) (second % (24 * 60 * 60));
        hour = left / (60 * 60);
        left = (int) (left % (60 * 60));
        min = left / 60;
        sec = left % 60;
        StringBuilder sb = new StringBuilder();
        if(day>0) {
            sb.append(day+"天");
        }
        if(hour>0) {
            sb.append(hour+"小时");
        }
        if(min>0) {
            sb.append(min+"分钟");
        }
        if(sec>0) {
            sb.append(sec+"秒");
        }
        return sb.toString();
    }

    public static String converTime(long timestamp) {
        //Log.d(TAG, "timestamp:"+timestamp.toGMTString());
        long currentmillSeconds = System.currentTimeMillis();
        //Log.d(TAG, "currentmillSeconds:"+currentmillSeconds+", "+ new Date(currentmillSeconds).toGMTString());
        long timeGap = (currentmillSeconds - timestamp) / 1000;// 与现在时间相差秒数
        //Log.d(TAG, "timeGap:" + timeGap);
        String timeStr = null;
        if (timeGap > 365 * 24 * 60 * 60) {// 1年以上
            timeStr = TimeUtil.getStandardTime(timestamp, "yyyy-MM-dd HH:mm");
        } else if (timeGap > 24 * 60 * 60 * 3) {
            timeStr = TimeUtil.getStandardTime(timestamp, "MM月dd日 HH:mm");
        } else if(timeGap > 24 * 60 * 60 * 2) {
            timeStr = TimeUtil.getStandardTime(timestamp, "前天 HH:mm");
        } else if(timeGap > 24 * 60 * 60 * 1) {
            timeStr = TimeUtil.getStandardTime(timestamp, "昨天 HH:mm");
        } else if (timeGap > 60 * 60) {// 1小时-24小时
            timeStr = timeGap / (60 * 60) + "小时前";
        } else if (timeGap > 60) {// 1分钟-59分钟
            timeStr = timeGap / 60 + "分钟前";
        } else {// 1秒钟-59秒钟
            timeStr = "刚刚";
        }
        return timeStr;
    }

    public static String getStandardTime(long date) {
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATE_TIME);
        return sdf.format(new Date(date));
    }
    
    public static String getStandardTime(long date,String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(date));
    }

    public final static String FORMAT_DATE = "yyyy-MM-dd";
    public final static String FORMAT_TIME = "HH:mm:ss";
    public final static String FORMAT_DATE_TIME = "yyyy-MM-dd HH:mm:ss";
    public final static String FORMAT_DATE_TIME_DETAIL = "yyyy-MM-dd HH:mm:ss:SSS";
    public final static String FORMAT_MONTH_DAY_TIME = "MM月dd日 HH:mm:ss";

    /**
     * 将日期字符串以指定格式转换为Date
     * 
     * @param time
     *            日期字符串
     * @param format
     *            指定的日期格式，若为null或""则使用指定的格式"yyyy-MM-dd HH:mm:ss"
     * @return
     */
    public static Date getTimeFromString(String timeStr, String format) {
        //Log.d(TAG, "timeStr:"+timeStr + ", format:"+format);
        SimpleDateFormat  sdf;
        if(timeStr == null || timeStr.length() == 0){
            return null;
        }
        if (format == null || format.trim().equals("")) {
            sdf = new SimpleDateFormat(FORMAT_DATE_TIME); 
        } else {
            sdf = new SimpleDateFormat(format); 
        }
        try {
            return sdf.parse(timeStr.trim());
        } catch (ParseException e) {
            Log.e(TAG, "parse "+ timeStr +" error, format is " + format,e);
            return null;
        }
    }
    
    public static long syncServerTime(long firstSyncelapsedRealtime,long serverTime){
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long currentTime = serverTime + (elapsedRealtime - firstSyncelapsedRealtime);
//        Calendar calendar = Calendar.getInstance(TimeZone
//                .getTimeZone("UTC"));
//        calendar.setTimeInMillis(currentTime);
        return currentTime;
    }
}
