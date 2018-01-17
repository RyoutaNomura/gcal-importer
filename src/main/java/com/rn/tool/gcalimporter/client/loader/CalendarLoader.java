package com.rn.tool.gcalimporter.client.loader;

import java.time.YearMonth;
import java.util.List;
import net.fortuna.ical4j.model.component.CalendarComponent;

/**
 * Interface of loader of calendar to transfer to google calendar
 */
public interface CalendarLoader {

  /**
   * Method for load calendar from Ariel
   *
   * @param yearMonth Target year and month of retrieve
   * @param range Acquisition range starting from yearMonth
   */
  List<CalendarComponent> loadCalendar(final YearMonth yearMonth, final int range);
//  List<CalendarComponent> loadCalendar(final LocalDateTime start, final LocalDateTime end);
//  List<CalendarComponent> loadCalendar(CustomCalendarSetting param);

  /**
   * Method for verify authentication
   *
   * @return true if verification passed
   */
  boolean verifyAuthentication();

  /**
   * Method for generate connection information as String
   *
   * @param keyLength left margin
   * @return String of connection information
   */
  String getConnectionInformation(final int keyLength);
}
