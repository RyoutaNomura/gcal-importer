package com.rn.tool.gcalimporter.logic;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.rn.tool.gcalimporter.client.google.GoogleCalendarClient;
import com.rn.tool.gcalimporter.client.loader.CalendarLoader;
import com.rn.tool.gcalimporter.common.Constants;
import com.rn.tool.gcalimporter.entity.EventContainer;
import com.rn.tool.gcalimporter.entity.impl.EventContainerFactory;
import com.rn.tool.gcalimporter.entity.impl.EventContainerType;
import com.rn.tool.gcalimporter.utils.GoogleDateUtils;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * Class for Transferring Calendar from Ariel to Google
 */
@Slf4j
public class CalendarTransfer {

  /**
   * Ariel Calendar Client
   */
  @Inject
  private CalendarLoader calendarLoader;

  /**
   * Client of Google Calendar
   */
  @Inject
  private GoogleCalendarClient gcalClient;


  /**
   * Method for Transferring Calendar from Ariel to Google
   *
   * @param googleCalendarId Target Calendar ID
   * @param targetYearMonth Target Year Month
   * @param targetRange Target Range
   */
  @SuppressWarnings("unchecked")
  public void transfer(@NonNull final String googleCalendarId, final YearMonth targetYearMonth,
      final int targetRange) {

    logParameter(googleCalendarId, targetYearMonth, targetRange);

    // Get events from Ariel
    final Map<String, EventContainer> compassEvents = this.calendarLoader
        .loadCalendar(targetYearMonth, targetRange)
        .stream()
        .map(c -> EventContainerFactory.create(EventContainerType.Ariel, c))
        .collect(Collectors.toMap(EventContainer::getKey, e -> e));

    // Get events from Google Calendar
    final Map<String, EventContainer> gcalEvents = this.gcalClient
        .findEvents(googleCalendarId, targetYearMonth, targetRange, EventContainerType.Ariel)
        .stream()
        .filter(EventContainer::hasKey)
        .collect(Collectors.toMap(EventContainer::getKey, e -> e));

    final List<EventContainer> eventsToUpdate = Lists.newArrayList();
    final List<EventContainer> eventsToDelete = Lists.newArrayList();

    final Set<String> ids = Sets.newHashSet();

    gcalEvents.values().forEach(ge -> {
      Optional<EventContainer> compassEvent = Optional.ofNullable(compassEvents.get(ge.getKey()));
      if (compassEvent.isPresent()) {
        // キャンセル済みでない場合
        if (!ge.hasSameContents(compassEvent.get())) {
          // サマリと内容が異なっている場合は -> Update
          eventsToUpdate.add(ge.createPatchedEvent(compassEvent.get()));
        }
      } else {
        // キャンセル済みでない場合
        if (GoogleDateUtils.contains(targetYearMonth, ge.getEvent().getStart())) {
          // 期間が対象期間内 -> DELETE
          eventsToDelete.add(ge);
        }
      }
      // キーのセットを追加
      ids.add(ge.getKey());
    });

    // 追加判定
    List<EventContainer> eventsToAdd = compassEvents.values().stream()
        .filter(ge -> !ids.contains(ge.getKey())).collect(Collectors.toList());

    this.logExecutionInfo(googleCalendarId, targetYearMonth, targetRange, eventsToAdd,
        eventsToUpdate,
        eventsToDelete);

    this.gcalClient.deleteEvents(googleCalendarId, eventsToDelete);
    this.gcalClient.updateEvents(googleCalendarId, eventsToUpdate);
    this.gcalClient.insertEvents(googleCalendarId, eventsToAdd);

    log.info("CalendarTransfer is done");
  }

