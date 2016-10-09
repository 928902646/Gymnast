package com.gymnast.data;

import com.gymnast.data.hotinfo.HotInfoServiceImpl;
import com.gymnast.data.user.UserServiceImpl;
import java.util.HashMap;
/**
 * Created by fldyown on 16/6/2.
 */
public class DataManager {
  private static HashMap<String, Service> services = new HashMap<String, Service>();
  public static <T extends Service> T getService(Class<T> clazz) {
    if (clazz == null) {
      throw new NullPointerException("clazz == null");
    }
    T service = (T) services.get(clazz.getName());
    if (service == null) {
      synchronized (clazz) {
        if (service == null) {
          if (clazz.isAssignableFrom(UserServiceImpl.class)) {
            service = (T) new UserServiceImpl();
            services.put(clazz.getName(), service);
            service = (T) services.get(clazz.getName());
          }
          if (clazz.isAssignableFrom(HotInfoServiceImpl.class)) {
            service = (T) new HotInfoServiceImpl();
            services.put(clazz.getName(), service);
            service = (T) services.get(clazz.getName());
          }
        }
      }
    }
    return service;
  }
}
