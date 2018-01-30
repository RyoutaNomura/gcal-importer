package com.rn.tool.gcalimporter.args;

import java.nio.file.Path;
import lombok.Builder;
import lombok.Data;

/**
 * Class of Argument's Representation
 */
@Data
@Builder
public class Argument {

  /**
   * Path for setting file which contains detail settings
   */
  private final Path settingFile;
}
