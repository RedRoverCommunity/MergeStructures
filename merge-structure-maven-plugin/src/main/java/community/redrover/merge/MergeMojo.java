package community.redrover.merge;

import community.redrover.merge.model.Strategy;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.LifecyclePhase;

@SuppressWarnings("unused")
@Mojo(name = "merge", defaultPhase = LifecyclePhase.GENERATE_RESOURCES, threadSafe = true)
public class MergeMojo extends BaseMojo {

    @Parameter(property = "merge.sourceFile", required = true)
    private String sourceFile;

    @Parameter(property = "merge.destinationFile", required = true)
    private String destinationFile;

    @Parameter(property = "merge.resultFile", required = true)
    private String resultFile;

    @Override
    protected Strategy getStrategy() {
        return Strategy.MERGE;
    }
}