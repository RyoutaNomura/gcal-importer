package com.rn.tool.gcalimporter.misc;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.URI;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Data
public class Setting {

  private static DateTimeFormatter YEAR_MONTH_FMT = DateTimeFormatter.ofPattern("yyyyMM");

  @JsonProperty("authPath")
  private String authPath;

  @JsonProperty("icalPath")
  private String icalPath;

  @JsonProperty("uri")
  private URI uri;

  @JsonProperty("userName")
  private String userName;

  @JsonProperty("password")
  private String password;

  @JsonProperty("googleCalendarId")
  private String googleCalendarId;

  @JsonProperty("clientSecretJsonPath")
  private String clientSecretJsonPath;

  @JsonProperty("dataStoreDir")
  private String dataStoreDir;

  @JsonProperty("IsSpecifyTargetYearMonth")
  private boolean isSpecifyTargetYearMonth;

  @JsonProperty("targetYearMonthType")
  private String targetYearMonthType;

  @JsonProperty("specificTargetYearMonth")
  private String specificTargetYearMonth;

  @JsonProperty("targetRange")
  private int targetRange;

  public YearMonth getTargetYearMonthValue() {
    if (isSpecifyTargetYearMonth) {
      return specificTargetYearMonth != null ? YearMonth.parse(specificTargetYearMonth, YEAR_MONTH_FMT) : YearMonth.now();
    } else {
      Optional<ReservedWord> res = ReservedWord.fromString(targetYearMonthType);
      if (res.isPresent()) {
        return res.get().getYearMonth();
      } else {
        throw new RuntimeException("targetYearMonthType: " + targetYearMonthType + " is not reserved word");
      }
    }
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
  }
}
