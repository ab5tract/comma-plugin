package org.raku.comma.inspection.inspections

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import org.raku.comma.inspection.RakuInspection
import org.raku.comma.psi.RakuDeprecatable
import org.raku.comma.psi.RakuMethodCall

class DeprecatedMethodInspection : RakuInspection() {
    override fun provideVisitFunction(holder: ProblemsHolder, element: PsiElement) {
        if (element is RakuMethodCall) {
            val reference = element.getReference() ?: return
            val resolution = reference.resolve()
            if (resolution is RakuDeprecatable && (resolution as RakuDeprecatable).isDeprecated) {
                val methodName = element.simpleName
                if (methodName == null || methodName.textLength == 0) return
                val deprecationMessage = (resolution as RakuDeprecatable).deprecationMessage
                val useMessage = if (deprecationMessage != null) " ... use $deprecationMessage" else ""
                val message = "method '%s' is deprecated%s".format(methodName.text, useMessage)
                holder.registerProblem(element, message, ProblemHighlightType.LIKE_DEPRECATED)
            }
        }
    }
}