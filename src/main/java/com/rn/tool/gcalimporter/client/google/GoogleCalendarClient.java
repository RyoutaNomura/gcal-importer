package com.rn.tool.gcalimporter.client.google;

import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpHeaders;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.google.common.collect.Maps;
import com.rn.tool.gcalimporter.entity.EventContainer;
import com.rn.tool.gcalimporter.entity.impl.EventContainerFactory;
import com.rn.tool.gcalimporter.entity.impl.EventContainerFactory.EventContainerType;
import com.rn.tool.gcalimporter.helper.Counter;
import com.rn.tool.gcalimporter.utils.DateUtils;
import com.rn.tool.gcalimporter.utils.GoogleDateUtils;
import java.io.File;
import java.io.IOException;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import jersey.repackaged.com.google.common.collect.Lists;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.jooq.lambda.Unchecked;
import org.jooq.lambda.function.Consumer3;

/**
 * Class of API client of Google Calendar
 */
@Slf4j
public class GoogleCalendarClient {

  /**
   * Access scope of GoogleCalendar
   */
  private static final List<String> SCOPES = Lists.newArrayList(CalendarScopes.all());
  /**
   * Timeout for GoogleCalendarService
   */
  private static final int TIME_OUT = 3 * 60000;
  /**
   * Number of processes to be executed in one batch
   */
  private static final int BATCH_LIMIT_COUNT = 50;
  /**
   * Execution interval of batch
   */
  private static final int INTERVAL = 50 * 1000;
  /**
   * Instance of the holder of GoogleCalendarService
   */
  private final GoogleCalendarServiceHolder calendarService;

  /**
   * Constructor
   *
   * @param applicationName Name of application which is used to access to google calendar
   * @param dataStoreDir Path to store credential
   */
  public GoogleCalendarClient(@NonNull final String applicationName,
      @NonNull final File dataStoreDir) {
    this.calendarService = GoogleCalendarServiceHolder.builder()
        .applicationName(applicationName)
        .dataStoreDir(dataStoreDir)
        .scopes(SCOPES)
        .timeout(TIME_OUT)
        .build();
  }

  /**
   * Method for create JsonBatchCallBack template
   *
   * @return callback template
   */
  private static <T> JsonBatchCallback<T> createCallBack(@NonNull final RequestType methodType,
      @NonNull final EventContainer event, @NonNull final Map<String, Optional<Boolean>> ret) {

    return new JsonBatchCallback<T>() {
      @Override
      public void onSuccess(final T t, final HttpHeaders responseHeaders) {
        ret.put(event.getKey(), Optional.of(true));
        log.info(String.format("Success to %s: %s", methodType.getVal(), event.getKey()));
      }

      @Override
      public void onFailure(final GoogleJsonError e, final HttpHeaders responseHeaders) {
        ret.put(event.getKey(), Optional.of(false));
        log.error(String
            .format("Fail to %s: %s, caused: %s", methodType.getVal(), event.getKey(),
                e.getMessage()));
        log.debug(String.format("  Error Event: %s", event.getEvent().toString()));
      }
    };
  }

