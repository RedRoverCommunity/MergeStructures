package community.redrover.merge;

import community.redrover.merge.model.Strategy;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;

@SuppressWarnings("unused")
@Mojo(name = "merge", defaultPhase = LifecyclePhase.GENERATE_RESOURCES, threadSafe = true)
public class MergeMojo extends BaseMojo {

    @Override
    protected Strategy getStrategy() {
        return Strategy.MERGE;
    }
}