package org.raku.psi.external;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import org.raku.psi.RakuParameter;
import org.raku.psi.RakuRoutineDecl;
import org.raku.psi.RakuSignature;
import org.raku.psi.stub.RakuRoutineDeclStub;
import org.raku.psi.symbols.MOPSymbolsAllowed;
import org.raku.psi.symbols.RakuExplicitAliasedSymbol;
import org.raku.psi.symbols.RakuSymbolCollector;
import org.raku.psi.symbols.RakuSymbolKind;
import org.raku.psi.type.RakuType;
import org.raku.psi.type.RakuUnresolvedType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

public class ExternalRakuRoutineDecl extends RakuExternalPsiElement implements RakuRoutineDecl {
    private final String myKind;
    private final String myScope;
    private final String myName;
    private final String myIsMulti;
    private String myReturnType;
    private final RakuSignature mySignature;
    private final String myDeprecationMessage;
    private final boolean myIsPure;
    private boolean myIsImplementationDetail;

    public ExternalRakuRoutineDecl(Project project, PsiElement parent,
                                   String kind, String scope, String name,
                                   String isMulti, String deprecationMessage,
                                   JSONObject signature, boolean isPure) {
        myProject = project;
        myParent = parent;
        myKind = kind;
        myScope = scope;
        myName = name;
        myIsMulti = isMulti;
        myDeprecationMessage = deprecationMessage;
        mySignature = new ExternalRakuSignature(project, parent, signature);
        myReturnType = (String)signature.get("r");
        if (myReturnType.endsWith(":D") || myReturnType.endsWith(":U")) {
            myReturnType = myReturnType.substring(0, myReturnType.length() - 2);
        }
        myIsPure = isPure;
        myIsImplementationDetail = false;
    }

    @Override
    public String getRoutineKind() {
        switch (myKind) {
            case "m":
                return "method";
            case "sm":
                return "submethod";
            default:
                return "sub";
        }
    }

    @Override
    public boolean isMethod() {
        return !isSub();
    }

    @Override
    public boolean isSub() {
        return !(myKind.equals("m") || myKind.equals("sm"));
    }

    @Override
    public String getRoutineName() {
        return getName();
    }

    @NotNull
    @Override
    public String getName() {
        return myName;
    }

    @Override
    public boolean isPrivate() {
        return myName.startsWith("!");
    }

    @Override
    public boolean isStubbed() {
        return false;
    }

    @Override
    public PsiElement[] getContent() {
        return PsiElement.EMPTY_ARRAY;
    }

    @Override
    public RakuParameter[] getParams() {
        return mySignature.getParameters();
    }

    @Override
    public @NotNull PsiElement @NotNull [] getChildren() {
        return new RakuSignature[]{mySignature};
    }

    @Override
    public String getMultiness() {
        return myIsMulti;
    }

    @Override
    public PsiElement getDeclaratorNode() {
        return null;
    }

    @Override
    public IStubElementType<?, ?> getElementType() {
        return null;
    }

    @Override
    public RakuRoutineDeclStub getStub() {
        return null;
    }

    @Override
    public @NotNull String getScope() {
        return myScope;
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return null;
    }

    @Override
    public String getSignature() {
        return mySignature.summary(new RakuUnresolvedType(myReturnType));
    }

    @Nullable
    @Override
    public RakuSignature getSignatureNode() {
        return mySignature;
    }

    @Nullable
    @Override
    public String getReturnsTrait() {
        return null;
    }

    @Override
    public @NotNull RakuType getReturnType() {
        return new RakuUnresolvedType(myReturnType);
    }

    @Override
    public void contributeLexicalSymbols(RakuSymbolCollector collector) {

    }

    @Override
    public void contributeMOPSymbols(RakuSymbolCollector collector, MOPSymbolsAllowed symbolsAllowed) {
        if (!myScope.equals("has") || myName.equals("<anon>"))
            return;
        if (!symbolsAllowed.privateMethodsVisible && myName.startsWith("!"))
            return;
        if (!symbolsAllowed.submethodsVisible && myKind.equals("submethod"))
            return;

        RakuExplicitAliasedSymbol sym = new RakuExplicitAliasedSymbol(RakuSymbolKind.Method, this,
                myName.startsWith("!") ? myName : "." + myName);
        if (myIsMulti.equals("only"))
            collector.offerSymbol(sym);
        else
            collector.offerMultiSymbol(sym, false);
    }

    @Override
    public boolean isDeprecated() {
        return myDeprecationMessage != null;
    }

    @Override
    public String getDeprecationMessage() {
        return myDeprecationMessage;
    }

    @Override
    public boolean isPure() {
        return myIsPure;
    }

    public void setImplementationDetail(boolean flag) {
        myIsImplementationDetail = flag;
    }

    public boolean isImplementationDetail() {
        return myIsImplementationDetail;
    }
}
