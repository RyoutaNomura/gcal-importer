package com.rn.tool.gcalimporter.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.CalendarComponent;
import org.apache.commons.lang3.StringUtils;

/**
 * Class of utility for converting {@link net.fortuna.ical4j.model.component.CalendarComponent}
 */
@UtilityClass
public class CalendarComponentUtils {

  /**
   * Format of ical date time
   */
  @SuppressWarnings("SpellCheckingInspection")
  public static final DateTimeFormatter ICAL_DATE_TIME_FORMAT = DateTimeFormatter
      .ofPattern("yyyyMMdd'T'HHmmss");

  public static final DateTimeFormatter ICAL_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

  /**
   * Method for judge whether all day event or not
   *
   * @param comp target calendar component
   * @return true if it is all day event
   */
  public static boolean isAllDayEvent(@NonNull final Component comp) {

    final PropertyList<Property> p = comp.getProperties();
    final Parameter sdtValue = p.getProperty(Property.DTSTART).getParameter(Parameter.VALUE);
    return (sdtValue != null) && (StringUtils.equals(sdtValue.getValue(), "DATE"));
  }

  /**
   * Method for extract start date time from calendar component
   *
   * @param comp target calendar component
   * @return start date time extracted from {@link Component}
   */
  public static ZonedDateTime getDttmStart(@NonNull final Component comp) {
    if (isAllDayEvent(comp)) {
      throw new RuntimeException(
          "Cannot convert to ZonedDateTime: " + System.lineSeparator() + comp);
    }

    final PropertyList<Property> p = comp.getProperties();
    final Property sdt = p.getProperty(Property.DTSTART);
    final ZoneId zid = ZoneId.of(sdt.getParameter(Property.TZID).getValue());
    return ZonedDateTime.of(LocalDateTime.parse(sdt.getValue(), ICAL_DATE_TIME_FORMAT), zid);
  }

  /**
   * Method for extract start date from calendar component
   *
   * @param comp target calendar component
   * @return start date extracted from {@link Component}
   */
  public static LocalDate getDtStart(@NonNull final Component comp) {
    if (!isAllDayEvent(comp)) {
      throw new RuntimeException("Cannot convert to LocalDate: " + System.lineSeparator() + comp);
    }

    final PropertyList<Property> p = comp.getProperties();
    final Property sdt = p.getProperty(Property.DTSTART);

    return LocalDate.parse(sdt.getValue(), ICAL_DATE_FORMAT);
  }

  /**
   * Method for extract end date time from calendar component
   *
   * @param comp target calendar component
   * @return end date time extracted from {@link Component}
   */
  public static ZonedDateTime getDttmEnd(@NonNull final Component comp) {
    if (isAllDayEvent(comp)) {
      throw new RuntimeException(
          "Cannot convert to ZonedDateTime: " + System.lineSeparator() + comp);
    }

    final PropertyList<Property> p = comp.getProperties();
    final Property sdt = p.getProperty(Property.DTEND);
    final ZoneId zid = ZoneId.of(sdt.getParameter(Property.TZID).getValue());
    return ZonedDateTime.of(LocalDateTime.parse(sdt.getValue(), ICAL_DATE_TIME_FORMAT), zid);
  }

  /**
   * Method for extract end date from calendar component
   *
   * @param comp target calendar component
   * @return end date extracted from {@link Component}
   */
  public static LocalDate getDtEnd(@NonNull final Component comp) {
    if (!isAllDayEvent(comp)) {
      throw new RuntimeException("Cannot convert to LocalDate. " + System.lineSeparator() + comp);
    }

    final PropertyList<Property> p = comp.getProperties();
    final Property sdt = p.getProperty(Property.DTEND);

    return LocalDate.parse(sdt.getValue(), ICAL_DATE_FORMAT);
  }

  /**
   * Method for check whether CalendarComponent is in target range or not.
   *
   * @param target start year month which target date should be included
   * @param range range to check from target year month
   * @param c CalendarComponent to check
   * @return true if CalendarComponent is in target year month
   */
  public static boolean isInTargetYearMonth(@NonNull final YearMonth target, final int range,
      @NonNull final CalendarComponent c) {

    if (isAllDayEvent(c)) {

      final LocalDate eventSdt = CalendarComponentUtils.getDtStart(c);
      final LocalDate eventEdt = CalendarComponentUtils.getDtEnd(c);
      final LocalDate targetSdt = target.atDay(1);
      final LocalDate targetEdt = targetSdt.plusMonths(range).minus(1, ChronoUnit.DAYS);

      return eventSdt.isAfter(targetSdt) && eventEdt.isBefore(targetEdt);

    } else {

      final ZonedDateTime eventSdt = CalendarComponentUtils.getDttmStart(c);
      final ZonedDateTime eventEdt = CalendarComponentUtils.getDttmEnd(c);
      final ZonedDateTime targetSdt = target.atDay(1).atStartOfDay(eventSdt.getZone());
      final ZonedDateTime targetEdt = targetSdt.plusMonths(range).minus(1, ChronoUnit.MILLIS);

      return eventSdt.isAfter(targetSdt) && eventEdt.isBefore(targetEdt);
    }
  }
}
