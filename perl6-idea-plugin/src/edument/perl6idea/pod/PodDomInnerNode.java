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
        children.add(node);
    }

    public List<PodDomNode> getChildren() {
        return children;
    }

    protected void renderChlidrenInto(StringBuilder builder, PodRenderingContext context) {
        int listLevelsOpened = 0;
        for (PodDomNode child : children) {
            child.emitPositionInfo(builder);
            if (child instanceof PodDomItem) {
                int desiredLevel = ((PodDomItem)child).getLevel();
                listLevelsOpened = emitListOpenings(builder, context, listLevelsOpened, desiredLevel);
                listLevelsOpened = emitListClosings(builder, context, listLevelsOpened, desiredLevel);
            }
            else if (listLevelsOpened > 0) {
                listLevelsOpened = emitListClosings(builder, context, listLevelsOpened,
                        context.listDepth - listLevelsOpened);
            }
            child.renderInto(builder, context);
        }
        emitListClosings(builder, context, listLevelsOpened,
                context.listDepth - listLevelsOpened);
    }

    private int emitListOpenings(StringBuilder builder, PodRenderingContext context, int listLevelsOpened, int desiredLevel) {
        while (desiredLevel > context.listDepth) {
            builder.append("<ul>");
            context.listDepth++;
            listLevelsOpened++;
        }
        return listLevelsOpened;
    }

    private int emitListClosings(StringBuilder builder, PodRenderingContext context, int listLevelsOpened, int desiredLevel) {
        while (desiredLevel < context.listDepth) {
            builder.append("</ul>");
            context.listDepth--;
            listLevelsOpened--;
        }
        return listLevelsOpened;
    }
}