  /**
   * Method for Logging Execution Information
   *
   * @param googleCalendarId Target Calendar ID
   * @param targetYearMonth Target Year Month
   * @param targetRange Target Range
   */
  private void logParameter(@NonNull final String googleCalendarId,
      @NonNull final YearMonth targetYearMonth, final int targetRange) {

    log.info("CalendarTransfer is executed with following parameter");
    log.info(StringUtils.repeat('-', Constants.SEP_LENGTH));
    log.info("# From: ");
    this.calendarLoader.getConnectionInfo()
        .entrySet()
        .stream()
        .map(entry -> String.format("%s: %s", entry.getKey(), entry.getValue()))
        .forEach(log::info);
    log.info(StringUtils.EMPTY);
    log.info("# To: ");
    log.info("Google Calendar ID: " + googleCalendarId);
    log.info(StringUtils.EMPTY);
    log.info("# Target");
    log.info("YearMonth: " + targetYearMonth);
    log.info("TargetRange: " + targetRange);
    log.info(StringUtils.repeat('-', Constants.SEP_LENGTH));
  }

  private void logExecutionInfo(@NonNull final String googleCalendarId,
      @NonNull final YearMonth targetYearMonth, final int range,
      @NonNull final List<EventContainer> insList, @NonNull final List<EventContainer> updList,
      @NonNull final List<EventContainer> delList) {

    log.info(StringUtils.repeat('=', Constants.SEP_LENGTH));
    log.info("Ariel Calendar Transfer");
    log.info(StringUtils.repeat('=', Constants.SEP_LENGTH));
    log.info(String.format("Summary (Executed: %s)", ZonedDateTime.now().toString()));
    log.info(StringUtils.repeat('-', Constants.SEP_LENGTH));
    log.info("Parameters");
    log.info(StringUtils.repeat('-', Constants.SEP_LENGTH));
    this.calendarLoader.getConnectionInfo().entrySet().stream().map(entry ->
        String.format("%-" + Constants.KEY_LENGTH + "s: %s", entry.getKey(), entry.getValue())
    ).forEach(log::info);
//    this.calendarLoader.getConnectionInformation(Constants.KEY_LENGTH).forEach(log::info);
    log.info(
        String.format("%-" + Constants.KEY_LENGTH + "s: %s", "Google Calendar CalendarId",
            googleCalendarId));
    log.info(
        String.format("%-" + Constants.KEY_LENGTH + "s: %s", "Target YearMonth",
            targetYearMonth.toString()));
    log.info(
        String.format("%-" + Constants.KEY_LENGTH + "s: %s", "Range", range));
    log.info(StringUtils.repeat('-', Constants.SEP_LENGTH));
    log.info("Plan");
    log.info(StringUtils.repeat('-', Constants.SEP_LENGTH));
    log.info(String.format("%-" + Constants.KEY_LENGTH + "s: %5d", "1. Insert", insList.size()));
    log.info(String.format("%-" + Constants.KEY_LENGTH + "s: %5d", "2. Update", updList.size()));
    log.info(String.format("%-" + Constants.KEY_LENGTH + "s: %5d", "3. Delete", delList.size()));
    log.info(StringUtils.repeat('=', Constants.SEP_LENGTH));
    if (insList.size() != 0 || updList.size() != 0 || delList.size() != 0) {
      log.info("Detail");
      log.info(StringUtils.repeat('-', Constants.SEP_LENGTH));
      log.info(String.format("%-12s%-50s%s", "Mode", "Id", "Summary"));
      log.info(StringUtils.repeat('-', Constants.SEP_LENGTH));
      insList.stream().map(
          s -> String.format("%-12s%-50s%s", "1. INSERT", s.getKey(), s.getEvent().getSummary()))
          .forEach(log::info);
      updList.stream().map(
          s -> String.format("%-12s%-50s%s", "2. UPDATE", s.getKey(), s.getEvent().getSummary()))
          .forEach(log::info);
      delList.stream().map(
          s -> String.format("%-12s%-50s%s", "3. DELETE", s.getKey(), s.getEvent().getSummary()))
          .forEach(log::info);
      log.info(StringUtils.repeat('=', Constants.SEP_LENGTH));
    }
  }
}