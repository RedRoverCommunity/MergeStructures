package community.redrover.merge;

import community.redrover.merge.model.Strategy;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.LifecyclePhase;

@SuppressWarnings("unused")
@Mojo(name = "append", defaultPhase = LifecyclePhase.GENERATE_RESOURCES, threadSafe = true)
public class AppendMojo extends BaseMojo {

    @Parameter(property = "append.sourceFile", required = true)
    private String sourceFile;

    @Parameter(property = "append.destinationFile", required = true)
    private String destinationFile;

    @Parameter(property = "append.resultFile", required = true)
    private String resultFile;

    @Override
    protected Strategy getStrategy() {
        return Strategy.APPEND;
    }
}