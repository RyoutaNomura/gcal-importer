package com.rn.tool.gcalimporter.entity.impl;

import com.google.api.services.calendar.model.Event;
import com.google.common.collect.Maps;
import com.rn.tool.gcalimporter.entity.EventContainer;
import com.rn.tool.gcalimporter.utils.CalendarComponentUtils;
import com.rn.tool.gcalimporter.utils.GoogleDateUtils;
import java.util.function.BiConsumer;
import lombok.Getter;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.CalendarComponent;
import org.apache.commons.lang3.StringUtils;

/**
 * Class for base implementation of {@link EventContainer}
 *
 * @param <T> Actual type
 */
abstract class AbstractEventContainerImpl<T extends AbstractEventContainerImpl> implements
    EventContainer<T> {

  /**
   * Event which is contained in the object
   */
  @Getter
  protected final Event event;

  /**
   * EventContainer type of this instance
   */
  @Getter
  protected final EventContainerType ecType;
  /**
   * Consumer for extract description from event and append it to original string
   */
  private final BiConsumer<Event, String> appendDescription = (e, d) -> e.setDescription(
      new StringBuilder()
          .append(e.getDescription())
          .append(System.lineSeparator())
          .append(d)
          .toString());

  /**
   * Constructor
   *
   * @param ecType actual type {@link EventContainerType}
   */
  AbstractEventContainerImpl(EventContainerType ecType) {
    this(ecType, new Event());
  }

  /**
   * Constructor
   *
   * @param ecType actual type {@link EventContainerType}
   * @param event event to contain {@link Event}
   */
  AbstractEventContainerImpl(EventContainerType ecType, Event event) {
    super();

    this.ecType = ecType;
    this.event = event;
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

  /**
   * Constructor
   *
   * @param ecType actual type {@link EventContainerType}
   * @param c calendarComponent with which generated Event to contain {@link CalendarComponent}
   */
  AbstractEventContainerImpl(EventContainerType ecType, final CalendarComponent c) {
    this(ecType);

    final PropertyList<Property> props = c.getProperties();
    ObjectMapper.mapString(props, Property.SUMMARY, this.event::setSummary);
    if (CalendarComponentUtils.isAllDayEvent(c)) {
      ObjectMapper.mapEventDate(props, Property.DTSTART, this.event::setStart);
      ObjectMapper.mapEventDate(props, Property.DTEND, this.event::setEnd);
    } else {
      ObjectMapper.mapEventDateTime(props, Property.DTSTART, this.event::setStart);
      ObjectMapper.mapEventDateTime(props, Property.DTEND, this.event::setEnd);
    }
    ObjectMapper.mapString(props, Property.DESCRIPTION, this.event::setDescription);
    ObjectMapper.mapString(props, Property.URL, s -> this.appendDescription.accept(this.event, s));
  }

  /**
   * Abstract method for determining whether it is the same event. Check logic for each {@link
   * EventContainerType} is to be implemented in this method
   *
   * @param container event to compare
   * @return true if it can be considered that these are same events
   */
  abstract boolean hasSameImplContents(T container);

  /**
   * Abstract method for patch to the container. Patch logic for each {@link EventContainerType} is
   * to be implemented in this method
   *
   * @param container event to patch
   */
  abstract void patchImplContent(T container);

  @Override
  public boolean hasSameContents(T container) {
    if (!this.hasSameImplContents(container)) {
      return false;
    }
    if (container == null) {
      return false;
    }
    if (!StringUtils.equals(this.event.getSummary(), container.getEvent().getSummary())) {
      return false;
    }
    if (!StringUtils.equals(this.event.getDescription(), container.getEvent().getDescription())) {
      return false;
    }
    if (!GoogleDateUtils.equals(event.getStart(), container.getEvent().getStart())) {
      return false;
    }
    if (!GoogleDateUtils.equals(event.getEnd(), container.getEvent().getEnd())) {
      return false;
    }
    return StringUtils.equals(this.event.getDescription(), container.getEvent().getDescription());
  }

  @SuppressWarnings("unchecked")
  @Override
  public T createPatchedEvent(T patch) {
    T copy = (T) EventContainerFactory.create(this.ecType, this.event.clone());

    copy.getEvent().setSummary(patch.getEvent().getSummary());
    copy.getEvent().setDescription(patch.getEvent().getDescription());
    copy.getEvent().setStart(patch.getEvent().getStart());
    copy.getEvent().setEnd(patch.getEvent().getEnd());
    copy.patchImplContent(patch);
    return copy;
  }
}
