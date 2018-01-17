//package com.rn.tool.arielgcal.client.ariel;
//
//import com.rn.tool.gcalimporter.client.loader.enums.Auth;
//import com.rn.tool.gcalimporter.client.loader.enums.DateRangeFieldType;
//import com.rn.tool.gcalimporter.client.loader.enums.HttpMethod;
//import com.rn.tool.gcalimporter.client.loader.enums.ReservedWord;
//import java.net.URI;
//import java.time.LocalDateTime;
//import java.time.ZonedDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.Map;
//import java.util.Optional;
//import lombok.Data;
//
//@Data
//public class CustomCalendarSetting {
//
//  private final Auth auth;
//  private final String userId;
//  private final String password;
//
//  private final URI targetUri;
//
//  private final HttpMethod method;
//  private final Map<String, String> parameters;
//
//  private final String dateFormat;
//  private final String dateTimeFormat;
//  private final String yearMonthFormat;
//
//  private final String startFieldName;
//  private final DateRangeFieldType startFieldType;
//  private final String endFieldName;
//  private final DateRangeFieldType endFieldType;
//
//  public Optional<String> getParameter(String paramId) {
//
//    final Optional<String> optVal = Optional.ofNullable(parameters.get(paramId));
//    if (optVal.isPresent()) {
//      final Optional<ReservedWord> reservedWordOpt = ReservedWord.fromWord(optVal.get());
//      if (reservedWordOpt.isPresent()) {
//        return Optional.ofNullable(convertReservedWord(reservedWordOpt.get()));
//      } else {
//        return optVal;
//      }
//    }
//    return Optional.empty();
//  }
//
//  private String convertReservedWord(ReservedWord word) {
//    switch (word) {
//      case TODAY:
//        final DateTimeFormatter dttmFormatter = DateTimeFormatter.ofPattern(this.dateTimeFormat);
//        return dttmFormatter.format(ZonedDateTime.now());
//      case THIS_MONTH: {
//        final DateTimeFormatter yearMonthFormatter = DateTimeFormatter
//            .ofPattern(this.yearMonthFormat);
//        return yearMonthFormatter.format(ZonedDateTime.now());
//      }
//      default: {
//        throw new RuntimeException("Not Supported");
//      }
//    }
//  }
//
//  public LocalDateTime getStartDttm() {
//    String dttmStr = parameters.get(startFieldName);
//    switch (startFieldType) {
//      case Date:
//        return LocalDateTime.parse(dttmStr, DateTimeFormatter.ofPattern(dateFormat));
//      case DateTime:
//        return LocalDateTime.parse(dttmStr, DateTimeFormatter.ofPattern(dateTimeFormat));
//      case YearMonth:
//        return LocalDateTime.parse(dttmStr, DateTimeFormatter.ofPattern(yearMonthFormat));
//      case YearRange:
//        throw new RuntimeException("Operation not supported");
//      case MonthRange:
//        throw new RuntimeException("Operation not supported");
//      case DayRange:
//        throw new RuntimeException("Operation not supported");
//      default:
//        throw new RuntimeException("Illegal state");
//    }
//  }
//
//  public LocalDateTime getEndDttm() {
//
//    String dttmStr = parameters.get(startFieldName);
//    int range;
//    LocalDateTime startdttm = getStartDttm();
//
//    switch (startFieldType) {
//      case Date:
//        return LocalDateTime.parse(dttmStr, DateTimeFormatter.ofPattern(dateFormat)).plusDays(1)
//            .minusMinutes(1);
//      case DateTime:
//        return LocalDateTime.parse(dttmStr, DateTimeFormatter.ofPattern(dateTimeFormat));
//      case YearMonth:
//        return LocalDateTime.parse(dttmStr, DateTimeFormatter.ofPattern(yearMonthFormat))
//            .plusDays(1).minusMinutes(1);
//      case YearRange:
//        range = Integer.parseInt(parameters.get(endFieldName));
//        return startdttm.plusYears(range).plusDays(1).minusMinutes(1);
//      case MonthRange:
//        range = Integer.parseInt(parameters.get(endFieldName));
//        return startdttm.plusMonths(range).plusDays(1).minusMinutes(1);
//      case DayRange:
//        range = Integer.parseInt(parameters.get(endFieldName));
//        return startdttm.plusDays(range).plusDays(1).minusMinutes(1);
//      default:
//        throw new RuntimeException("Illegal state");
//    }
//  }
//}
