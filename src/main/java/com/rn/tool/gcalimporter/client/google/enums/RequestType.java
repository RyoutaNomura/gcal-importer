package com.rn.tool.gcalimporter.client.google.enums;

import lombok.Getter;
import lombok.NonNull;

/**
 * Enum to specify which request to execute
 */
public enum RequestType {
  INSERT("Insert"),
  UPDATE("Update"),
  DELETE("Delete"),;

  @Getter
  private final String val;

  RequestType(@NonNull final String val) {
    this.val = val;
  }
}
