package com.rn.tool.gcalimporter.entity.impl;

import lombok.Getter;
import lombok.NonNull;

/**
 * Enum for representing Type of {@link com.rn.tool.gcalimporter.entity.EventContainer}
 */
public enum EventContainerType {
  /**
   * Raw Event
   */
  Raw(EventContainerGcalRawImpl.class),
  /**
   * Event containing Ariel Calendar
   */
  Ariel(EventContainerArielImpl.class),;

  @Getter
  private final Class clazz;

  EventContainerType(@NonNull final Class clazz) {
    this.clazz = clazz;
  }
}
