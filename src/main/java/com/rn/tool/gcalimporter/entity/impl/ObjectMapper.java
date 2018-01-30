package com.rn.tool.gcalimporter.entity.impl;

import com.google.api.services.calendar.model.EventDateTime;
import com.rn.tool.gcalimporter.utils.CalendarComponentUtils;
import com.rn.tool.gcalimporter.utils.GoogleDateUtils;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;

/**
 * Class of PropertyList of Calendar's mapper
 */
@Slf4j
class ObjectMapper {

  /**
   * Method for mapping single value in {@link PropertyList}
   *
   * @param props {@link PropertyList} which contains target value
   * @param propName target property's name
   * @param mapper {@link Function} for mapping
   * @param propValConsumer {@link Consumer} called after value converted
   * @param <E> type of mapped value
   */
  private static <E> void map(final PropertyList<Property> props, final String propName,
      final Function<Property, E> mapper, final Consumer<E> propValConsumer) {
    try {
      Optional.ofNullable(props.getProperty(propName))
          .ifPresent(p -> propValConsumer.accept(mapper.apply(p)));
    } catch (RuntimeException e) {
      log.error("An Error Occurred with following property");
      log.error(props.toString());
      throw e;
    }
  }

  /**
   * Method for mapping two values in PropertyList
   *
   * @param props {@link PropertyList} which contains target value
   * @param propName1 target property's name
   * @param propName2 target property's name
   * @param mapper {@link Function} for mapping
   * @param propValConsumer {@link Consumer} called after the value converted
   * @param <E> type of mapped value
   */
  public static <E> void map(final PropertyList<Property> props, final String propName1,
      final String propName2,
      final BiFunction<Property, Property, E> mapper, final Consumer<E> propValConsumer) {
    final Property prop1 = props.getProperty(propName1);
    final Property prop2 = props.getProperty(propName2);
    propValConsumer.accept(mapper.apply(prop1, prop2));
  }

  /**
   * Method for mapping string value in {@link PropertyList}
   *
   * @param props {@link PropertyList} which contains target value
   * @param propName target property's name
   * @param propValConsumer {@link Consumer} called after the valued converted
   */
  public static void mapString(final PropertyList<Property> props, final String propName,
      final Consumer<String> propValConsumer) {
    ObjectMapper.map(props, propName, Property::getValue, propValConsumer);
  }

  /**
   * Method for mapping {@link EventDateTime} value in {@link PropertyList} as DateTime
   *
   * @param props {@link PropertyList} which contains target value
   * @param propName target property's name
   * @param propValConsumer {@link Consumer} called after the valued converted
   */
  public static void mapEventDateTime(final PropertyList<Property> props, final String propName,
      final Consumer<EventDateTime> propValConsumer) {
    ObjectMapper.map(
        props,
        propName,
        p -> GoogleDateUtils.parseAsDttm(
            p.getValue(),
            CalendarComponentUtils.ICAL_DATE_TIME_FORMAT,
            ZoneId.of(p.getParameter(Parameter.TZID).getValue())
        ),
        propValConsumer);
  }

  /**
   * Method for mapping {@link EventDateTime} value in {@link PropertyList} as Date
   *
   * @param props {@link PropertyList} which contains target value
   * @param propName target property's name
   * @param propValConsumer {@link Consumer} called after the valued converted
   */
  public static void mapEventDate(final PropertyList<Property> props, final String propName,
      final Consumer<EventDateTime> propValConsumer) {
    ObjectMapper.map(
        props,
        propName,
        p -> GoogleDateUtils.parseAsDate(
            p.getValue(),
            CalendarComponentUtils.ICAL_DATE_FORMAT
        ),
        propValConsumer);
  }

  /**
   * Method for mapping BigDecimal value in {@link PropertyList}
   *
   * @param props {@link PropertyList} which contains target value
   * @param propName target property's name
   * @param propValConsumer {@link Consumer} called after the valued converted
   */
  public static void mapBigDecimal(final PropertyList<Property> props, final String propName,
      final Consumer<BigDecimal> propValConsumer) {
    ObjectMapper.map(props, propName, p -> new BigDecimal(p.getValue()), propValConsumer);
  }
}
