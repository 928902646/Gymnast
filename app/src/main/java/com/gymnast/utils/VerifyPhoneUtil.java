package com.gymnast.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Cymbi on 2016/9/7.
 */
public class VerifyPhoneUtil {
    /**
     * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
      联通：130、131、132、152、155、156、185、186
     电信：133、153、180、189、（1349卫通）
     * @param mobiles
     * @return
     */
    public static boolean isMobileNO(String mobiles){
        Pattern p = Pattern.compile("^1(3[0-9]|4[57]|5[0-35-9]|7[01678]|8[0-9])\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
        }
    /**
     * 判断邮箱是否合法
     * @param email
     * @return
     */
    public static boolean isEmail(String email){
        if (null==email || "".equals(email)) return false;
        //Pattern p = Pattern.compile("\\w+@(\\w+.)+[a-z]{2,3}"); //简单匹配
        Pattern p =  Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");//复杂匹配
        Matcher m = p.matcher(email);
        return m.matches();
    }
    public static boolean isZipNO(String zipString){
        String str = "^[1-9][0-9]{5}$";
        return Pattern.compile(str).matcher(zipString).matches();
    }
}
