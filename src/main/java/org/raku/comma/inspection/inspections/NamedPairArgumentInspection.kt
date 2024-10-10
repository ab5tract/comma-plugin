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
        processPair(arrow, holder, "FatArrow")
    }

    private fun checkColonPair(pair: RakuColonPair, holder: ProblemsHolder) {
        val key = pair.key ?: return
        val value = pair.statement ?: return
        val child = value.firstChild
        if (getSimplifiedPair(pair, key, child) == null) return

        processPair(pair, holder, "ColonPair")
    }

    private fun processPair(pair: PsiElement, holder: ProblemsHolder, pairKind: String) {
        holder.registerProblem(pair, DESCRIPTION, FatarrowSimplificationFix(pairKind))
    }
}