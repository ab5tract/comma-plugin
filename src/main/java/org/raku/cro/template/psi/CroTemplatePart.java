package org.raku.cro.template.psi;

import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.StubBasedPsiElement;
import org.raku.cro.template.psi.stub.CroTemplatePartStub;

public interface CroTemplatePart extends StubBasedPsiElement<CroTemplatePartStub>, PsiNamedElement, Scope {
}
