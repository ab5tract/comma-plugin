package org.raku.comma.inspection.inspections

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.raku.comma.inspection.InspectionConstants.NonNilReturn.DESCRIPTION
import org.raku.comma.inspection.RakuInspection
import org.raku.comma.psi.RakuRoutineDecl
import org.raku.comma.psi.RakuSubCall
import org.raku.comma.sdk.RakuSdkType
import org.raku.comma.sdk.RakuSettingTypeId

class NonNilReturnInspection : RakuInspection() {
    override fun provideVisitFunction(holder: ProblemsHolder, element: PsiElement) {
        if (element !is RakuSubCall) return

        if (element.callName != "return") return

        if (element.callArguments.isEmpty()) return

        val routineDecl = PsiTreeUtil.getParentOfType(element, RakuRoutineDecl::class.java) ?: return

        val retType = routineDecl.returnType
        val nilType = RakuSdkType.getInstance().getCoreSettingType(routineDecl.project, RakuSettingTypeId.Nil)
        if (nilType != retType) return

        holder.registerProblem(element, DESCRIPTION, ProblemHighlightType.ERROR)
    }
}