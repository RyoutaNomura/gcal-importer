package com.rn.tool.gcalimporter.entity;

import com.rn.tool.gcalimporter.entity.impl.EventContainerFactory.EventContainerType;

/**
 * Class of container which hold iCal based event
 *
 * @param <T> Actual type
 */
public interface EventContainer<T extends EventContainer> extends Cloneable {

  /**
   * Method for acquisition of {@link EventContainerType} of this instance
   *
   * @return EventContainerType
   */
  EventContainerType getEcType();

  /**
   * Method for compare two instances of EventContainer
   *
   * @param container Container to compare
   * @return if has same eventContainerType and has same contents, return true
   */
  boolean hasSameContents(T container);

  /**
   * Method for create new event which is based on this instance and patched with argument's event
   *
   * @param patch event to patch
   * @return copied event patched with argument
   */
  T createPatchedEvent(T patch);

  /**
   * Method for acquisition of {@link com.google.api.services.calendar.model.Event} which is
   * contained in container
   *
   * @return Contained event
   */
  com.google.api.services.calendar.model.Event getEvent();

  /**
   * Method for confirm whether this instance has key or not
   *
   * @return if key of each eventContainerType exits, return true
   */
  boolean hasKey();

  /**
   * Method for acquisition of key of each eventContainerType
   */
  String getKey();
}
