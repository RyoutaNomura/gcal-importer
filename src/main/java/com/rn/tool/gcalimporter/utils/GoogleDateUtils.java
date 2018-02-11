package com.rn.tool.gcalimporter.utils;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.EventDateTime;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Date;
import java.util.TimeZone;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

/**
 * Class of utility for Google API Date
 */
@UtilityClass
public class GoogleDateUtils {

  /**
   * Mathod for converting {@link LocalDateTime} to {@link DateTime}
   *
   * @param datetime target
   * @param zone zone
   * @return converted {@link DateTime}
   */
  public static DateTime fromLocalDateTimeToDateTime(@NonNull final LocalDateTime datetime,
      final ZoneId zone) {
    final ZonedDateTime zdt = datetime.atZone(zone);
    return fromZonedDateTimeToDateTime(zdt);
  }

  /**
   * Mathod for converting {@link ZonedDateTime} to {@link DateTime}
   *
   * @param datetime target
   * @return converted {@link DateTime}
   */
  public static DateTime fromZonedDateTimeToDateTime(@NonNull final ZonedDateTime datetime) {
    final java.util.Date date = Date.from(datetime.toInstant());
    final TimeZone tz = TimeZone.getTimeZone(datetime.getZone());
    return new DateTime(date, tz);
  }

  /**
   * Method for parsing String as {@link EventDateTime}
   *
   * @param text target
   * @param formatter formatter
   * @param zone zone
   * @return converted {@link EventDateTime}
   */
  public static EventDateTime parseAsDttm(@NonNull final String text,
      @NonNull final DateTimeFormatter formatter,
      final ZoneId zone) {
    final LocalDateTime ldt = LocalDateTime.parse(text, formatter);
    return fromLocalDateTimeToEventDateTime(ldt, zone);
  }

  /**
   * Method for converting {@link LocalDateTime} to {@link EventDateTime}
   *
   * @param datetime target
   * @param zone zone
   * @return converted {@link EventDateTime}
   */
  public static EventDateTime fromLocalDateTimeToEventDateTime(
      @NonNull final LocalDateTime datetime,
      final ZoneId zone) {
    return fromZonedDateTimeToEventDateTime(datetime.atZone(zone));
  }

  /**
   * Method for converting {@link ZonedDateTime} to {@link EventDateTime}
   *
   * @param datetime target
   * @return converted {@link EventDateTime}
   */
  public static EventDateTime fromZonedDateTimeToEventDateTime(
      @NonNull final ZonedDateTime datetime) {
    final java.util.Date date = Date.from(datetime.toInstant());
    final TimeZone tz = TimeZone.getTimeZone(datetime.getZone());
    final DateTime dttm = new DateTime(date, tz);
    return new EventDateTime().setDateTime(dttm).setTimeZone(datetime.getZone().getId());

  }

  /**
   * Method for parsing String as {@link EventDateTime}(Date representation)
   *
   * @param text target
   * @param formatter formatter
   * @return converted {@link EventDateTime}
   */
  public static EventDateTime parseAsDate(@NonNull final String text,
      @NonNull final DateTimeFormatter formatter) {
    final LocalDate ldt = LocalDate.parse(text, formatter);
    return fromLocalDateToEventDate(ldt);
  }

  /**
   * Method for converting {@link LocalDate} to {@link EventDateTime}(Date representation)
   *
   * @param ldt target
   * @return converted {@link EventDateTime}
   */
  public static EventDateTime fromLocalDateToEventDate(@NonNull final LocalDate ldt) {
    final DateTime dt = new DateTime(true,
        ldt.atStartOfDay(ZoneId.of("GMT")).toInstant().toEpochMilli(), 0);
    return new EventDateTime().setDate(dt);
  }

  /**
   * Method for comparing {@link DateTime}
   *
   * @param dttm1 A
   * @param dttm2 B
   * @return 1 if A is greater than B, 0 if A is equal to B, -1 if A is smaller than B
   */
  public static int compare(@NonNull final DateTime dttm1, @NonNull final DateTime dttm2) {
    return Comparator.comparing(DateTime::getValue).compare(dttm1, dttm2);
  }

  /**
   * Method for checking existence in target range
   *
   * @param yearMonth target range
   * @param dttm target date to check
   * @return true if it is contained
   */
  public static boolean contains(@NonNull final YearMonth yearMonth,
      @NonNull final EventDateTime dttm) {

    final Date date;
    if (dttm.getDate() != null) {
      date = new Date(dttm.getDate().getValue());
    } else {
      date = new Date(dttm.getDateTime().getValue());
    }

    final ZoneId zone;
    if (StringUtils.isNotEmpty(dttm.getTimeZone())) {
      zone = ZoneId.of(dttm.getTimeZone());
    } else {
      zone = ZoneId.systemDefault();
    }

    ZonedDateTime zdt = ZonedDateTime.ofInstant(date.toInstant(), zone);
    YearMonth eventYearMonth = YearMonth.from(zdt);
    return yearMonth.compareTo(eventYearMonth) == 0;
  }

  /**
   * Method for checking equality of {@link EventDateTime}
   *
   * @param a A
   * @param b B
   * @return true if A is equal to B
   */
  public static boolean equals(@NonNull final EventDateTime a, @NonNull final EventDateTime b) {
    if ((a == null) && (b == null)) {
      return true;
    }
    if ((a == null) || (b == null)) {
      return false;
    }
    if ((a.getDate() != null) && (b.getDate() != null)) {
      return a.getDate().toString().equals(b.getDate().toString());
    }
    return (a.getDateTime() != null) && (b.getDateTime() != null) && a.getDateTime()
        .equals(b.getDateTime());
  }
}
