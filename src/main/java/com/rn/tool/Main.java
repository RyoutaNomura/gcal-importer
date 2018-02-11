package com.rn.tool;


import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.rn.tool.gcalimporter.args.Argument;
import com.rn.tool.gcalimporter.args.ArgumentBuilder;
import com.rn.tool.gcalimporter.client.google.GoogleCalendarClient;
import com.rn.tool.gcalimporter.client.google.provider.GoogleCalendarClientProvider;
import com.rn.tool.gcalimporter.client.google.setting.GoogleCalendarClientSetting;
import com.rn.tool.gcalimporter.client.loader.CalendarLoader;
import com.rn.tool.gcalimporter.client.loader.CalendarLoaderArielImpl;
import com.rn.tool.gcalimporter.client.loader.annotation.ArielAuthService;
import com.rn.tool.gcalimporter.client.loader.annotation.ArielICalService;
import com.rn.tool.gcalimporter.client.loader.provider.ArielAuthServiceProvider;
import com.rn.tool.gcalimporter.client.loader.provider.ArielCalendarServiceProvider;
import com.rn.tool.gcalimporter.client.loader.setting.ArielCalendarServiceSetting;
import com.rn.tool.gcalimporter.logic.CalendarTransfer;
import com.rn.tool.gcalimporter.misc.ApplicationInformationWriter;
import com.rn.tool.gcalimporter.misc.Setting;
import com.rn.tool.gcalimporter.misc.SettingFileLoader;
import java.nio.file.Paths;
import javax.ws.rs.client.WebTarget;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * Main Class for Gcal Importer
 */
@Slf4j
public class Main {

  /**
   * Application Name
   */
  private static final String GOOGLE_APPLICATION_NAME = "GCalImporter";

  /**
   * Main Method
   *
   * @param args arguments passed from console
   */
  public static void main(String[] args) throws Exception {

    // write application information
    ApplicationInformationWriter.write();

    try {
      // Execute Main Logic
      ArgumentBuilder.proceed(args).ifPresent(Main::exec);

    } catch (Exception e) {
      log.error("An error occurred during executing application", e);
      System.exit(1);

    }

    log.info("Done.");
  }

  /**
   * Method for Main Logic
   */
  public static void exec(@NonNull final Argument arg) {

    final Setting setting = SettingFileLoader.load(arg.getSettingFile());

    final ArielCalendarServiceSetting arielSetting = ArielCalendarServiceSetting.builder()
        .uri(setting.getUri())
        .authPath(setting.getAuthPath())
        .icalPath(setting.getIcalPath())
        .username(setting.getUserName())
        .password(setting.getPassword())
        .build();
    final GoogleCalendarClientSetting googleSetting = GoogleCalendarClientSetting.builder()
        .applicationName(GOOGLE_APPLICATION_NAME)
        .clientSecret(Paths.get(setting.getClientSecretJsonPath()))
        .dataStoreDir(Paths.get(setting.getDataStoreDir()))
        .build();

    Injector injector = Guice.createInjector(new AbstractModule() {
      @Override
      protected void configure() {
        bind(ArielCalendarServiceSetting.class)
            .toInstance(arielSetting);
        bind(WebTarget.class)
            .annotatedWith(ArielAuthService.class)
            .toProvider(ArielAuthServiceProvider.class);
        bind(WebTarget.class)
            .annotatedWith(ArielICalService.class)
            .toProvider(ArielCalendarServiceProvider.class);
        bind(CalendarLoader.class)
            .to(CalendarLoaderArielImpl.class);
        bind(GoogleCalendarClientSetting.class)
            .toInstance(googleSetting);
        bind(GoogleCalendarClient.class)
            .toProvider(GoogleCalendarClientProvider.class);
      }
    });

    final CalendarTransfer act = injector.getInstance(CalendarTransfer.class);

    act.transfer(
        setting.getGoogleCalendarId(),
        setting.getTargetYearMonthValue(),
        setting.getTargetRange()
    );
  }
}
