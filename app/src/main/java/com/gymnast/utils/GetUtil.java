package com.gymnast.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
/**
 * Created by 永不放弃 on 2016/8/14.
 */
public class GetUtil{
    public static String sendGetMessage(String url,HashMap<String,String> params){
        String result="";
        BufferedReader br=null;
        try{
            String str=url+"?";
            StringBuffer sb=new StringBuffer(str);
            Iterator<String> it = params.keySet().iterator();
            while(it.hasNext()){
                String key=it.next();
                String value=params.get(key);
                sb.append(key);
                sb.append("=");
                sb.append(value);
                sb.append("&");
            }
            sb.delete(sb.length()-1,sb.length());
            URL uri=new URL(sb.toString());
            URLConnection conn=uri.openConnection();
            conn.setConnectTimeout(3000);
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
            conn.connect();
            br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while((line=br.readLine())!=null){
                result+=line;
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            try{
                if(br!=null){
                br.close();
            }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return result;
    }
}