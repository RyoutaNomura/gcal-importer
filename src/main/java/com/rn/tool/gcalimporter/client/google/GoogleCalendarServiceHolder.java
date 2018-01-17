package com.rn.tool.gcalimporter.client.google;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.rn.tool.gcalimporter.utils.GoogleAuthorizationUtils;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import lombok.Builder;

/**
 * Class to hold service object's instance of Google Calendar
 */
class GoogleCalendarServiceHolder {

  /**
   * Application name of OAuth client id
   */
  private final String applicationName;

  /**
   * Path to store credential
   */
  private final File dataStoreDir;

  /**
   * Access scope of Google Calendar
   */
  private final List<String> scopes;

  /**
   * Timeout for GoogleCalendarService
   */
  private final int timeout;

  /**
   * Instance of service class of Google Calendar
   */
  private com.google.api.services.calendar.Calendar calendar;

  @Builder
  private GoogleCalendarServiceHolder(final String applicationName, final File dataStoreDir,
      final List<String> scopes,
      final int timeout) {
    this.applicationName = applicationName;
    this.dataStoreDir = dataStoreDir;
    this.scopes = scopes;
    this.timeout = timeout;
  }

  /**
   * Method for getting instance of calendar service
   *
   * @return instance of calendar service
   */
  public com.google.api.services.calendar.Calendar get() {
    if (this.calendar == null) {
      this.initialize();
    }
    return this.calendar;
  }

  /**
   * Method for initialization of instance
   */
  private void initialize() {
    try {
      final HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
      final JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
      final Credential credential = GoogleAuthorizationUtils
          .authorize(this.dataStoreDir, this.scopes);

      this.calendar = new com.google.api.services.calendar.Calendar.Builder(
          httpTransport,
          jsonFactory,
          r -> {
            credential.initialize(r);
            r.setConnectTimeout(this.timeout);
            r.setReadTimeout(this.timeout);
          }).setApplicationName(this.applicationName)
          .build();
    } catch (final IOException | GeneralSecurityException e) {
      throw new RuntimeException(e);
    }
  }
}
