package com.rn.tool.gcalimporter.entity.impl;

import com.google.api.services.calendar.model.Event;
import com.rn.tool.gcalimporter.entity.EventContainer;
import net.fortuna.ical4j.model.component.CalendarComponent;

public class EventContainerFactory {

  public static EventContainer create(EventContainerType ecType, Event event) {
    switch (ecType) {
      case Raw:
        return new EventContainerGcalRawImpl(event);
      case Ariel:
        return new EventContainerArielImpl(event);
      default:
        throw new RuntimeException(String.format("%s is not supported.", ecType));
    }
  }

  public static EventContainer create(EventContainerType ecType, CalendarComponent component) {
    switch (ecType) {
      case Raw:
        return new EventContainerGcalRawImpl(component);
      case Ariel:
        return new EventContainerArielImpl(component);
      default:
        throw new RuntimeException(String.format("%s is not supported.", ecType));
    }
  }

  public enum EventContainerType {
    Raw(EventContainerGcalRawImpl.class),
    Ariel(EventContainerArielImpl.class),;

    private final Class clazz;

    EventContainerType(Class clazz) {
      this.clazz = clazz;
    }
  }
}
