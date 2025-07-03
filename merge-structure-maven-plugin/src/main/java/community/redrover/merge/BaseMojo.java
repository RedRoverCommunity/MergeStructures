package community.redrover.merge;

import community.redrover.merge.cli.CliException;
import community.redrover.merge.cli.CliUtils;
import community.redrover.merge.model.Strategy;
import community.redrover.merge.model.config.AbstractStrategyConfig;
import lombok.Getter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;

@Getter
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
        String[] args = {
                "--source", getSourceFile(),
                "--destination", getDestinationFile(),
                "--result", getResultFile()
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