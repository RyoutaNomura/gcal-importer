package com.rn.tool.gcalimporter.client.google.provider;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.rn.tool.gcalimporter.client.google.GoogleCalendarClient;
import com.rn.tool.gcalimporter.client.google.setting.GoogleCalendarClientSetting;

/**
 * Class for providing instance of {@link GoogleCalendarClient}
 */
public class GoogleCalendarClientProvider implements Provider<GoogleCalendarClient> {

  @Inject
  private GoogleCalendarClientSetting setting;

  @Override
  public GoogleCalendarClient get() {
    return new GoogleCalendarClient(setting.getApplicationName(),
        setting.getClientSecret(),
        setting.getDataStoreDir(),
        setting.getGoogleUserId());
  }
}
