package com.rn.tool.gcalimporter.client.google;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.google.api.services.calendar.model.Event;
import com.google.common.collect.Lists;
import com.rn.tool.gcalimporter.entity.EventContainer;
import com.rn.tool.gcalimporter.entity.impl.EventContainerFactory;
import com.rn.tool.gcalimporter.entity.impl.EventContainerFactory.EventContainerType;
import com.rn.tool.gcalimporter.utils.GoogleDateUtils;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class GoogleCalendarClientTest {

  private static final String APPLICATION_NAME = GoogleCalendarClientTest.class.getName();

  private static final java.io.File DATA_STORE_DIR = new java.io.File(
      System.getProperty("user.dir"), "src/main/resources");

  private static String GOOGLE_CALENDAR_ID;

  private static GoogleCalendarClient client;

  @BeforeClass
  public static void beforeClass() {
    client = new GoogleCalendarClient(APPLICATION_NAME, DATA_STORE_DIR);
    GOOGLE_CALENDAR_ID = client.createNewCalendar(ZonedDateTime.now().toString());
  }

  @AfterClass
  public static void afterClass() {
    client.deleteCalendar(GOOGLE_CALENDAR_ID);
  }

  @Test
  public void testInsertEvents() throws Exception {

    final ZonedDateTime sdate = ZonedDateTime.now();
    final ZonedDateTime edate = sdate.plusDays(1);
    final EventContainer expected = EventContainerFactory.create(EventContainerType.Raw, new Event()
        .setStart(GoogleDateUtils.fromZonedDateTimeToEventDateTime(sdate))
        .setEnd(GoogleDateUtils.fromZonedDateTimeToEventDateTime(edate))
        .setICalUID(UUID.randomUUID().toString()));

    client.insertEvents(GOOGLE_CALENDAR_ID, Lists.newArrayList(expected));

    Thread.sleep(5000);

    final List<EventContainer> actual = client
        .findEvents(GOOGLE_CALENDAR_ID, YearMonth.now(), 1, EventContainerType.Raw);
    assertThat(actual.stream().findFirst().isPresent(), is(true));

    actual.stream().findFirst().ifPresent(w -> {
      assertThat(w.getEvent().getICalUID(), is(expected.getEvent().getICalUID()));
      // assertThat(event.getStart(),
      // is(comparesEqualTo(expected.getStart())));
      // assertThat(event.getEnd(),
      // is(comparesEqualTo(expected.getEnd())));
    });
  }

  @Test
  public void testDeleteEvents() {
    client.deleteEvents(GOOGLE_CALENDAR_ID, YearMonth.now());

    final List<EventContainer> actual = client
        .findEvents(GOOGLE_CALENDAR_ID, YearMonth.now(), 1, EventContainerType.Raw);
    assertThat(actual.size(), is(0));
  }
}
