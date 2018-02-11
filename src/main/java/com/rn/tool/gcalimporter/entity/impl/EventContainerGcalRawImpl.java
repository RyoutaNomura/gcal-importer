package com.rn.tool.gcalimporter.entity.impl;

import com.google.api.services.calendar.model.Event;
import com.google.common.collect.Maps;
import lombok.NonNull;
import net.fortuna.ical4j.model.component.CalendarComponent;

/**
 * Implementation class of AbstractEventContainerImpl for representing gcal event
 */
class EventContainerGcalRawImpl extends AbstractEventContainerImpl<EventContainerGcalRawImpl> {

  /**
   * Constructor
   *
   * @param event event to contain {@link Event}
   */
  EventContainerGcalRawImpl(@NonNull final Event event) {
    super(EventContainerType.Raw, event);
    this.initExtendedProperties();
  }

  /**
   * Constructor
   *
   * @param c CalendarComponent with which generate event and make contain to this object {@link
   * CalendarComponent}
   */
  EventContainerGcalRawImpl(@NonNull final CalendarComponent c) {
    super(EventContainerType.Raw, c);
    this.initExtendedProperties();
  }

  /**
   * Method for initialize extended properties
   */
  private void initExtendedProperties() {

    if (this.event.getExtendedProperties() == null) {
      this.event.setExtendedProperties(new Event.ExtendedProperties());
    }
    if (this.event.getExtendedProperties().getPrivate() == null) {
      this.event.getExtendedProperties().setPrivate(Maps.newHashMap());
    }
    if (this.event.getExtendedProperties().getShared() == null) {
      this.event.getExtendedProperties().setShared(Maps.newHashMap());
    }
  }


  @Override
  boolean hasSameImplContents(@NonNull final EventContainerGcalRawImpl container) {
    return true;
  }

  @Override
  void patchImplContent(@NonNull final EventContainerGcalRawImpl container) {
  }

  @Override
  public boolean hasKey() {
    return true;
  }

  @Override
  public String getKey() {
    return this.event.getId();
  }
}
