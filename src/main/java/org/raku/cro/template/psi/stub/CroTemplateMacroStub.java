package org.raku.cro.template.psi.stub;

import com.intellij.psi.stubs.StubElement;
import org.raku.cro.template.psi.CroTemplateMacro;

public interface CroTemplateMacroStub extends StubElement<CroTemplateMacro> {
    String getName();
}
