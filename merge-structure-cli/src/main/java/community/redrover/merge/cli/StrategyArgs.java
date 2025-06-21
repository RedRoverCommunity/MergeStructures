package community.redrover.merge.cli;

import com.beust.jcommander.Parameter;

public class StrategyArgs {

    @Parameter(names = "--config", description = "Path to config file (JSON or YAML)")
    public String config;

    @Parameter(names = "--source", description = "Source file path")
    public String source;

    @Parameter(names = "--destination", description = "Destination file path")
    public String destination;

    @Parameter(names = "--result", description = "Output file path")
    public String result;

    @Parameter(names = "--help", help = true, description = "Display help message", order = 99)
    public boolean help;
}