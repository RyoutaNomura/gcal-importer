package com.rn.tool.gcalimporter.client.loader;

import com.google.common.collect.Lists;
import com.rn.tool.gcalimporter.utils.CalendarComponentUtils;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.data.UnfoldingReader;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.component.CalendarComponent;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.logging.LoggingFeature;

/**
 * Class of API Client for Ariel Calendar
 */
@Slf4j
@Builder
public class ArielCalendarLoader implements CalendarLoader {

  /**
   * Constant representing Parameter of Target Year Month
   */
  private static final String PARAM_TARGET_YEAR_MONTH = "target";
  /**
   * Constant representing Parameter of Target Range
   */
  private static final String PARAM_TARGET_RANGE = "range";
  /**
   * Formatter for calender parameter
   */
  @Getter
  private final DateTimeFormatter yearMonthFormatter = DateTimeFormatter
      .ofPattern("yyyy-MM");
  /**
   * URI to connect
   */
  @Getter
  private final URI uri;
  /**
   * Path for authentication
   */
  @Getter
  private final String authPath;
  /**
   * Path for access to calendar
   */
  @Getter
  private final String icalPath;
  /**
   * User Name
   */
  @Getter
  private final String username;
  /**
   * Password
   */
  private final String password;

  @Override
  public List<CalendarComponent> loadCalendar(@NonNull final YearMonth yearMonth,
      @NonNull final int range) {

    if (Objects.isNull(yearMonth)) {
      throw new RuntimeException("yearMonth is empty");
    }
    if (range <= 0) {
      throw new RuntimeException("range should be larger than 0");
    }

    if (!verifyAuthentication()) {
      throw new RuntimeException("Authentication failed");
    }

    final WebTarget wt = ClientBuilder.newClient().register(new LoggingFeature())
        .register(HttpAuthenticationFeature.basic(this.username, this.password)).target(this.uri)
        .path(icalPath)
        .queryParam(PARAM_TARGET_YEAR_MONTH, yearMonth.format(yearMonthFormatter))
        .queryParam(PARAM_TARGET_RANGE, range);

    log.info("Calender data is loading...");

    final String iCal = wt.request().get(String.class);

    try {
      return new CalendarBuilder().build(new UnfoldingReader(new StringReader(iCal)))
          .getComponents().stream()
          .filter(c -> StringUtils.equals(c.getName(), Component.VEVENT))
          .filter(c -> CalendarComponentUtils.isInTargetYearMonth(yearMonth, range, c))
          .collect(Collectors.toList());
    } catch (IOException | ParserException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public boolean verifyAuthentication() {
    final Response res = ClientBuilder.newClient()
        .register(new LoggingFeature())
        .register(HttpAuthenticationFeature.basic(this.username, this.password))
        .target(this.uri)
        .path(authPath)
        .request()
        .get();
    return StringUtils.contains(res.readEntity(String.class), "OK");
  }

  @Override
  public List<String> getConnectionInformation(@NonNull final int keyLength) {
    return Lists.newArrayList(
        String.format("%-" + keyLength + "s: %s", "COMPASS URL", this.uri),
        String.format("%-" + keyLength + "s: %s", "COMPASS User Name", this.username)
    );
  }
}
