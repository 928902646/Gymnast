package com.gymnast.event;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;
//有必要时再说 http://www.loongwind.com/archives/264.html
public class EventBus {
  public static volatile EventBus bus;
  private final Subject<Object, Object> _bus;
  private EventBus() {
    _bus = new SerializedSubject<>(PublishSubject.create());
  }
  public static EventBus getInstance() {
    if (bus == null) {
      synchronized (EventBus.class) {
        if (bus == null) {
          bus = new EventBus();
        }
      }
    }
    return bus;
  }
  public void send(Event event) {
    _bus.onNext(event);
  }
  public <T> Observable<T> toObservable(Class<T> event) {
    return _bus.ofType(event);
  }
  public boolean hasObservers() {
    return _bus.hasObservers();
  }
}