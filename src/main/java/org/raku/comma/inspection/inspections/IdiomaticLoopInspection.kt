package org.raku.comma.inspection.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import org.raku.comma.inspection.RakuInspection
import org.raku.comma.inspection.fixes.IdiomaticLoopFix
import org.raku.comma.parsing.RakuTokenTypes
import org.raku.comma.psi.RakuIntLiteral
import org.raku.comma.psi.RakuParenthesizedExpr
import org.raku.comma.psi.RakuTypeName
import org.raku.comma.psi.RakuWhileStatement

class IdiomaticLoopInspection : RakuInspection() {
    private val description = "Idiomatic 'loop' construction can be used instead"

// TODO: Verify that doing the statement.firstChild.nextSibling.firstChild.firstChild
// is not actually slower than just doing a regex on statement.text
//    private val regex = Regex("\\(\\s*((True)?|(1)?)\\s*\\)")

    override fun provideVisitFunction(holder: ProblemsHolder, element: PsiElement) {
        if (element !is RakuWhileStatement) return
        val keyword = element.getFirstChild()
        var statement = keyword.nextSibling
        while (statement != null
            && (statement is PsiWhiteSpace || statement.node.elementType === RakuTokenTypes.UNV_WHITE_SPACE))
        {
            statement = statement.nextSibling
        }
        if (statement == null) return

        val condition = (statement as RakuParenthesizedExpr).firstChild.nextSibling.firstChild.firstChild
        val trueOr1 = when (condition) {
            is RakuIntLiteral -> true
            is RakuTypeName -> "True" == condition.text
            else -> false
        }

        if (trueOr1) {
            holder.registerProblem(element, description, IdiomaticLoopFix())
        }
    }
}