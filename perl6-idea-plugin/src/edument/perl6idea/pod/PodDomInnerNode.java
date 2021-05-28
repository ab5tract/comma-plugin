package edument.perl6idea.pod;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class PodDomInnerNode extends PodDomNode {
    private final List<PodDomNode> children = new ArrayList<>();

    public PodDomInnerNode(int offset) {
        super(offset);
    }

    public void addChild(@NotNull PodDomNode node) {
        if (node instanceof PodDomNodeList)
            children.addAll(((PodDomNodeList)node).getChildren());
        else
            children.add(node);
    }

    public List<PodDomNode> getChildren() {
        return children;
    }

    protected void renderChlidrenInto(StringBuilder builder) {
        for (PodDomNode child : children) {
            child.emitPositionInfo(builder);
            child.renderInto(builder);
        }
    }
}
