package com.rn.tool.gcalimporter.entity.impl;

import com.google.api.services.calendar.model.Event;
import com.rn.tool.gcalimporter.entity.EventContainer;
import lombok.NonNull;
import net.fortuna.ical4j.model.component.CalendarComponent;

/**
 * Factory Class for Instantiation of Implemented Class of {@link EventContainer}
 */
public class EventContainerFactory {

  /**
   * Method for Creating Instance of {@link EventContainer} with Google Calendar Event
   *
   * @param ecType type
   * @param event Google Calendar Event
   * @return Implemented Class of {@link EventContainer}
   */
  public static EventContainer create(@NonNull final EventContainerType ecType,
      @NonNull final Event event) {
    switch (ecType) {
      case Raw:
        return new EventContainerGcalRawImpl(event);
      case Ariel:
        return new EventContainerArielImpl(event);
      default:
        throw new RuntimeException(String.format("%s is not supported.", ecType));
    }
  }

  /**
   * Method for Creating Instance of {@link EventContainer} with Google Calendar Event
   *
   * @param ecType type
   * @param component Calendar Component
   * @return Implemented Class of {@link EventContainer}
   */
  public static EventContainer create(@NonNull final EventContainerType ecType,
      @NonNull final CalendarComponent component) {
    switch (ecType) {
      case Raw:
        return new EventContainerGcalRawImpl(component);
      case Ariel:
        return new EventContainerArielImpl(component);
      default:
        throw new RuntimeException(String.format("%s is not supported.", ecType));
    }
  }

}
