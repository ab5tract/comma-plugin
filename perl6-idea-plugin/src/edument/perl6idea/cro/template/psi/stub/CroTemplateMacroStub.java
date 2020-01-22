package edument.perl6idea.cro.template.psi.stub;

import com.intellij.psi.stubs.StubElement;
import edument.perl6idea.cro.template.psi.CroTemplateMacro;

public interface CroTemplateMacroStub extends StubElement<CroTemplateMacro> {
    String getName();
}
