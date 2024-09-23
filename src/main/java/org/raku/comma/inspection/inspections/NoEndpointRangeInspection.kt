package org.raku.comma.inspection.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.util.endOffset
import org.raku.comma.inspection.InspectionConstants.NoEndpointRange.*
import org.raku.comma.inspection.RakuInspection
import org.raku.comma.inspection.fixes.NoEndPointRangeFix
import org.raku.comma.parsing.RakuTokenTypes
import org.raku.comma.psi.*

class NoEndpointRangeInspection : RakuInspection() {
    override fun provideVisitFunction(holder: ProblemsHolder, element: PsiElement) {
        if (element !is RakuInfix) return
        if (element.operator.text != "..") return
        var next = element.getNextSibling()
        while (next != null && next.node.elementType === RakuTokenTypes.UNV_WHITE_SPACE || next is PsiWhiteSpace) {
            next = next.nextSibling
        }
        if (ENDPOINT_RELEVANCE.test(next)) return

        holder.registerProblem(element, DESCRIPTION, NoEndPointRangeFix(element.endOffset))
    }
}