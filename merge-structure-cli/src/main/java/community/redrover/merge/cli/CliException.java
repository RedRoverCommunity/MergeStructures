package community.redrover.merge.cli;

import com.beust.jcommander.JCommander;

public class CliException extends RuntimeException {
    private final boolean showUsage;
    private final JCommander commander;

    public CliException(String message, boolean showUsage) {
        super(message);
        this.showUsage = showUsage;
        this.commander = null;
    }

    public CliException(String message, JCommander commander) {
        super(message);
        this.showUsage = true;
        this.commander = commander;
    }

    public boolean shouldShowUsage() {
        return showUsage;
    }

    public JCommander getCommander() {
        return commander;
    }
}