package com.rn.tool.gcalimporter.client.google.setting;

import java.nio.file.Path;
import lombok.Builder;
import lombok.Data;

/**
 * Class for holding settings of GoogleCalendarClient
 */
@Data
@Builder
public class GoogleCalendarClientSetting {

  /**
   * Application Name
   */
  private final String applicationName;

  /**
   * Path to client_secret.json
   */
  private final Path clientSecret;

  /**
   * Path to store files
   */
  private final Path dataStoreDir;

  /**
   * Google User ID
   */
  private final String googleUserId;
}
