package community.redrover.merge;

import community.redrover.merge.model.Strategy;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.LifecyclePhase;

@SuppressWarnings("FieldCanBeLocal")
@Mojo(name = "extend", defaultPhase = LifecyclePhase.GENERATE_RESOURCES, threadSafe = true)
public class ExtendMojo extends BaseMojo {

    @Parameter(property = "extend.sourceFile", required = true)
    private String sourceFile;

    @Parameter(property = "extend.destinationFile", required = true)
    private String destinationFile;

    @Parameter(property = "extend.resultFile", required = true)
    private String resultFile;

    @Override
    protected Strategy getStrategy() {
        return Strategy.EXTEND;
    }
}