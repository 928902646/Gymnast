package com.gymnast.utils;

/**
 * Created by zzqybyb19860112 on 2016/9/2.
 */
public class NumberUtil {
    public static String handleNumber(String baseNumber){
        if (baseNumber.startsWith("0")){
            baseNumber=baseNumber.substring(1);
        }
        int number=Integer.valueOf(baseNumber);
        String result=String.valueOf(number);
        return result;
    }
}
