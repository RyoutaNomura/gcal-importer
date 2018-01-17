//package com.rn.tool.arielgcal.client.ariel;
//
//import com.rn.tool.gcalimporter.utils.CalendarComponentUtils;
//import java.io.IOException;
//import java.io.StringReader;
//import java.util.List;
//import java.util.stream.Collectors;
//import javax.ws.rs.client.ClientBuilder;
//import javax.ws.rs.client.WebTarget;
//import lombok.RequiredArgsConstructor;
//import net.fortuna.ical4j.data.CalendarBuilder;
//import net.fortuna.ical4j.data.ParserException;
//import net.fortuna.ical4j.data.UnfoldingReader;
//import net.fortuna.ical4j.model.Component;
//import net.fortuna.ical4j.model.component.CalendarComponent;
//import org.apache.commons.lang3.StringUtils;
//import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
//import org.glassfish.jersey.logging.LoggingFeature;
//
//@RequiredArgsConstructor
//public class CustomCalendarLoader implements CalendarLoader {
//
//  private final CustomCalendarSetting setting;
//
//  @Override
////  public List<CalendarComponent> loadCalendar(final LocalDateTime start, final LocalDateTime end) {
//  public List<CalendarComponent> loadCalendar(CustomCalendarSetting param) {
//    final WebTarget wt = ClientBuilder.newClient()
//        .register(new LoggingFeature())
//        .register(
//            HttpAuthenticationFeature.basic(
//                this.setting.getParameter(param.getUserId()).orElse(""),
//                this.setting.getParameter(param.getPassword()).orElse("")
//            )
//        ).target(this.setting.getTargetUri());
//
//    param.getParameters().forEach((k, v) -> {
//      wt.queryParam(k, v);
//    });
//
//    final String iCal = wt.request().get(String.class);
//
//    try {
//      return new CalendarBuilder().build(new UnfoldingReader(new StringReader(iCal)))
//          .getComponents().stream()
//          .filter(c -> StringUtils.equals(c.getName(), Component.VEVENT))
//          .filter(c -> CalendarComponentUtils
//              .isInTargetYearMonth(param.getStartDttm(), param.getEndDttm(), c))
//          .collect(Collectors.toList());
//    } catch (IOException | ParserException e) {
//      throw new RuntimeException(e);
//    }
//  }
//
//  @Override
//  public boolean verifyAuthentication() {
//    return true;
//  }
//
//  @Override
//  public String getConnectionInformation(int keyLength) {
//    return null;
//  }
//}
