package org.raku.comma.cro.template.psi.stub;

import com.intellij.psi.stubs.StubElement;
import org.raku.comma.cro.template.psi.CroTemplatePart;

public interface CroTemplatePartStub extends StubElement<CroTemplatePart> {
    String getName();
}
