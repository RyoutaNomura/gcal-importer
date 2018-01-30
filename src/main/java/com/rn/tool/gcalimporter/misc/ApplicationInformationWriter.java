package com.rn.tool.gcalimporter.misc;

import com.google.common.io.Resources;
import com.rn.tool.gcalimporter.common.Constants;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * Class for Logging Application Information
 */
@Slf4j
public class ApplicationInformationWriter {

  /**
   * Method for Logging Application Information Using POM.xml
   */
  public static void write() {

    try (InputStream s = Resources.getResource("application.properties").openStream()) {

      Properties prop = new Properties();
      prop.load(s);

      log.info(StringUtils.repeat('#', Constants.SEP_LENGTH));
      log.info(StringUtils.EMPTY);
      log.info(prop.getProperty("application.name"));
      log.info(prop.getProperty("application.version"));
      log.info(StringUtils.EMPTY);
      log.info(StringUtils.repeat('#', Constants.SEP_LENGTH));
      log.info(StringUtils.EMPTY);

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
