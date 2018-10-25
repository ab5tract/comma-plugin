package edument.perl6idea.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.TokenSet;
import edument.perl6idea.psi.Perl6Enum;
import edument.perl6idea.psi.Perl6StrLiteral;
import edument.perl6idea.psi.Perl6TypeStubBasedPsi;
import edument.perl6idea.psi.stub.Perl6EnumStub;
import edument.perl6idea.psi.stub.Perl6EnumStubElementType;
import edument.perl6idea.psi.symbols.Perl6ExplicitAliasedSymbol;
import edument.perl6idea.psi.symbols.Perl6ExplicitSymbol;
import edument.perl6idea.psi.symbols.Perl6SymbolCollector;
import edument.perl6idea.psi.symbols.Perl6SymbolKind;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static edument.perl6idea.parsing.Perl6ElementTypes.STRING_LITERAL;

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
    public List<String> getEnumValues() {
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
            String[] result = text.split(" ");
            values.addAll(Arrays.asList(result));
        }
        return values;
    }

    @Override
    public void contributeSymbols(Perl6SymbolCollector collector) {
        super.contributeSymbols(collector);
        List<String> enumValues = getEnumValues();
        for (String type : enumValues)
            collector.offerSymbol(new Perl6ExplicitAliasedSymbol(Perl6SymbolKind.TypeOrConstant, this, type));
    }

    public String toString() {
        return getClass().getSimpleName() + "(Perl6:ENUM)";
    }
}
