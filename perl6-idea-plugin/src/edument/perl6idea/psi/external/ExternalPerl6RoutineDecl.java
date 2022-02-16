package edument.perl6idea.psi.external;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import edument.perl6idea.psi.Perl6Parameter;
import edument.perl6idea.psi.Perl6RoutineDecl;
import edument.perl6idea.psi.Perl6Signature;
import edument.perl6idea.psi.stub.Perl6RoutineDeclStub;
import edument.perl6idea.psi.symbols.MOPSymbolsAllowed;
import edument.perl6idea.psi.symbols.Perl6ExplicitAliasedSymbol;
import edument.perl6idea.psi.symbols.Perl6SymbolCollector;
import edument.perl6idea.psi.symbols.Perl6SymbolKind;
import edument.perl6idea.psi.type.Perl6Type;
import edument.perl6idea.psi.type.Perl6UnresolvedType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

public class ExternalPerl6RoutineDecl extends Perl6ExternalPsiElement implements Perl6RoutineDecl {
    private final String myKind;
    private final String myScope;
    private final String myName;
    private final String myIsMulti;
    private String myReturnType;
    private final Perl6Signature mySignature;
    private final String myDeprecationMessage;
    private final boolean myIsPure;
    private boolean myIsImplementationDetail;

    public ExternalPerl6RoutineDecl(Project project, PsiElement parent,
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
        mySignature = new ExternalPerl6Signature(project, parent, signature);
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
    public Perl6Parameter[] getParams() {
        return mySignature.getParameters();
    }

    @Override
    public @NotNull PsiElement @NotNull [] getChildren() {
        return new Perl6Signature[]{mySignature};
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
    public Perl6RoutineDeclStub getStub() {
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
        return mySignature.summary(new Perl6UnresolvedType(myReturnType));
    }

    @Nullable
    @Override
    public Perl6Signature getSignatureNode() {
        return mySignature;
    }

    @Nullable
    @Override
    public String getReturnsTrait() {
        return null;
    }

    @Override
    public @NotNull Perl6Type getReturnType() {
        return new Perl6UnresolvedType(myReturnType);
    }

    @Override
    public void contributeLexicalSymbols(Perl6SymbolCollector collector) {

    }

    @Override
    public void contributeMOPSymbols(Perl6SymbolCollector collector, MOPSymbolsAllowed symbolsAllowed) {
        if (!myScope.equals("has") || myName.equals("<anon>"))
            return;
        if (!symbolsAllowed.privateMethodsVisible && myName.startsWith("!"))
            return;
        if (!symbolsAllowed.submethodsVisible && myKind.equals("submethod"))
            return;

        Perl6ExplicitAliasedSymbol sym = new Perl6ExplicitAliasedSymbol(Perl6SymbolKind.Method, this,
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
