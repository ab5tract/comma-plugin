package edument.perl6idea.pod;

import java.util.List;

public class PodDomCode extends PodDomBlock {
    public PodDomCode(int offset) {
        super(offset);
    }

    @Override
    public void renderInto(StringBuilder builder) {
        builder.append("<pre><code>");
        List<PodDomNode> children = getChildren();
        int last = children.size() - 1;
        for (int i = 0; i <= last; i++) {
            // Don't emit leading/trailing whitespace nodes.
            PodDomNode child = children.get(i);
            if (i == 0 || i == last)
                if (child instanceof PodDomText && ((PodDomText)child).getText().equals("\n"))
                    continue;

            // Otherwise, emit it.
            child.emitPositionInfo(builder);
            child.renderInto(builder);
        }
        builder.append("</code></pre>");
    }
}
