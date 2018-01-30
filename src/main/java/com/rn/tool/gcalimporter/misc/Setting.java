package com.rn.tool.gcalimporter.misc;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import java.net.URI;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Data
public class Setting {

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

  private YearMonth targetYearMonth;
  @JsonProperty("targetRange")
  private int targetRange;

  @JsonGetter("targetYearMonth")
  public YearMonth getTargetYearMonth() {
    return this.targetYearMonth;
  }

  @JsonSetter("targetYearMonth")
  public void setTargetYearMonth(String targetYearMonth) {
    if (StringUtils.isEmpty(targetYearMonth)) {
      this.targetYearMonth = YearMonth.now();
    } else {
      DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyyMM");
      this.targetYearMonth = YearMonth.parse(targetYearMonth, fmt);
    }
  }

  public YearMonth getTargetYearMonthValue() {
    return isSpecifyTargetYearMonth ? targetYearMonth : YearMonth.now();
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
  }
}
