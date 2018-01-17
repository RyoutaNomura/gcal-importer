package com.rn.tool.gcalimporter.entity.impl;

import com.google.api.services.calendar.model.Event;
import com.rn.tool.gcalimporter.entity.impl.EventContainerFactory.EventContainerType;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.CalendarComponent;
import org.apache.commons.lang3.StringUtils;


class EventContainerArielImpl extends AbstractEventContainerImpl<EventContainerArielImpl> {

  private static final String COMPASS_ID = "compassId";

  public EventContainerArielImpl(final CalendarComponent c) {
    super(EventContainerType.Ariel, c);
    final PropertyList<Property> props = c.getProperties();
    ObjectMapper.mapString(props, Property.UID,
        s -> this.getEvent().getExtendedProperties().getPrivate().put("compassId", s));
  }

  public EventContainerArielImpl(Event event) {
    super(EventContainerType.Ariel, event);
  }

  @Override
  public boolean hasSameImplContents(final EventContainerArielImpl container) {
    return StringUtils.equals(this.getCompassId(), container.getCompassId());
  }

  private String getCompassId() {
    if (!this.event.getExtendedProperties().getPrivate().containsKey(COMPASS_ID)) {
      throw new RuntimeException("COMPASS_ID not found.");
    }
    return this.event.getExtendedProperties().getPrivate().get(COMPASS_ID);
  }

  private void setCompassId(String value) {
    this.event.getExtendedProperties().getPrivate().put(COMPASS_ID, value);
  }

  @Override
  public void patchImplContent(final EventContainerArielImpl container) {
    this.setCompassId(container.getCompassId());
  }

  public boolean containsCompassId() {
    return this.event.getExtendedProperties().getPrivate()
        .containsKey(EventContainerArielImpl.COMPASS_ID);
  }

  @Override
  public boolean hasKey() {
    return this.event.getExtendedProperties().getPrivate().containsKey(COMPASS_ID);
  }

  @Override
  public String getKey() {
    return this.getCompassId();
  }
}
