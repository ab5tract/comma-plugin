package org.raku.comma.inspection.inspections

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import kotlinx.collections.immutable.persistentSetOf
import org.raku.comma.inspection.RakuInspection
import org.raku.comma.parsing.RakuTokenTypes
import org.raku.comma.psi.RakuScopedDecl
import org.raku.comma.psi.RakuSignature
import org.raku.comma.psi.RakuVariable
import org.raku.comma.psi.RakuVariableDecl

class IllegalVariableDeclarationInspection : RakuInspection() {

    private val namedMatchFirstChildSet: Set<String> = persistentSetOf("$<", "@<", "%<")
    private val contextualizerSet: Set<String> = persistentSetOf("$", "@", "%")

    private val namedMatchDescription = "Cannot declare a regex named match variable"
    private val posMatchDescription = "Cannot declare a regex positional match variable"
    private val contextualizerDescription = "Cannot declare a contextualizer"

    override fun provideVisitFunction(holder: ProblemsHolder, element: PsiElement) {
        if (element !is RakuScopedDecl) return

        val varDecl = PsiTreeUtil.getChildOfType(element, RakuVariableDecl::class.java) ?: return
        val variable = PsiTreeUtil.getChildOfType(varDecl, RakuVariable::class.java) ?: return

        val firstChild = variable.firstChild
        if (firstChild!= null && firstChild.node.elementType === RakuTokenTypes.REGEX_CAPTURE_NAME) {
                // $<foo>, @<foo>, %<foo>
            if (namedMatchFirstChildSet.contains(firstChild.text)  && variable.lastChild.text == ">") {
                holder.registerProblem(element, namedMatchDescription, ProblemHighlightType.ERROR)
            }   // $ + integer
            else {
                holder.registerProblem(element, posMatchDescription, ProblemHighlightType.ERROR)
            }
        }


        // Check out contextualizer
        if (contextualizerSet.contains(variable.text) && variable.nextSibling is RakuSignature) {
            holder.registerProblem(element, contextualizerDescription, ProblemHighlightType.ERROR)
        }
    }
}