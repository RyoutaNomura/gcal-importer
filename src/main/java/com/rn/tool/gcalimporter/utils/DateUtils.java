package com.rn.tool.gcalimporter.utils;

import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import lombok.NonNull;

/**
 * Class of Utility for java.time.*
 */
public class DateUtils {

  /**
   * Method for converting {@link YearMonth} to {@link ZonedDateTime}
   *
   * @param yearMonth Value to Convert
   * @return Converted Value
   */
  public static ZonedDateTime fromYearMonth(@NonNull final YearMonth yearMonth) {
    return yearMonth.atDay(1).atStartOfDay(ZoneId.systemDefault());
  }
}