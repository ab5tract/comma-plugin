package org.raku.comma.inspection.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import kotlinx.collections.immutable.persistentSetOf
import org.raku.comma.inspection.RakuInspection
import org.raku.comma.inspection.fixes.RemoveRedundantInitializerFix
import org.raku.comma.psi.*

class RedundantInitializationInspection : RakuInspection() {
    private val sigils : Set<Char>  = persistentSetOf('@', '%')

    override fun provideVisitFunction(holder: ProblemsHolder, element: PsiElement) {
        if (element !is RakuVariableDecl) return

        val names = element.variableNames
        if (names.size != 1) return
        val name = names[0]
        val sigil = RakuVariable.getSigil(name)
        if (! sigils.contains(sigil)) return

        var shouldAnnotate = false
        val initializer = element.initializer

        if (sigil == '@' && initializer is RakuArrayComposer) {
            shouldAnnotate = initializer.elements.isEmpty()
        } else if (initializer is RakuParenthesizedExpr) {
            shouldAnnotate = initializer.elements.isEmpty()
        } else if (sigil == '%' && initializer is RakuBlockOrHash) {
            shouldAnnotate = initializer.elements.isEmpty()
        }

        if (shouldAnnotate) {
            val description = "Initialization of empty %s is redundant".format(if (sigil == '@') "Array" else "Hash")
            holder.registerProblem(element, description, RemoveRedundantInitializerFix())
        }
    }
}