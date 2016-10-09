package com.gymnast.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by zzqybyb19860112 on 2016/9/7.
 */
public class TimeUtil {
    public static String getDetailTime(long timeMillions){
        SimpleDateFormat sdf=new SimpleDateFormat("MM月dd日HH时mm分");
        String temptime=sdf.format(new Date(timeMillions));
        String month=temptime.substring(0, 2);
        String day=temptime.substring(3, 5);
        String hour=temptime.substring(6, 8);
        String minute=temptime.substring(9,11);
        month=NumberUtil.handleNumber(month);
        day=NumberUtil.handleNumber(day);
        hour=NumberUtil.handleNumber(hour);
        minute=NumberUtil.handleNumber(minute);
        return month+"月"+day+"日"+hour+"时"+minute+"分";
    }
    public static String getDetailTimeNumber(long timeMillions){
        SimpleDateFormat sdf=new SimpleDateFormat("MM月dd日HH时mm分");
        String temptime=sdf.format(new Date(timeMillions));
        String month=temptime.substring(0, 2);
        String day=temptime.substring(3, 5);
        String hour=temptime.substring(6, 8);
        String minute=temptime.substring(9,11);
        month=NumberUtil.handleNumber(month);
        day=NumberUtil.handleNumber(day);
        return month+"月"+day+"日  "+hour+":"+minute;
    }
    public static String getMonthDayTime(long timeMillions){
        SimpleDateFormat sdf=new SimpleDateFormat("MM月dd日");
        String temptime=sdf.format(new Date(timeMillions));
        String month=temptime.substring(0, 2);
        String day=temptime.substring(3, 5);
        month=NumberUtil.handleNumber(month);
        day=NumberUtil.handleNumber(day);
        return month+"月"+day+"日";
    }
    public static String getNumberTime(long timeMillions){
        SimpleDateFormat sdf=new SimpleDateFormat("MM月dd日HH时mm分");
        String temptime=sdf.format(new Date(timeMillions));
        String month=temptime.substring(0, 2);
        String day=temptime.substring(3, 5);
        String hour=temptime.substring(6, 8);
        String minute=temptime.substring(9,11);
        month=NumberUtil.handleNumber(month);
        day=NumberUtil.handleNumber(day);
        return month+"月"+day+"日  "+hour+":"+minute;
    }
    public static String getDisTime(long nowTime,long beginTime){
        long disTime=beginTime-nowTime;
        String result="";
        if (disTime<86400L*1000L){
            long hour= disTime/3600000L;
            long minute=(disTime-hour*3600000L)/60000L;
                result=hour+"小时"+minute+"分";
        }else {
            long day=  (disTime/86400000L);
            long hour= (disTime-day*86400000L)/3600000L;
            long minute=(disTime-day*86400000L-hour*3600000L)/60000L;
            result=day+"天"+hour+"小时"+minute+"分";
        }
        return result;
    }
    public static String checkTime(long timeInMillions){
        String time="";
        Calendar calendarBegin=Calendar.getInstance(Locale.CHINESE);
        calendarBegin.setTimeInMillis(timeInMillions);
        Calendar calendarNow=Calendar.getInstance(Locale.CHINESE);
        long nowTimeInMillions=System.currentTimeMillis();
        calendarNow.setTimeInMillis(nowTimeInMillions);
        long disTime=timeInMillions-nowTimeInMillions;
        int beginDay= calendarBegin.get(Calendar.DAY_OF_WEEK);
        int realDay= calendarNow.get(Calendar.DAY_OF_WEEK);
        int realWeek= calendarNow.get(Calendar.WEEK_OF_YEAR);
        int beginWeek= calendarBegin.get(Calendar.WEEK_OF_YEAR);
        if (beginDay==realDay&&disTime<=1000L*60L*60L*24L*7L){
            time="今天"+setStartTime(timeInMillions);
        }else if (beginWeek==realWeek){
            time="本周"+setNumberToChinese(beginDay)+setStartTime(timeInMillions);
        }else {
            SimpleDateFormat sdf=new SimpleDateFormat("MM-dd HH:ss");
            time=sdf.format(new Date(timeInMillions));
        }
        return time;
    }
    private static String setStartTime(long timeInMillions){
        Date date=new Date();
        date.setTime(timeInMillions);
        SimpleDateFormat sdf=new SimpleDateFormat("HH:mm");
        String time=sdf.format(date);
        String hour=time.substring(0,2);
        if (Integer.valueOf(hour)<10){
            time=time.substring(1);
            return time;
        }
        return time;
    }
    private static String setNumberToChinese(int beginDay) {
        String dayInCN="";
        switch (beginDay){
            case 1:dayInCN="日";break;
            case 2:dayInCN="一";break;
            case 3:dayInCN="二";break;
            case 4:dayInCN="三";break;
            case 5:dayInCN="四";break;
            case 6:dayInCN="五";break;
            case 7:dayInCN="六";break;
        }
        return dayInCN;
    }
    public static String setLoveNum(int number){
        String favNumber="";
        if (number<1000){
            favNumber=number+"";
        }else if (number<10000){
            favNumber=number/1000+"K";
        }else{
            favNumber=number/10000+"万";
        }
        return favNumber;
    }
}
