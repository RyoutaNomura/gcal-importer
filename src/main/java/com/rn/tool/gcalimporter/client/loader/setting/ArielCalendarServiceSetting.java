package com.rn.tool.gcalimporter.client.loader.setting;

import java.net.URI;
import lombok.Builder;
import lombok.Data;

/**
 * Class for holding settings of Ariel Calendar
 */
@Data
@Builder
public class ArielCalendarServiceSetting {

  /**
   * URI to connect
   */
  private final URI uri;
  /**
   * Path for authentication
   */
  private final String authPath;
  /**
   * Path for access to calendar
   */
  private final String icalPath;
  /**
   * User Name
   */
  private final String username;
  /**
   * Password
   */
  private final String password;
}
