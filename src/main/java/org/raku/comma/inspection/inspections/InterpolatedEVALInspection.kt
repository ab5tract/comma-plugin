package org.raku.comma.inspection.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.raku.comma.inspection.fixes.AddEvalPragmaFix
import org.raku.comma.inspection.RakuInspection
import org.raku.comma.psi.*

class InterpolatedEVALInspection : RakuInspection() {
    override fun provideVisitFunction(holder: ProblemsHolder, element: PsiElement) {
        if (element !is RakuSubCall) return
        if (element.callName != "EVAL") return

        val args = (element as RakuSubCall).callArguments ?: return

        var scope = PsiTreeUtil.getParentOfType(
            element,
            RakuPsiScope::class.java
        )
        while (scope != null) {
            val list = PsiTreeUtil.findChildOfType(
                scope,
                RakuStatementList::class.java
            )
            if (list == null) break
            var stats = PsiTreeUtil.getChildrenOfType(
                list,
                RakuStatement::class.java
            )
            if (stats == null) stats = arrayOfNulls(0)
            for (statement in stats) {
                if (statement!!.textOffset > element.getTextOffset()) break
                for (child in statement!!.children) {
                    if (child !is RakuUseStatement) continue
                    val moduleName = child.moduleName
                    if (moduleName == null || moduleName == "MONKEY" ||
                        moduleName == "MONKEY-SEE-NO-EVAL"
                    ) return
                }
            }
            scope = PsiTreeUtil.getParentOfType(scope, RakuPsiScope::class.java)
        }

        for (arg in args) {
            if (arg is RakuStrLiteral) {
                val t = arg.getText() // Literal text
                if (t.startsWith("Q")) return
                if (t.startsWith("q") && !t.startsWith("qq")) return
                // Check is variable used
                if (PsiTreeUtil.findChildOfType(arg, RakuVariable::class.java) == null) return
            }
        }

        val description = "Cannot EVAL interpolated expression without MONKEY-SEE-NO-EVAL pragma"
        holder.registerProblem(element, description, AddEvalPragmaFix())
    }
}