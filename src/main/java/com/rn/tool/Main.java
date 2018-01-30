package com.rn.tool;


import com.rn.tool.gcalimporter.args.Argument;
import com.rn.tool.gcalimporter.args.ArgumentBuilder;
import com.rn.tool.gcalimporter.logic.ArielCalenderTransfer;
import com.rn.tool.gcalimporter.misc.ApplicationInformationWriter;
import com.rn.tool.gcalimporter.misc.Setting;
import com.rn.tool.gcalimporter.misc.SettingFileLoader;
import lombok.extern.slf4j.Slf4j;

/**
 * Main Class for Gcal Importer
 */
@Slf4j
public class Main {

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
  public static void exec(Argument arg) {

    final Setting setting = SettingFileLoader.load(arg.getSettingFile());

    final ArielCalenderTransfer act = new ArielCalenderTransfer(
        setting.getClientSecretJsonPath(),
        setting.getDataStoreDir(),
        setting.getUri(),
        setting.getAuthPath(),
        setting.getIcalPath(),
        setting.getUserName(),
        setting.getPassword()
    );

    act.transfer(
        setting.getGoogleCalendarId(),
        setting.getTargetYearMonthValue(),
        setting.getTargetRange()
    );
  }
}
