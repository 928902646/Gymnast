package com.gymnast.event;

public class Event<T> {
  public int type;
  public T t;
  public Event(int type, T t) {
    this.type = type;
    this.t = t;
  }
}
