package community.redrover.merge;

import community.redrover.merge.model.Strategy;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;

@SuppressWarnings("unused")
@Mojo(name = "replace", defaultPhase = LifecyclePhase.GENERATE_RESOURCES, threadSafe = true)
public class ReplaceMojo extends BaseMojo {

    @Override
    protected Strategy getStrategy() {
        return Strategy.REPLACE;
    }
}