package community.redrover.merge;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.util.List;

@Mojo(name = "merge", defaultPhase = LifecyclePhase.COMPILE)
public class AppendGoal extends AbstractMojo {
    // A mojo has to implement the Mojo interface.
    // In our case, we’ll extend from AbstractMojo so we’ll only have to implement the execute method

    // To have access to the project information, we have to add a MavenProject as a parameter:
    // This object will be injected by Maven when the context is created.
    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    MavenProject project;

    //The parameter project we’ve added before is read-only and can’t be configured by the user.
    // Also, it’s injected by Maven so we could say it’s kind of special.
    // add a parameter where users can specify the scope of the dependencies that we want to count.
    @Parameter(property = "scope")
    String scope;

    @Override
    public void execute() {
        getLog().info("Running Append strategy");

        @SuppressWarnings("unchecked")
        List<Dependency> dependencies = project.getDependencies();
        long numDependencies = dependencies.size();

        // The getLog() method provides access to the Maven log. The AbstractMojo already handles its lifecycle.
        getLog().info("Number of dependencies: " + numDependencies);

        getLog().info("Scope: " + scope);

        //list all dependencies
        for (Dependency dependency : dependencies) {
            getLog().info("Dependency: " + dependency.getGroupId() + ":" + dependency.getArtifactId() + ":" + dependency.getVersion());
        }

    }
}
