package com.rn.tool.gcalimporter.misc;

import java.time.YearMonth;
import java.util.Arrays;
import java.util.Optional;
import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

/**
 * Enum for representing ReservedWord
 */
public enum ReservedWord {
  ThisMonth("THIS_MONTH"),
  LastMonth("LAST_MONTH"),
  TwoMonthAgo("TWO_MONTH_AGO"),;

  /**
   * String Representation
   */
  @Getter
  private final String str;

  /**
   * Constructor
   *
   * @param str String representation
   */
  ReservedWord(@NonNull final String str) {
    this.str = str;
  }

  /**
   * Method for checking whether arg is reserved word or not
   *
   * @param str checking target
   * @return true if it is reserved word
   */
  public static boolean isReservedWord(@NonNull final String str) {
    return Arrays.stream(ReservedWord.values()).map(ReservedWord::getStr)
        .filter(s -> StringUtils.equals(s, str)).count() != 0;
  }

  /**
   * Method for converting {@link ReservedWord} from {@link String}
   *
   * @return converted {@link ReservedWord} as Optional
   */
  public static Optional<ReservedWord> fromString(@NonNull final String str) {
    return Arrays.stream(ReservedWord.values()).filter(r -> StringUtils.equals(r.getStr(), str))
        .findFirst();
  }

  /**
   * Method for getting {@link YearMonth} representation of enum
   *
   * @return YearMonth representation
   */
  public YearMonth getYearMonth() {
    switch (this) {
      case LastMonth:
        return YearMonth.now().minusMonths(1);
      case ThisMonth:
        return YearMonth.now();
      case TwoMonthAgo:
        return YearMonth.now().minusMonths(2);
      default:
        throw new RuntimeException();
    }
  }

}
