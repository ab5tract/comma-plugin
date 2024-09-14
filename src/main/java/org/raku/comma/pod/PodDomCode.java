package org.raku.comma.pod;

import java.util.List;

public class PodDomCode extends PodDomBlock {
    public PodDomCode(int offset) {
        super(offset);
    }

    @Override
    public void renderInto(StringBuilder builder, PodRenderingContext context) {
        builder.append("<pre><code>");
        List<PodDomNode> children = getChildren();
        int last = children.size() - 1;
        for (int i = 0; i <= last; i++) {
            // We typically end up with a Pod newline before the code and another
            // one after it placed into this node. This results in too many newlines
            // in the HTML, so we drop them here.
            PodDomNode child = children.get(i);
            if (i == 0 || i == last)
                if (child instanceof PodDomText && ((PodDomText)child).getText().equals("\n"))
                    continue;

            // Otherwise, emit it.
            child.emitPositionInfo(builder);
            child.renderInto(builder, context);
        }
        builder.append("</code></pre>");
    }
}
