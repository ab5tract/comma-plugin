package edument.perl6idea.psi;

import java.util.HashMap;
import java.util.Map;

public class PodRenderingContext {
    private final Map<String, String> semanticBlocks = new HashMap<>();

    public void addSemanticBlock(String typename, String renderedContent) {
        semanticBlocks.put(typename, renderedContent);
    }

    public Map<String, String> getSemanticBlocks() {
        return semanticBlocks;
    }
}
