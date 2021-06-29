package edument.perl6idea.pod;

import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PodDomBuildingContext {
    private final List<PodDomNode> blocks = new ArrayList<>();
    private final Map<String, PodDomNode> semanticBlocks = new HashMap<>();
    private final List<PodDomDeclarator> types = new ArrayList<>();
    private final List<PodDomDeclarator> subs = new ArrayList<>();
    private final Deque<String> globalNameParts = new ArrayDeque<>();
    private int inLexicalPackage = 0;
    private final Deque<Optional<PodDomClassyDeclarator>> classyTypeStack = new ArrayDeque<>();

    public void addBlock(PodDomNode domNode) {
        blocks.add(domNode);
    }

    public List<PodDomNode> getBlocks() {
        return blocks;
    }

    public void addSemanticBlock(String typename, PodDomNode domNode) {
        semanticBlocks.put(typename, domNode);
    }

    public Map<String, PodDomNode> getSemanticBlocks() {
        return semanticBlocks;
    }

    public void addType(PodDomDeclarator type) {
        types.add(type);
    }

    public List<PodDomDeclarator> getTypes() {
        return types;
    }

    public void addSub(PodDomDeclarator sub) {
        subs.add(sub);
    }

    public List<PodDomDeclarator> getSubs() {
        return subs;
    }

    public void enterGlobalNamePart(String part) {
        globalNameParts.push(part);
    }

    public void exitGlobalNamePart() {
        globalNameParts.pop();
    }

    public void enterLexicalPackage() {
        inLexicalPackage++;
    }

    public void exitLexicalPackage() {
        inLexicalPackage--;
    }

    @Nullable
    public String prependGlobalNameParts(String name) {
        if (inLexicalPackage > 0)
            return null;
        if (globalNameParts.isEmpty())
            return name;
        return String.join("::", globalNameParts) + "::" + name;
    }

    public void enterClassyType(PodDomClassyDeclarator type) {
        classyTypeStack.push( Optional.ofNullable(type));
    }

    public void exitClassyType() {
        classyTypeStack.pop();
    }

    @Nullable
    public PodDomClassyDeclarator currentClassyDeclarator() {
        return classyTypeStack.isEmpty() ? null : classyTypeStack.peek().orElse(null);
    }
}
