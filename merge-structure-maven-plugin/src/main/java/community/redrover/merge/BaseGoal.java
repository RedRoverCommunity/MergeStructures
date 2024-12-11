package community.redrover.merge;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

@Mojo(name = "N/A", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public abstract class BaseGoal  extends AbstractMojo {
    // A mojo has to implement the Mojo interface.
    // In our case, we’ll extend from AbstractMojo so we’ll only have to implement the execute method

    // To have access to the project information, we have to add a MavenProject as a parameter:
    // This object will be injected by Maven when the context is created.
    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    protected MavenProject project;

    @Parameter(property = "fileIn1", defaultValue = "fileIn1.yml", required = true)
    protected String fileIn1;

    @Parameter(property = "fileIn2", defaultValue = "fileIn2.yml", required = true)
    protected String fileIn2;

    @Parameter(property = "fileOut", defaultValue = "fileOut.yml")
    protected String fileOut;

    @Override
    public abstract void execute();

}
