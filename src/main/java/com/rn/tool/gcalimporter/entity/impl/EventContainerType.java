package com.rn.tool.gcalimporter.entity.impl;

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

  private final Class clazz;

  EventContainerType(Class clazz) {
    this.clazz = clazz;
  }
}
