package org.raku.comma.inspection.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import org.raku.comma.inspection.InspectionConstants.NamedPairArgument.*
import org.raku.comma.inspection.RakuInspection
import org.raku.comma.inspection.fixes.FatarrowSimplificationFix
import org.raku.comma.psi.*

class NamedPairArgumentInspection : RakuInspection() {
    override fun provideVisitFunction(holder: ProblemsHolder, element: PsiElement) {
        when (element) {
            is RakuColonPair -> checkColonPair(element, holder)
            is RakuFatArrow  -> checkFatArrow(element, holder)
        }
    }

    private fun checkFatArrow(arrow: RakuFatArrow, holder: ProblemsHolder) {
        processPair(arrow.value, arrow.key, arrow, holder)
    }

    private fun checkColonPair(pair: RakuColonPair, holder: ProblemsHolder) {
        val key = pair.key ?: return
        val value = pair.statement ?: return
        val child = value.firstChild

        if (child !is RakuVariable) return
        processPair(child, key, pair, holder)
    }

    private fun processPair(element: PsiElement, key: String, pair: PsiElement, holder: ProblemsHolder) {
        holder.registerProblem(pair, DESCRIPTION, FatarrowSimplificationFix(getSimplifiedPair(pair, key, element)))
    }
}