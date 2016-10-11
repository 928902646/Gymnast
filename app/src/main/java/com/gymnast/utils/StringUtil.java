package com.gymnast.utils;

import android.util.Log;

import java.util.List;

/**
 * Created by zzqybyb19860112 on 2016/8/25.
 */
public class StringUtil {
    public static String isNullAvatar(String tempString){
        boolean isNull=tempString == null || tempString.equals("") ||tempString.equals("null");
        boolean isFalse=false;
        if (!isNull){
            isFalse=(!tempString.contains(".png"))&&(!tempString.contains(".jpg"))&&(!tempString.contains(".jpeg"))&&(!tempString.contains(".gif"));
        }
        return isNull||isFalse?"http://image.tiyujia.com/group1/M00/00/00/052YyFfXxLKARvQWAAAbNiA-OGw444.png":"http://image.tiyujia.com/"+tempString;
    }
    public static String isNullImage(String tempString){
        boolean isNull=tempString == null || tempString.equals("") ||tempString.equals("null");
        boolean isFalse=false;
        if (!isNull){
            isFalse=(!tempString.contains(".png"))&&(!tempString.contains(".jpg"))&&(!tempString.contains(".jpeg"))&&(!tempString.contains(".gif"));
        }
        return isNull||isFalse?"http://image.tiyujia.com/group1/M00/00/05/052YyFfozOqAOoAPAAN3M9vaMhU687.png":"http://image.tiyujia.com/"+tempString;
    }
    public static String isNullImageUrl(String tempString){
        boolean isNull=tempString == null || tempString.equals("") ||tempString.equals("null");
        boolean isFalse=false;
        if (!isNull){
            isFalse=(!tempString.contains(".png"))&&(!tempString.contains(".jpg"))&&(!tempString.contains(".jpeg"))&&(!tempString.contains(".gif"));
        }
        return isNull||isFalse?"http://image.tiyujia.com/group1/M00/00/00/052YyFfXxMCAd8PZAAAb3AsdeC4844.png":"http://image.tiyujia.com/"+tempString;
    }
    public static String isNullGroupId(String tempString){
        return tempString == null || tempString.equals("") || tempString.equals("null")?"000":tempString;
    }
    public static String isNullAuth(String tempString){
        return tempString == null ||tempString.equals("")||tempString.equals("null")?"":tempString;
    }
    public static String isNullDATA(String tempString){
        return tempString == null ||tempString.equals("")||tempString.equals("null")?"0":tempString;
    }
    public static String isNullNickName(String tempString){
        return tempString == null ||tempString.equals("")||tempString.equals("null")?"":tempString;
    }
    public static String listToString(List<String> stringList){
        if (stringList==null) {
            return null;
        }
        StringBuilder result=new StringBuilder();
        boolean flag=false;
        for (String string : stringList) {
            if (flag) {
                result.append(",");
            }else {
                flag=true;
            }
            result.append(string);
        }
        return result.toString();
    }
}
