package community.redrover.merge.cli;

import com.beust.jcommander.JCommander;

public record ParsedStrategy(StrategyArgs args, JCommander commander) {}