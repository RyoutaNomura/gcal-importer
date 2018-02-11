package com.rn.tool.gcalimporter.client.loader;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import com.rn.tool.gcalimporter.client.loader.annotation.ArielAuthService;
import com.rn.tool.gcalimporter.client.loader.annotation.ArielICalService;
import com.rn.tool.gcalimporter.client.loader.setting.ArielCalendarServiceSetting;
import com.rn.tool.gcalimporter.utils.CalendarComponentUtils;
import java.io.IOException;
import java.io.StringReader;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.data.UnfoldingReader;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.component.CalendarComponent;
import org.apache.commons.lang3.StringUtils;

/**
 * Class of API Client for Ariel Calendar
 */
@Slf4j
//@Builder
public class CalendarLoaderArielImpl implements CalendarLoader {

  /**
   * Constant representing Parameter of Target Year Month
   */
  private static final String PARAM_TARGET_YEAR_MONTH = "target";
  /**
   * Constant representing Parameter of Target Range
   */
  private static final String PARAM_TARGET_RANGE = "target_range";
  /**
   * Formatter for calendar parameter
   */
  private final DateTimeFormatter yearMonthFormatter = DateTimeFormatter
      .ofPattern("yyyy-MM");

  @Inject
  private ArielCalendarServiceSetting setting;

  @Inject
  @ArielAuthService
  private WebTarget arielAuthService;

  @Inject
  @ArielICalService
  private WebTarget arielCalendarService;


  @Override
  public List<CalendarComponent> loadCalendar(@NonNull final YearMonth yearMonth, final int range) {

    if (Objects.isNull(yearMonth)) {
      throw new RuntimeException("yearMonth is empty");
    }
    if (range <= 0) {
      throw new RuntimeException("range should be larger than 0");
    }

    if (!verifyAuthentication()) {
      throw new RuntimeException("Authentication failed");
    }

    final WebTarget wt = this.arielCalendarService
        .queryParam(PARAM_TARGET_YEAR_MONTH, yearMonth.format(yearMonthFormatter))
        .queryParam(PARAM_TARGET_RANGE, range);

    log.info("Calendar data is loading...");

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
    final Response res = this.arielAuthService.request().get();
    return StringUtils.contains(res.readEntity(String.class), "OK");
  }

  @Override
  public Map<String, String> getConnectionInfo() {
    return ImmutableMap.of(
        "URL", this.setting.getUri().toString(),
        "User Name", this.setting.getUsername()
    );
  }
}
