package com.rn.tool.gcalimporter.client.google.enums;

import lombok.Getter;

/**
 * Enum to specify which request to execute
 */
public enum RequestType {
  INSERT("Insert"),
  UPDATE("Update"),
  DELETE("Delete"),;

  @Getter
  private final String val;

  RequestType(String val) {
    this.val = val;
  }
}
