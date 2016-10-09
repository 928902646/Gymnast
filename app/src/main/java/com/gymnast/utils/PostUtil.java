package com.gymnast.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
/**
 * Created by 永不言败 on 2016/8/12.
 */
public class PostUtil {
    /**
     * @param params 填写的url的参数
     */
    public static String sendPostMessage(String uri,Map<String, String> params){
        String result=null;
        URL url = null;
        try {
            url = new URL(uri);
        } catch (Exception e) {
            e.printStackTrace();
        }
        StringBuffer buffer = new StringBuffer();
        try {//把请求的主体写入正文！！
            if(params != null&&!params.isEmpty()){
                //迭代器
                for(Map.Entry<String, String> entry : params.entrySet()){
                    buffer.append(entry.getKey()).append("=").
                            append(URLEncoder.encode(entry.getValue(), "utf-8")).
                            append("&");
                }
            }
            //删除最后一个字符&，多了一个;主体设置完毕
            buffer.deleteCharAt(buffer.length()-1);
            byte[] mydata = buffer.toString().getBytes();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(3000);
            connection.setDoInput(true);//表示从服务器获取数据
            connection.setDoOutput(true);//表示向服务器写数据
            //获得上传信息的字节大小以及长度
            connection.setRequestMethod("POST");
            //是否使用缓存
            connection.setUseCaches(false);
            //表示设置请求体的类型是文本类型
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", String.valueOf(mydata.length));
            connection.connect();   //连接，不写也可以。。？？有待了解
            //获得输出流，向服务器输出数据
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(mydata,0,mydata.length);
            //获得服务器响应的结果和状态码
                result =changeInputeStream(connection.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    /**
     * 将一个输入流转换成字符串
     * @param inputStream
     */
    private static String changeInputeStream(InputStream inputStream) {
        //通常叫做内存流，写在内存中的
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        String result = "";
        if(inputStream != null){
            try {
                while((len = inputStream.read(data))!=-1){
                    data.toString();
                    outputStream.write(data, 0, len);
                }
                //result是在服务器端设置的doPost函数中的
                result = new String(outputStream.toByteArray(),"utf-8");
                outputStream.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
