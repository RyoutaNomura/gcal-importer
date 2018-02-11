package com.rn.tool.gcalimporter.misc;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Path;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * Class of Setting File Loader
 */
@Slf4j
public class SettingFileLoader {

  /**
   * Method for Loading Setting File
   *
   * @param path Path to Setting File
   * @return Setting File Instance if it Present
   */
  public static Setting load(@NonNull final Path path) {

    try {
      final ObjectMapper mapper = new ObjectMapper();
      final Setting setting = mapper.readValue(path.toFile(), Setting.class);
      log.info("Setting File: " + path.toFile().getAbsolutePath());
      return setting;

    } catch (IOException e) {
      log.error("An error occurred during loading setting files: " + path, e);
      throw new RuntimeException(e);
    }

  }
}
