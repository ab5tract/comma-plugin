package org.raku.comma.inspection.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import org.raku.comma.inspection.InspectionConstants.MonitorUsage.*
import org.raku.comma.inspection.RakuInspection
import org.raku.comma.inspection.fixes.AddUseModuleFix
import org.raku.comma.psi.RakuPackageDecl
import org.raku.comma.utils.RakuUseUtils

class MonitorUsageInspection : RakuInspection() {
    override fun provideVisitFunction(holder: ProblemsHolder, element: PsiElement) {
        if (element !is RakuPackageDecl) return

        val packageKind = element.packageKind
        if (packageKind != "monitor") return
        if (RakuUseUtils.usesModule(element, OO_MONITORS)) return

        val packageName = element.packageName ?: return
        val declarationLength = 8 + packageName.length
        holder.registerProblem(element, TextRange(0, declarationLength), DESCRIPTION, AddUseModuleFix(OO_MONITORS))
    }
}