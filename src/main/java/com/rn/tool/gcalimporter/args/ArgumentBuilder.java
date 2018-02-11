package com.rn.tool.gcalimporter.args;

import java.nio.file.Paths;
import java.util.Optional;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Class of Argument's Processor
 */
@Slf4j
@UtilityClass
public class ArgumentBuilder {

  /**
   * Method for creation of Argument object
   *
   * @param args argument passed from Main Class
   * @return Argument object
   */
  public static Optional<Argument> proceed(@NonNull final String... args) {

    final Options options = new Options();
    options.addOption(
        Option.builder("f").longOpt("file").argName("setting file").hasArg()
            .desc("Path for setting file").required().build()
    );

    if (args.length == 0) {

      // Show help if argument is empty
      final HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp("GCal Importer version 0.1", options);

      return Optional.empty();

    } else {

      final CommandLineParser parser = new DefaultParser();

      try {
        final CommandLine cmd = parser.parse(options, args);
        final String fileName = cmd.getOptionValue("f");
        return Optional.of(Argument.builder().settingFile(Paths.get(fileName)).build());

      } catch (MissingArgumentException e) {
        log.error("Missing Required Argument: -" + e.getOption().getOpt(), e);
        throw new RuntimeException(e);

      } catch (ParseException e) {
        throw new RuntimeException(e);

      }
    }
  }
}
