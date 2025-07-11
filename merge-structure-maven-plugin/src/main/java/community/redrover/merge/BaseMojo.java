package community.redrover.merge;

import community.redrover.merge.cli.CliException;
import community.redrover.merge.cli.CliUtils;
import community.redrover.merge.model.Strategy;
import community.redrover.merge.model.config.AbstractStrategyConfig;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;

public abstract class BaseMojo extends AbstractMojo {

    @Parameter(required = true)
    protected String sourceFile;

    @Parameter(required = true)
    protected String destinationFile;

    @Parameter(required = true)
    protected String resultFile;

    protected abstract Strategy getStrategy();

    @Override
    public final void execute() throws MojoExecutionException {
        Strategy strategy = getStrategy();
        String goal = strategy.getName();

        getLog().info("[DEBUG] sourceFile = " + sourceFile);
        getLog().info("[DEBUG] destinationFile = " + destinationFile);
        getLog().info("[DEBUG] resultFile = " + resultFile);

        if (sourceFile == null || destinationFile == null || resultFile == null) {
            throw new MojoExecutionException("One or more required parameters are null!");
        }

        String[] args = {
                "--source", sourceFile,
                "--destination", destinationFile,
                "--result", resultFile
        };

        try {
            AbstractStrategyConfig config = CliUtils.buildStrategyConfig(args, strategy);
            strategy.execute(config);
            getLog().info(goal + " strategy completed successfully.");
        } catch (CliException e) {
            mojoErrorHandler(goal, e);
        }
    }

    protected void mojoErrorHandler(String goal, CliException e) throws MojoExecutionException {
        if (e.getMessage() != null) {
            getLog().error(e.getMessage());
        }
        if (e.shouldShowUsage()) {
            getLog().info("Run with -X to see usage details.");
        }
        throw new MojoExecutionException(goal + " goal failed", e);
    }
}