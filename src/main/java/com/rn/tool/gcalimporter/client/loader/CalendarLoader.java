package com.rn.tool.gcalimporter.client.loader;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import lombok.NonNull;
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
  List<CalendarComponent> loadCalendar(@NonNull final  YearMonth yearMonth, @NonNull final  int range);

  /**
   * Method for verify authentication
   *
   * @return true if verification passed
   */
  boolean verifyAuthentication();

  /**
   * Method for generate connection information as String
   *
   * @return Map of connection information
   */
  Map<String, String> getConnectionInfo();
}
