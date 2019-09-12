package edument.perl6idea.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiTreeUtil;
import edument.perl6idea.psi.*;
import edument.perl6idea.psi.stub.Perl6EnumStub;
import edument.perl6idea.psi.stub.Perl6EnumStubElementType;
import edument.perl6idea.psi.symbols.Perl6ExplicitAliasedSymbol;
import edument.perl6idea.psi.symbols.Perl6SymbolCollector;
import edument.perl6idea.psi.symbols.Perl6SymbolKind;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static edument.perl6idea.parsing.Perl6ElementTypes.STRING_LITERAL;
import static edument.perl6idea.parsing.Perl6TokenTypes.PAIR_KEY;

public class Perl6EnumImpl extends Perl6TypeStubBasedPsi<Perl6EnumStub> implements Perl6Enum {
    public Perl6EnumImpl(@NotNull ASTNode node) {
        super(node);
    }

    public Perl6EnumImpl(Perl6EnumStub stub, Perl6EnumStubElementType type) {
        super(stub, type);
    }

    @Override
    public String getEnumName() {
        return getName();
    }

    @Override
    public Collection<String> getEnumValues() {
        Perl6EnumStub stub = getStub();
        if (stub != null) {
            return stub.getEnumValues();
        }

        List<String> values = new ArrayList<>();
        PsiElement literal = findChildByFilter(TokenSet.create(STRING_LITERAL));
        if (literal != null) {
            String text = literal.getText();
            if (text.length() < 3)
                return values;
            text = text.substring(1, text.length()-1);
            String[] result = text.split("\\s+");
            values.addAll(Arrays.stream(result).filter(s -> !s.isEmpty()).collect(Collectors.toList()));
            return values;
        }
        PsiElement semilist = PsiTreeUtil.findChildOfType(this, Perl6SemiList.class);
        if (semilist != null) {
            Collection<Perl6FatArrow> keys = PsiTreeUtil.findChildrenOfType(semilist, Perl6FatArrow.class);
            for (Perl6FatArrow key : keys) {
                PsiElement child = key.getFirstChild();
                if (child != null && child.getNode().getElementType() == PAIR_KEY)
                    values.add(child.getText());
            }
        }
        return values;
    }

    @Override
    public void contributeLexicalSymbols(Perl6SymbolCollector collector) {
        super.contributeLexicalSymbols(collector);
        if (collector.isSatisfied()) return;
        String enumName = getEnumName();
        for (String type : getEnumValues()) {
            collector.offerSymbol(new Perl6ExplicitAliasedSymbol(Perl6SymbolKind.TypeOrConstant, this, type));
            if (collector.isSatisfied()) return;
            collector.offerSymbol(new Perl6ExplicitAliasedSymbol(Perl6SymbolKind.TypeOrConstant, this, enumName + "::" + type));
            if (collector.isSatisfied()) return;
        }
    }

    public String toString() {
        return getClass().getSimpleName() + "(Perl6:ENUM)";
    }
}
