package com.rn.tool.gcalimporter.client.google;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.google.api.services.calendar.model.Event;
import com.google.common.collect.Lists;
import com.rn.tool.gcalimporter.entity.EventContainer;
import com.rn.tool.gcalimporter.entity.impl.EventContainerFactory;
import com.rn.tool.gcalimporter.entity.impl.EventContainerType;
import com.rn.tool.gcalimporter.utils.GoogleDateUtils;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class GoogleCalendarClientTest {

  private static final String APPLICATION_NAME = GoogleCalendarClientTest.class.getName();

  private static final Path DATA_STORE_DIR = Paths.get(System.getProperty("user.dir"));
  private static final Path CLIENT_SECRET = Paths
      .get(System.getProperty("user.dir"), "client_secret.json");

  private static String GOOGLE_CALENDAR_ID;

  private static GoogleCalendarClient client;

  @BeforeClass
  public static void beforeClass() {
    client = new GoogleCalendarClient(APPLICATION_NAME, CLIENT_SECRET, DATA_STORE_DIR);
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
