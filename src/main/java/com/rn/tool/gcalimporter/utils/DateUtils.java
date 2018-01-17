package com.rn.tool.gcalimporter.utils;

import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DateUtils {

  public static ZonedDateTime fromYearMonth(final YearMonth yearMonth) {
    if (yearMonth == null) {
      throw new NullPointerException("yearMonth should not be null.");
    }

    return yearMonth.atDay(1).atStartOfDay(ZoneId.systemDefault());
  }
}