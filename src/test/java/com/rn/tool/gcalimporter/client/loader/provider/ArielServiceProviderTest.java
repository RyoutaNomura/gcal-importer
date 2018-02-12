package com.rn.tool.gcalimporter.client.loader.provider;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.rn.tool.gcalimporter.client.loader.annotation.ArielAuthService;
import com.rn.tool.gcalimporter.client.loader.annotation.ArielICalService;
import com.rn.tool.gcalimporter.client.loader.setting.ArielCalendarServiceSetting;
import java.net.URI;
import javax.ws.rs.client.WebTarget;
import lombok.Getter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class ArielServiceProviderTest {

  @Getter
  private static class InjectedClass {

    @Inject @ArielAuthService
    private WebTarget auth;

    @Inject @ArielICalService
    private WebTarget iCal;
  }

  @Test
  public void injectionTest() {
    ArielCalendarServiceSetting setting = ArielCalendarServiceSetting.builder()
        .uri(URI.create("http://localhost"))
        .authPath("/auth")
        .icalPath("/ical")
        .build();

    Injector injector = Guice.createInjector(new AbstractModule() {
      @Override
      protected void configure() {
        bind(ArielCalendarServiceSetting.class).toInstance(setting);
        bind(WebTarget.class).annotatedWith(ArielAuthService.class)
            .toProvider(ArielAuthServiceProvider.class);
        bind(WebTarget.class).annotatedWith(ArielICalService.class)
            .toProvider(ArielCalendarServiceProvider.class);
      }
    });

    InjectedClass test = injector.getInstance(InjectedClass.class);
    assertThat(test.getAuth(), is(notNullValue()));
    assertThat(test.getAuth().getUri(), is(setting.getUri().resolve(setting.getAuthPath())));
    assertThat(test.getICal().getUri(), is(setting.getUri().resolve(setting.getIcalPath())));
  }



}