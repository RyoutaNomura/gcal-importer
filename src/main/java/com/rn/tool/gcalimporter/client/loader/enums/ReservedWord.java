package com.rn.tool.gcalimporter.client.loader.enums;

import java.util.Arrays;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;

public enum ReservedWord {
  TODAY("CCS_TODAY", true),
  THIS_MONTH("CCS_THIS_MONTH", true),
  BASIC_USER_ID("BASIC.USER_ID", false),
  BASIC_PASSWORD("BASIC.PASSWORD", false),;

  private String word;
  private boolean convertable;

  ReservedWord(String word, boolean convertable) {
    this.word = word;
    this.convertable = convertable;
  }

  public static Optional<ReservedWord> fromWord(String word) {
    return Arrays.stream(ReservedWord.values())
        .filter(v -> StringUtils
            .equals(v.word, word))
        .findFirst();
  }
}

