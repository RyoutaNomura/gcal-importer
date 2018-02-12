package com.rn.tool.gcalimporter.client.google.provider;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.rn.tool.gcalimporter.client.google.GoogleCalendarClient;
import com.rn.tool.gcalimporter.client.google.setting.GoogleCalendarClientSetting;
import java.nio.file.Paths;
import org.junit.Test;

public class GoogleCalendarClientProviderTest {

  private GoogleCalendarClientProvider createInstance(GoogleCalendarClientSetting setting) {
    Injector injector = Guice.createInjector(new AbstractModule() {
      @Override
      protected void configure() {
        bind(GoogleCalendarClientSetting.class).toInstance(setting);
      }
    });
    return injector.getInstance(GoogleCalendarClientProvider.class);
  }

  @Test
  public void getTest() throws Exception {

    GoogleCalendarClientSetting setting = GoogleCalendarClientSetting.builder()
        .applicationName("hoge")
        .clientSecret(Paths.get("fuga"))
        .dataStoreDir(Paths.get("piyo"))
        .build();

    GoogleCalendarClientProvider provider = createInstance(setting);
    GoogleCalendarClient actual = provider.get();

    assertThat(actual.getCalendarService().getApplicationName(), is(setting.getApplicationName()));
    assertThat(actual.getCalendarService().getClientSecret(), is(setting.getClientSecret()));
    assertThat(actual.getCalendarService().getDataStoreDir(), is(setting.getDataStoreDir()));


  }

}