package org.raku.cro.template.psi.stub;

import com.intellij.psi.stubs.StubElement;
import org.raku.cro.template.psi.CroTemplateSub;

public interface CroTemplateSubStub extends StubElement<CroTemplateSub> {
    String getName();
}
