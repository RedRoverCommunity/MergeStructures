package community.redrover.merge;

import community.redrover.merge.model.Strategy;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;

@SuppressWarnings("unused")
@Mojo(name = "extend", defaultPhase = LifecyclePhase.GENERATE_RESOURCES, threadSafe = true)
public class ExtendMojo extends BaseMojo {

    @Override
    protected Strategy getStrategy() {
        return Strategy.EXTEND;
    }
}