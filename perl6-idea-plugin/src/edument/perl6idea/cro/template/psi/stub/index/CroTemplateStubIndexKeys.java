package edument.perl6idea.cro.template.psi.stub.index;

import com.intellij.psi.stubs.StubIndexKey;
import edument.perl6idea.cro.template.psi.CroTemplateMacro;
import edument.perl6idea.cro.template.psi.CroTemplateSub;

public class CroTemplateStubIndexKeys {
    public static final StubIndexKey<String, CroTemplateSub> TEMPLATE_SUB
            = StubIndexKey.createIndexKey("croTemplate.subs");
    public static final StubIndexKey<String, CroTemplateMacro> TEMPLATE_MACRO
            = StubIndexKey.createIndexKey("croTemplate.macros");
}
