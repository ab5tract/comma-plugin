package org.raku.comma.cro.template.findUsages;

import com.intellij.psi.PsiElement;
import com.intellij.usages.impl.rules.UsageType;
import com.intellij.usages.impl.rules.UsageTypeProvider;
import org.raku.comma.cro.template.psi.CroTemplateApply;
import org.raku.comma.cro.template.psi.CroTemplateCall;
import org.raku.comma.cro.template.psi.CroTemplateVariableAccess;
import org.jetbrains.annotations.Nullable;

public class CroTemplateUsageTypeProvider implements UsageTypeProvider {
    public static final UsageType MACRO_CALL_USAGE = new UsageType(() -> "Macro call usage");
    public static final UsageType SUB_CALL_USAGE = new UsageType(() -> "Sub call usage");
    public static final UsageType VARIABLE_USAGE = new UsageType(() -> "Variable usage");

    @Nullable
    @Override
    public UsageType getUsageType(PsiElement element) {
        if (element instanceof CroTemplateApply)
            return MACRO_CALL_USAGE;
        if (element instanceof CroTemplateCall)
            return SUB_CALL_USAGE;
        if (element instanceof CroTemplateVariableAccess)
            return VARIABLE_USAGE;
        return null;
    }
}
