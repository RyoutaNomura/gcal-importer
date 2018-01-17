package com.rn.tool.gcalimporter.logic;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.rn.tool.gcalimporter.client.loader.ArielCalendarLoader;
//import com.rn.tool.gcalimporter.client.loader.CustomCalendarSetting;
import com.rn.tool.gcalimporter.client.google.GoogleCalendarClient;
import com.rn.tool.gcalimporter.entity.EventContainer;
import com.rn.tool.gcalimporter.entity.impl.EventContainerFactory;
import com.rn.tool.gcalimporter.entity.impl.EventContainerFactory.EventContainerType;
import com.rn.tool.gcalimporter.utils.GoogleDateUtils;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class ArielCalenderTransfer {

  private static final String GOOGLE_APPLICATION_NAME = "ArielCalendarTransfer";

  private static final java.io.File DATA_STORE_DIR = new java.io.File(
      System.getProperty("user.dir"), "src/main/resources");
  private static final int KEY_LENGTH = 30;
  private static final int SEP_LENGTH = 80;
  private final ArielCalendarLoader compassClient;
  private final GoogleCalendarClient gcalClient;

  public ArielCalenderTransfer(final String uri, final String username, final String password) {
    this.compassClient = new ArielCalendarLoader(uri, username, password);
    this.gcalClient = new GoogleCalendarClient(GOOGLE_APPLICATION_NAME, DATA_STORE_DIR);
  }

  public void transfer(final String googleCalendarId, YearMonth targetYearMonth, int targetRange) {

    // Get events from COMPASS
    final Map<String, EventContainer> compassEvents = this.compassClient
        .loadCalendar(targetYearMonth, targetRange)
        .stream()
        .map(c -> EventContainerFactory.create(EventContainerFactory.EventContainerType.Ariel, c))
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

    this.log(googleCalendarId, targetYearMonth, eventsToAdd, eventsToUpdate, eventsToDelete);

    this.gcalClient.deleteEvents(googleCalendarId, eventsToDelete);
    this.gcalClient.updateEvents(googleCalendarId, eventsToUpdate);
    this.gcalClient.insertEvents(googleCalendarId, eventsToAdd);

    log.info("Done.");

  }

  private void log(final String googleCalendarId, final YearMonth targetYearMonth,
      final List<EventContainer> insList, final List<EventContainer> updList,
      final List<EventContainer> delList) {
    log.info(System.lineSeparator());
    log.info(StringUtils.repeat('=', SEP_LENGTH));
    log.info("Ariel Calendar Transfer");
    log.info(StringUtils.repeat('=', SEP_LENGTH));
    log.info(String.format("Summary (Executed: %s)", ZonedDateTime.now().toString()));
    log.info(StringUtils.repeat('-', SEP_LENGTH));
    log.info("Parameters");
    log.info(StringUtils.repeat('-', SEP_LENGTH));
    log.info(this.compassClient.getConnectionInformation(KEY_LENGTH));
    log.info(
        String.format("%-" + KEY_LENGTH + "s: %s", "Google Calendar CalendarId", googleCalendarId));
    log.info(
        String.format("%-" + KEY_LENGTH + "s: %s", "Target YearMonth", targetYearMonth.toString()));
    log.info(StringUtils.repeat('-', SEP_LENGTH));
    log.info("Plan");
    log.info(StringUtils.repeat('-', SEP_LENGTH));
    log.info(String.format("%-" + KEY_LENGTH + "s: %5d", "1. Insert", insList.size()));
    log.info(String.format("%-" + KEY_LENGTH + "s: %5d", "2. Update", updList.size()));
    log.info(String.format("%-" + KEY_LENGTH + "s: %5d", "3. Delete", delList.size()));
    log.info(StringUtils.repeat('=', SEP_LENGTH));
    if (insList.size() != 0 || updList.size() != 0 || delList.size() != 0) {
      log.info("Detail");
      log.info(StringUtils.repeat('-', SEP_LENGTH));
      log.info(String.format("%-12s%-50s%s", "Mode", "Id", "Summary"));
      log.info(StringUtils.repeat('-', SEP_LENGTH));
      insList.stream().map(
          s -> String.format("%-12s%-50s%s", "1. INSERT", s.getKey(), s.getEvent().getSummary()))
          .forEach(log::info);
      updList.stream().map(
          s -> String.format("%-12s%-50s%s", "2. UPDATE", s.getKey(), s.getEvent().getSummary()))
          .forEach(log::info);
      delList.stream().map(
          s -> String.format("%-12s%-50s%s", "3. DELETE", s.getKey(), s.getEvent().getSummary()))
          .forEach(log::info);
      log.info(StringUtils.repeat('=', SEP_LENGTH));
    }
  }
}