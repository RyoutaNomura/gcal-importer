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
import org.apache.commons.lang3.StringUtils;

public class GoogleDateUtils {

  public static DateTime fromLocalDateTimeToDateTime(final LocalDateTime datetime,
      final ZoneId zone) {
    final ZonedDateTime zdt = datetime.atZone(zone);
    return fromZonedDateTimeToDateTime(zdt);
  }

  public static DateTime fromZonedDateTimeToDateTime(final ZonedDateTime datetime) {
    final java.util.Date date = Date.from(datetime.toInstant());
    final TimeZone tz = TimeZone.getTimeZone(datetime.getZone());
    return new DateTime(date, tz);
  }

  public static EventDateTime parseAsDttm(final String text, final DateTimeFormatter formatter,
      final ZoneId zone) {
    final LocalDateTime ldt = LocalDateTime.parse(text, formatter);
    return fromLocalDateTimeToEventDateTime(ldt, zone);
  }

  public static EventDateTime fromLocalDateTimeToEventDateTime(final LocalDateTime datetime,
      final ZoneId zone) {
    return fromZonedDateTimeToEventDateTime(datetime.atZone(zone));
  }

  public static EventDateTime fromZonedDateTimeToEventDateTime(final ZonedDateTime datetime) {
    final java.util.Date date = Date.from(datetime.toInstant());
    final TimeZone tz = TimeZone.getTimeZone(datetime.getZone());
    final DateTime dttm = new DateTime(date, tz);
    return new EventDateTime().setDateTime(dttm).setTimeZone(datetime.getZone().getId());

  }

  public static EventDateTime parseAsDate(final String text, final DateTimeFormatter formatter,
      final ZoneId zone) {
    final LocalDate ldt = LocalDate.parse(text, formatter);
    return fromLocalDateToEventDate(ldt);
  }

  public static EventDateTime fromLocalDateToEventDate(final LocalDate ldt) {
    final DateTime dt = new DateTime(true,
        ldt.atStartOfDay(ZoneId.of("GMT")).toInstant().toEpochMilli(), 0);
    return new EventDateTime().setDate(dt);
  }

  public static int compare(final DateTime dttm1, final DateTime dttm2) {
    return Comparator.comparing(DateTime::getValue).compare(dttm1, dttm2);
  }

  public static boolean contains(final YearMonth yearMonth, final EventDateTime dttm) {

    if (dttm == null) {
      throw new RuntimeException("dttm should not be null");
    }

    Date date;
    if (dttm.getDate() != null) {
      date = new Date(dttm.getDate().getValue());
    } else {
      date = new Date(dttm.getDateTime().getValue());
    }

    ZoneId zone;
    if (StringUtils.isNotEmpty(dttm.getTimeZone())) {
      zone = ZoneId.of(dttm.getTimeZone());
    } else {
      zone = ZoneId.systemDefault();
    }

    ZonedDateTime zdt = ZonedDateTime.ofInstant(date.toInstant(), zone);
    YearMonth eventYearMonth = YearMonth.from(zdt);
    return yearMonth.compareTo(eventYearMonth) == 0;
  }

  public static boolean equals(final EventDateTime a, final EventDateTime b) {
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
