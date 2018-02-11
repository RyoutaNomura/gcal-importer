package com.rn.tool.gcalimporter.client.loader.provider;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.rn.tool.gcalimporter.client.loader.setting.ArielCalendarServiceSetting;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.logging.LoggingFeature;

public class ArielCalendarServiceProvider implements Provider<WebTarget>{

  /**
   * Setting to generate WebTarget for providing
   */
  @Inject
  private ArielCalendarServiceSetting setting;

  @Override
  public WebTarget get() {
    return ClientBuilder.newClient().register(new LoggingFeature())
        .register(
            HttpAuthenticationFeature.basic(this.setting.getUsername(), this.setting.getPassword()))
        .target(this.setting.getUri())
        .path(this.setting.getIcalPath());
  }
}