  /**
   * Method for execute insert/update/delete queries with batch operation.
   *
   * returns when all callback function are called.
   *
   * @param requestType request
   * @param c Google calendar service
   * @param cid target calendar id of Google Calendar
   * @param s Target events
   */
  private static void executeRequest(@NonNull final RequestType requestType,
      @NonNull final Calendar c, @NonNull final String cid,
      @NonNull final Stream<EventContainer> s) {

    final Map<String, Optional<Boolean>> ret = Maps.newHashMap();

    final BatchRequest request = c.batch();
    s.forEach(Unchecked.consumer(w -> {

      ret.put(w.getKey(), Optional.empty());

      switch (requestType) {
        case INSERT:
          c.events().insert(cid, w.getEvent())
              .queue(request, createCallBack(RequestType.INSERT, w, ret));
          break;
        case UPDATE:
          c.events().update(cid, w.getEvent().getId(), w.getEvent())
              .queue(request, createCallBack(RequestType.UPDATE, w, ret));
          break;
        case DELETE:
          c.events().delete(cid, w.getEvent().getId())
              .queue(request, createCallBack(RequestType.DELETE, w, ret));
          break;
        default:
          throw new RuntimeException("UnknownType");
      }
    }));

    try {
      if (request.size() > 0) {
        request.execute();
      }
      while (ret.values().contains(Optional.empty())) {
        Thread.sleep(100);
      }
    } catch (InterruptedException | IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Method for create new calender to Google Calender
   *
   * @param summary Calendar's summary
   * @return calendarId of created calendar
   */
  public String createNewCalendar(@NonNull final String summary) {
    final Calendar calendar = this.calendarService.get();
    try {
      final com.google.api.services.calendar.model.Calendar ret = calendar.calendars()
          .insert(new com.google.api.services.calendar.model.Calendar().setSummary(summary))
          .execute();
      return ret.getId();

    } catch (final IOException e) {
      throw new RuntimeException(e);

    }
  }

  /**
   * Method for delete existing calender from Google Calendar
   *
   * @param id id of the calender to delete
   */
  public void deleteCalendar(@NonNull final String id) {
    final Calendar calendar = this.calendarService.get();
    try {
      calendar.calendars().delete(id).execute();
    } catch (final GoogleJsonResponseException e) {
      log.info(String.format("%s does not exist", id));
      log.warn(e.getLocalizedMessage());

    } catch (final IOException e) {
      throw new RuntimeException(e);

    }
  }

  /**
   * Method for find calender with calendarId
   *
   * @param id id of the calender to find
   * @return result if found
   */
  public Optional<com.google.api.services.calendar.model.Calendar> findCalendar(
      @NonNull final String id) {

    final Calendar calendar = this.calendarService.get();

    try {
      return Optional.of(calendar.calendars().get(id).execute());

    } catch (final GoogleJsonResponseException e) {
      log.warn(e.getLocalizedMessage());
      return Optional.empty();

    } catch (final IOException e) {
      throw new RuntimeException(e);

    }
  }

  /**
   * Method for execute requests divided by batch size with batch operation
   *
   * @param calendar Google Calendar Service
   * @param calendarId Target calendar Id of Google Calendar
   * @param stream Data to process
   * @param batchSize Size of execute batch at once
   * @param consumer consumer
   * @param <T> The class implemented {@link EventContainer}
   */
  private <T extends EventContainer> void executeBatch(@NonNull final Calendar calendar,
      @NonNull final String calendarId, @NonNull final Stream<T> stream,
      @NonNull final int batchSize,
      @NonNull final Consumer3<Calendar, String, Stream<T>> consumer) {

    final Counter counter = Counter.create(0);

    stream.map(t -> Pair.of(counter.increment(), t))
        .collect(Collectors.groupingBy(p -> p.getKey() / BATCH_LIMIT_COUNT))
        .values()
        .forEach(Unchecked.consumer(l -> {
          consumer.accept(calendar, calendarId, l.stream().map(Pair::getValue));
          if (l.size() == batchSize) {
            log.info("Waiting next execution..." +
                ZonedDateTime.now().plus(INTERVAL, ChronoUnit.MILLIS));
            Thread.sleep(INTERVAL);
          }
        }));
  }

  /**
   * Method for insert events
   *
   * @param calendarId calendar id to insert
   * @param events events to insert
   */
  public void insertEvents(@NonNull final String calendarId,
      @NonNull final List<EventContainer> events) {
    final com.google.api.services.calendar.Calendar service = this.calendarService.get();
    this.executeBatch(service, calendarId, events.stream(), BATCH_LIMIT_COUNT,
        (c, cid, s) -> GoogleCalendarClient.executeRequest(RequestType.INSERT, c, cid, s));
  }

  /**
   * Method for update events
   *
   * @param calendarId calendar id to update
   * @param events events to update
   */
  public void updateEvents(@NonNull final String calendarId,
      @NonNull final List<EventContainer> events) {
    final com.google.api.services.calendar.Calendar service = this.calendarService.get();
    this.executeBatch(service, calendarId, events.stream(), BATCH_LIMIT_COUNT,
        (c, cid, s) -> GoogleCalendarClient.executeRequest(RequestType.UPDATE, c, cid, s));
  }

  /**
   * Method for delete events
   *
   * @param calendarId calendar id to delete
   * @param events events to delete
   */
  public void deleteEvents(@NonNull final String calendarId,
      @NonNull final List<EventContainer> events) {
    final com.google.api.services.calendar.Calendar service = this.calendarService.get();
    this.executeBatch(service, calendarId, events.stream(), BATCH_LIMIT_COUNT,
        (c, cid, s) -> GoogleCalendarClient.executeRequest(RequestType.DELETE, c, cid, s));
  }

  /**
   * Method for delete events in certain year and month
   *
   * @param calendarId calendar id to delete
   * @param yearMonth year and month to delete
   */
  public void deleteEvents(@NonNull final String calendarId, @NonNull final YearMonth yearMonth) {
    final List<EventContainer> events = this.findEvents(calendarId, yearMonth,
        1, EventContainerType.Raw);
    this.deleteEvents(calendarId, events);
  }

  /**
   * Method for find events in specific year and month
   *
   * @param calendarId Calendar id to find events
   * @param yearMonth Target year and month
   * @param range Months additionally obtained from yearMonth
   * @param ecType The class to return
   * @return Events located in target yearMonth
   */
  public List<EventContainer> findEvents(@NonNull final String calendarId,
      @NonNull final YearMonth yearMonth, final int range,
      @NonNull final EventContainerType ecType) {

    final com.google.api.services.calendar.Calendar service = this.calendarService.get();
    final ZonedDateTime min = DateUtils.fromYearMonth(yearMonth);
    final ZonedDateTime max = min.plusMonths(range);

    final List<Event> eventList = Lists.newArrayList();
    String pageToken = null;

    try {
      do {
        Events events = service.events().list(calendarId)
            .setTimeMin(GoogleDateUtils.fromZonedDateTimeToDateTime(min))
            .setTimeMax(GoogleDateUtils.fromZonedDateTimeToDateTime(max))
            .setPageToken(pageToken)
            .execute();
        eventList.addAll(events.getItems());
        pageToken = events.getNextPageToken();
      } while (pageToken != null);

      return eventList.stream()
          .map(e -> EventContainerFactory.create(ecType, e))
          .collect(Collectors.toList());

    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Enum to specify which request to execute
   */
  public enum RequestType {
    INSERT("Insert"),
    UPDATE("Update"),
    DELETE("Delete"),;

    @Getter
    private final String val;

    RequestType(String val) {
      this.val = val;
    }
  }
}