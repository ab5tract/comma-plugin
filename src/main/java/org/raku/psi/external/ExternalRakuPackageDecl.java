package org.raku.psi.external;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import org.raku.psi.*;
import org.raku.psi.stub.RakuPackageDeclStub;
import org.raku.psi.symbols.*;
import org.raku.psi.type.RakuResolvedType;
import org.raku.psi.type.RakuType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExternalRakuPackageDecl extends RakuExternalPsiElement implements RakuPackageDecl {
    private final String myType;
    private List<RakuRoutineDecl> myRoutines = new ArrayList<>();
    private List<RakuVariableDecl> myAttributes = new ArrayList<>();
    private List<String> myMRO = new ArrayList<>();
    private final String myPackageKind;
    private String myName;
    private final Set<String> myGettersPool = new HashSet<>();
    private RakuPackageDecl myMetaClass;

    public ExternalRakuPackageDecl(Project project, RakuFile file, String kind, String name, String type, String base,
                                   List<RakuRoutineDecl> routines, List<RakuVariableDecl> attrs, List<String> mro,
                                   RakuPackageDecl metaClass) {
        this(project, file, kind, name, type, base);
        myMetaClass = metaClass;
        myMRO = mro;
        myRoutines = routines;
        myAttributes = attrs;
        for (RakuVariableDecl decl : myAttributes) {
            String[] names = decl.getVariableNames();
            for (String getterName : names) {
                if (RakuVariable.getTwigil(getterName) == '.')
                    myGettersPool.add(getterName.substring(2)); // cut off sigil
            }
        }
    }

    public ExternalRakuPackageDecl(Project project, RakuFile file, String kind, String name, String type, String base) {
        myProject = project;
        myParent = file;
        switch (kind) {
            case "ro":
                myPackageKind = "role";
                break;
            case "c":
                myPackageKind = "class";
                break;
            default:
                myPackageKind = "";
        }
        myName = name;
        myType = type;
    }

    @Override
    public void setMetaClass(RakuPackageDecl metaClass) {
        myMetaClass = metaClass;
    }

    @Override
    public @Nullable RakuPackageDecl getMetaClass() {
        return myMetaClass;
    }

    @Override
    public String getPackageKind() {
        return myPackageKind;
    }

    @Override
    public String getPackageName() {
        return getName();
    }

    @Override
    public boolean isStubbed() {
        return false;
    }

    @Override
    public PsiElement setName(@NotNull String name) {
        myName = name;
        return null;
    }

    @NotNull
    @Override
    public String getName() {
        return myName;
    }

    @Nullable
    @Override
    public PsiElement getPackageKeywordNode() {
        return null;
    }

    @Override
    public void contributeNestedPackagesWithPrefix(RakuSymbolCollector collector, String prefix) {

    }

    @Override
    public List<RakuPackageDecl> collectChildren() {
        return new ArrayList<>();
    }

    @Override
    public List<RakuPackageDecl> collectParents() {
        return new ArrayList<>();
    }

    @Override
    public boolean trustsOthers() {
        return false;
    }

    @Override
    public IStubElementType<?, ?> getElementType() {
        return null;
    }

    @Override
    public RakuPackageDeclStub getStub() {
        return null;
    }

    @Override
    public @NotNull String getScope() {
        return "our";
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return null;
    }

    @Override
    public void contributeLexicalSymbols(RakuSymbolCollector collector) {

    }

    @Override
    public @NotNull RakuType inferType() {
        return new RakuResolvedType(myType, this);
    }

    @Override
    public void contributeMOPSymbols(RakuSymbolCollector collector, MOPSymbolsAllowed symbolsAllowed) {
        for (RakuRoutineDecl routine : myRoutines) {
            String name = routine.getRoutineName();
            if (!symbolsAllowed.privateMethodsVisible && name.startsWith("!"))
                continue;
            if (!symbolsAllowed.submethodsVisible && routine.getRoutineKind().equals("submethod"))
                continue;
            if (myGettersPool.contains(name))
                continue;
            routine.contributeMOPSymbols(collector, symbolsAllowed);
            if (collector.isSatisfied()) return;
        }
        for (RakuVariableDecl variable : myAttributes) {
            variable.contributeMOPSymbols(collector, symbolsAllowed);
            if (collector.isSatisfied()) return;
        }
        for (String mroParent : myMRO) {
            RakuSymbol parent = resolveLexicalSymbol(RakuSymbolKind.TypeOrConstant, mroParent);
            if (parent != null && parent.getPsi() instanceof RakuPackageDecl decl) {
                decl.contributeMOPSymbols(collector, symbolsAllowed);
            }
        }
        RakuPackageDecl metaClass = getMetaClass();
        if (metaClass == null)
            return;

        collector.decreasePriority();
        metaClass.contributeMOPSymbols(collector, symbolsAllowed);
    }

    public void setRoutines(List<RakuRoutineDecl> routines) {
        myRoutines = routines;
    }

    public void setAttributes(List<RakuVariableDecl> attributes) {
        myAttributes = attributes;
    }

    @Override
    public RakuParameter[] getSignature() {
        // TODO
        return new RakuParameter[0];
    }
}
