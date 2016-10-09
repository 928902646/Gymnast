package com.gymnast.data.net;

/**
 * Created by fldyown on 16/6/27.
 */
public class Result<T> {
  public int state;
  public String successmsg;
  public String phone;
  public int code;
  public T data;
  @Override
  public String toString() {
    return "{" +
        "state=" + state +
        ", successmsg='" + successmsg + '\'' +
        ", data='" + data + '\'' +
        '}';
  }
}