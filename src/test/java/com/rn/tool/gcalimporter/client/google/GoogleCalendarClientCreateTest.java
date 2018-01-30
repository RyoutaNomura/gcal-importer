package com.rn.tool.gcalimporter.client.google;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

import com.google.api.services.calendar.model.Calendar;
import com.google.common.collect.Lists;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class GoogleCalendarClientCreateTest {

  private static final String APPLICATION_NAME = GoogleCalendarClientCreateTest.class.getName();

  private static final Path DATA_STORE_DIR = Paths.get(
      System.getProperty("user.dir"), "src/main/resources");
  private static final Path CLIENT_SECRET = Paths.get("client_secret.json");

  private static final List<String> calendarIds = Lists.newArrayList();
  private static GoogleCalendarClient client;

  @BeforeClass
  public static void init() {
    client = new GoogleCalendarClient(APPLICATION_NAME, CLIENT_SECRET, DATA_STORE_DIR);
  }

  @AfterClass
  public static void finish() {
    calendarIds.forEach(client::deleteCalendar);
  }

  @Test
  public void testFindCalendar() {
    final Optional<Calendar> actual = client.findCalendar(UUID.randomUUID().toString());
    assertThat(actual, is(Optional.empty()));
  }

  @Test
  public void testCreateCalendar() {
    final String expected = ZonedDateTime.now().toString();

    final String id = client.createNewCalendar(expected);
    calendarIds.add(id);

    final Optional<Calendar> actual = client.findCalendar(id);

    if (actual.isPresent()) {
      assertThat(actual.get().getSummary(), is(expected));
    } else {
      fail();
    }
  }

  @Test
  public void testDeleteCalendar() {
    final String expected = ZonedDateTime.now().toString();

    final String id = client.createNewCalendar(expected);
    calendarIds.add(id);

    if (!client.findCalendar(id).isPresent()) {
      fail("Calendar is not created");
    }

    client.deleteCalendar(id);

    final Optional<Calendar> actual = client.findCalendar(id);

    assertThat(actual, is(Optional.empty()));

  }

}
