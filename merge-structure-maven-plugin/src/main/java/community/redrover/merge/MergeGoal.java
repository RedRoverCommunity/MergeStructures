package community.redrover.merge;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.util.List;

@Mojo(name = "merge", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class MergeGoal extends BaseGoal {

    @Override
    public void execute() {

        // The getLog() method provides access to the Maven log. The AbstractMojo already handles its lifecycle.
        getLog().info("""
        Running Merge strategy with
            input file1: %s
            input file2: %s
            outputting results into file: %s
        """.formatted(fileIn1, fileIn2, fileOut));

        //below just sample code to get access to the current scope and list of the dependencies
        @SuppressWarnings("unchecked")
        List<Dependency> dependencies = project.getDependencies();
        long numDependencies = dependencies.size();

        getLog().info("Number of dependencies: " + numDependencies);
        for (Dependency dependency : dependencies) {
            getLog().info("Dependency: " + dependency.getGroupId() + ":" + dependency.getArtifactId() + ":" + dependency.getVersion());
        }
    }
}
