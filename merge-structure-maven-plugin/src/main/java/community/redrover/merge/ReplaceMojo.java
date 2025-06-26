package community.redrover.merge;

import community.redrover.merge.model.Strategy;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.LifecyclePhase;

@SuppressWarnings("FieldCanBeLocal")
@Mojo(name = "replace", defaultPhase = LifecyclePhase.GENERATE_RESOURCES, threadSafe = true)
public class ReplaceMojo extends BaseMojo {

    @Parameter(property = "replace.sourceFile", required = true)
    private String sourceFile;

    @Parameter(property = "replace.destinationFile", required = true)
    private String destinationFile;

    @Parameter(property = "replace.resultFile", required = true)
    private String resultFile;

    @Override
    protected Strategy getStrategy() {
        return Strategy.REPLACE;
    }
}