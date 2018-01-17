package com.rn.tool.gcalimporter.utils;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.CalendarComponent;

/**
 * Class of utility for converting {@link net.fortuna.ical4j.model.component.CalendarComponent}
 */
public class CalendarComponentUtils {

  /**
   * Format of ical date time
   */
  public static final DateTimeFormatter ICAL_DATE_TIME_FORMAT = DateTimeFormatter
      .ofPattern("yyyyMMdd'T'HHmmss");

  /**
   * Method for judge whether all day event or not
   *
   * @param comp target calendar component
   * @return true i
   */
  public static boolean isAllDayEvent(final Component comp) {
    ZonedDateTime sdt = getDtStart(comp);
    if (!sdt.truncatedTo(ChronoUnit.DAYS).equals(sdt)) {
      return false;
    }

    ZonedDateTime edt = getDtEnd(comp);
    return edt.truncatedTo(ChronoUnit.DAYS).equals(edt);
  }

  /**
   * Method for extract start date from calendar component
   */
  public static ZonedDateTime getDtStart(final Component comp) {
    final PropertyList<Property> p = comp.getProperties();
    final Property sdt = p.getProperty(Property.DTSTART);
    final ZoneId zid = ZoneId.of(sdt.getParameter(Property.TZID).getValue());
    return ZonedDateTime.of(LocalDateTime.parse(sdt.getValue(), ICAL_DATE_TIME_FORMAT), zid);
  }

  /**
   * Method for extract end date from calendar component
   */
  public static ZonedDateTime getDtEnd(final Component comp) {
    final PropertyList<Property> p = comp.getProperties();
    final Property sdt = p.getProperty(Property.DTEND);
    final ZoneId zid = ZoneId.of(sdt.getParameter(Property.TZID).getValue());
    return ZonedDateTime.of(LocalDateTime.parse(sdt.getValue(), ICAL_DATE_TIME_FORMAT), zid);
  }

  /**
   * Method for validate whether CalendarComponent is in target range or not.
   */
  public static boolean isInTargetYearMonth(final YearMonth target, final int range,
      final CalendarComponent c) {
    final ZonedDateTime eventSdt = CalendarComponentUtils.getDtStart(c);
    final ZonedDateTime eventEdt = CalendarComponentUtils.getDtEnd(c);
    final ZonedDateTime targetSdt = target.atDay(1).atStartOfDay(eventSdt.getZone());
    final ZonedDateTime targetEdt = targetSdt.plusMonths(range).minus(1, ChronoUnit.MILLIS);

    return eventSdt.isAfter(targetSdt) && eventEdt.isBefore(targetEdt);
  }

  /**
   * Method for validate whether CalendarComponent is in target range or not.
   */
  public static boolean isInTargetYearMonth(final ZonedDateTime start, final ZonedDateTime end,
      final CalendarComponent c) {
    final ZonedDateTime eventSdt = CalendarComponentUtils.getDtStart(c);
    final ZonedDateTime eventEdt = CalendarComponentUtils.getDtEnd(c);

    return eventSdt.isAfter(start) && eventEdt.isBefore(end);
  }

}
