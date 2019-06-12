package com.ve572.p1.preprocess;

import org.apache.commons.cli.*;

import java.nio.file.Path;
import java.nio.file.Paths;

public class MillionSong {

    public static void main(String[] args) throws Exception {
        String mainDirectory = "", subDirectory = "", outputDirectory = "";
        Options options = new Options();

        options.addOption(Option.builder()
                .longOpt("data")
                .hasArg()
                .build());

        options.addOption(Option.builder()
                .longOpt("subdirectory")
                .hasArg()
                .build());

        options.addOption(Option.builder()
                .longOpt("output")
                .hasArg()
                .build());

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        try {
            // parse the command line arguments

            CommandLine line = parser.parse(options, args);

            if (line.hasOption("help")) {
                formatter.printHelp("millionsongavro", options);
                System.exit(0);
            }

            if (line.hasOption("data")) {
                mainDirectory = line.getOptionValue("data");
            }

            if (line.hasOption("subdirectory")) {
                subDirectory = line.getOptionValue("subdirectory");
            }

            if (line.hasOption("output")) {
                outputDirectory = line.getOptionValue("output");
            }

        } catch (ParseException exp) {
            // oops, something went wrong
            System.err.println("Parsing failed.  Reason: " + exp.getMessage());
            formatter.printHelp("millionsongavro", options);
            System.exit(0);
        }

        if (mainDirectory.isEmpty()) {
            System.err.println("Parsing failed.  Reason: data not defined");
            formatter.printHelp("millionsongavro", options);
            System.exit(0);
        }
        if (subDirectory.isEmpty()) {
            System.err.println("Parsing failed.  Reason: subdirectory not defined");
            formatter.printHelp("millionsongavro", options);
            System.exit(0);
        }
        if (outputDirectory.isEmpty()) {
            System.err.println("Parsing failed.  Reason: output not defined");
            formatter.printHelp("millionsongavro", options);
            System.exit(0);
        }


        Path avroPath = Paths.get(outputDirectory, subDirectory + ".avro");
        avroPath.getParent().toFile().mkdirs();

        CompactSmallFiles.SerializeDirectory(mainDirectory, subDirectory, avroPath.toString());
    }

}
