package com.rn.tool.gcalimporter.utils;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.EventDateTime;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.TimeZone;
import org.junit.Test;

public class GoogleDateUtilsTest {

  // @Test
  // public final void testFromZonedDateTimeToDateTime() {
  // final Date date = new Date();
  // final TimeZone tz = TimeZone.getDefault();
  // final ZonedDateTime zdt = ZonedDateTime.ofInstant(date.toInstant(),
  // tz.toZoneId());
  //
  // final DateTime expected = new DateTime(date, tz);
  // final DateTime actual = GoogleDateUtils.fromZonedDateTimeToDateTime(zdt);
  //
  // assertThat(actual, is(expected));
  // }

  @Test
  public final void testFromLocalDateTimeToDateTime() {
    final Date date = new Date();
    final TimeZone tz = TimeZone.getDefault();
    final LocalDateTime ldt = LocalDateTime.ofInstant(date.toInstant(), tz.toZoneId());

    final DateTime expected = new DateTime(date, tz);
//		final DateTime actual = GoogleDateUtils.fromLocalDateTimeToDateTime(ldt, tz.toZoneId());

//		assertThat(actual, is(expected));
  }
  //
  // @Test
  // public final void testFromZonedDateTimeToEventDateTime() {
  // final Date date = new Date();
  // final TimeZone tz = TimeZone.getDefault();
  // final ZonedDateTime zdt = ZonedDateTime.ofInstant(date.toInstant(),
  // tz.toZoneId());
  //
  // final EventDateTime expected = new EventDateTime().setDateTime(new
  // DateTime(date, tz)).setTimeZone(tz.getID());
  // final EventDateTime actual =
  // GoogleDateUtils.fromZonedDateTimeToEventDateTime(zdt);
  //
  // assertThat(actual, is(expected));
  // }

  @Test
  public final void testFromLocalDateTimeToEventDateTime() {
    final Date date = new Date();
    final TimeZone tz = TimeZone.getDefault();
    final LocalDateTime ldt = LocalDateTime.ofInstant(date.toInstant(), tz.toZoneId());

    final EventDateTime expected = new EventDateTime().setDateTime(new DateTime(date, tz))
        .setTimeZone(tz.getID());
    final EventDateTime actual = GoogleDateUtils
        .fromLocalDateTimeToEventDateTime(ldt, tz.toZoneId());

    assertThat(actual, is(expected));
  }
}
