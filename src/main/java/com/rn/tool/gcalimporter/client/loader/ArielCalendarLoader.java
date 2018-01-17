package com.rn.tool.gcalimporter.client.loader;

import com.rn.tool.gcalimporter.utils.CalendarComponentUtils;
import java.io.IOException;
import java.io.StringReader;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class ArielCalendarLoader implements CalendarLoader {

  /**
   * Path for authentication
   */
  private static final String AUTH_PATH = "/aqua/api/auth";

  /**
   * Path for access to calendar
   */
  private static final String ICAL_PATH = "/aqua/api/ical";

  /**
   * Formatter for calender parameter
   */
  private static final DateTimeFormatter YEAR_MONTH_FORMATTER = DateTimeFormatter
      .ofPattern("yyyy-MM");

  /**
   * URI to connect
   */
  private final String uri;

  /**
   * User Name
   */
  private final String username;

  /**
   * Password
   */
  private final String password;


  @Override
  public List<CalendarComponent> loadCalendar(final YearMonth yearMonth, final int range) {
//  public List<CalendarComponent> loadCalendar(final LocalDateTime start, final LocalDateTime end) {
//  public List<CalendarComponent> loadCalendar(CustomCalendarSetting param) {

//    LocalDateTime start = param.getStartDttm();
//    LocalDateTime end = param.getEndDttm();

//    // validate arguments
//    if (Objects.isNull(start)) {
//      throw new RuntimeException("start is null");
//    }
//    if (Objects.isNull(end)) {
//      throw new RuntimeException("end is null");
//    }


    if (Objects.isNull(yearMonth)) {
      throw new RuntimeException("yearMonth is empty");
    }
    if (range <= 0) {
      throw new RuntimeException("range should be larger than 0");
    }

    if (!verifyAuthentication()) {
      throw new RuntimeException("Authentication failed");
    }

//    final YearMonth yearMonth = YearMonth.of(start.getYear(), start.getMonth());
//    final int range = end.getMonthValue() - start.getMonthValue() + 1;

    final WebTarget wt = ClientBuilder.newClient().register(new LoggingFeature())
        .register(HttpAuthenticationFeature.basic(this.username, this.password)).target(this.uri)
        .path(ICAL_PATH);

    wt.queryParam("target", yearMonth.format(YEAR_MONTH_FORMATTER));
    wt.queryParam("target_range", range);

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
        .path(AUTH_PATH)
        .request()
        .get();
    return StringUtils.contains(res.readEntity(String.class), "OK");
  }

  @Override
  public String getConnectionInformation(final int keyLength) {
    return new StringBuilder()
        .append(String.format("%-" + keyLength + "s: %s", "COMPASS URL", this.uri))
        .append(String.format("%-" + keyLength + "s: %s", "COMPASS User Name", this.username))
        .toString();
  }
}
