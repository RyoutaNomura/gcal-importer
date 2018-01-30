package com.rn.tool.gcalimporter.entity.impl;

import com.google.api.services.calendar.model.Event;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.CalendarComponent;
import org.apache.commons.lang3.StringUtils;

/**
 * Implemented Class of  {@link AbstractEventContainerImpl} for Representing Ariel Calendar
 */
class EventContainerArielImpl extends AbstractEventContainerImpl<EventContainerArielImpl> {

  /**
   * Key for specifying Calendar
   */
  private static final String ARIEL_ID = "arielId";

  /**
   * Constructor with {@link CalendarComponent}
   *
   * @param c Calendar Object
   */
  public EventContainerArielImpl(final CalendarComponent c) {
    super(EventContainerType.Ariel, c);

    final PropertyList<Property> props = c.getProperties();
    ObjectMapper.mapString(props, Property.UID,
        s -> this.getEvent().getExtendedProperties().getPrivate().put(ARIEL_ID, s));
  }

  /**
   * Constructor with {@link Event}
   *
   * @param event Google Calendar Event
   */
  public EventContainerArielImpl(Event event) {
    super(EventContainerType.Ariel, event);
  }

  @Override
  public boolean hasSameImplContents(final EventContainerArielImpl container) {
    return StringUtils.equals(this.findArielId(), container.findArielId());
  }

  @Override
  public void patchImplContent(final EventContainerArielImpl container) {
    this.setArielId(container.findArielId());
  }

  @Override
  public boolean hasKey() {
    return this.event.getExtendedProperties().getPrivate().containsKey(ARIEL_ID);
  }

  @Override
  public String getKey() {
    return this.findArielId();
  }

  /**
   * Method for finding Ariel ID
   *
   * @return Ariel Id if it presents
   */
  private String findArielId() {
    if (!this.event.getExtendedProperties().getPrivate().containsKey(ARIEL_ID)) {
      throw new RuntimeException("ARIEL_ID not found.");
    }
    return this.event.getExtendedProperties().getPrivate().get(ARIEL_ID);
  }

  /**
   * Method for setting Ariel ID
   *
   * @param value arielId
   */
  private void setArielId(String value) {
    this.event.getExtendedProperties().getPrivate().put(ARIEL_ID, value);
  }

  /**
   * Method for checking existence of Ariel ID
   *
   * @return true if it is contained
   */
  public boolean containsArielId() {
    return this.event.getExtendedProperties().getPrivate()
        .containsKey(EventContainerArielImpl.ARIEL_ID);
  }
}
