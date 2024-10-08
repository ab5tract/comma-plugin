package org.raku.comma.inspection.inspections

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.editor.colors.CodeInsightColors
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.search.LocalSearchScope
import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.psi.util.PsiEditorUtil
import com.intellij.psi.util.PsiTreeUtil
import org.raku.comma.highlighter.RakuHighlighter
import org.raku.comma.inspection.RakuInspection
import org.raku.comma.psi.*

import org.raku.comma.inspection.InspectionConstants.UnusedRoutine.AUTOCALLED
import org.raku.comma.inspection.fixes.MakeSubroutineExportedFix
import org.raku.comma.inspection.fixes.RemoveUnusedRoutineFix

class UnusedRoutineInspection : RakuInspection() {

    override fun provideVisitFunction(holder: ProblemsHolder, element: PsiElement) {
        if (element !is RakuRoutineDecl) return
        if (element.routineKind == "method" && element.isPrivate) {
            // Private routines should be used within the enclosing package, so long
            // as it's not a role.
            val thePackage = PsiTreeUtil.getParentOfType(element, RakuPackageDecl::class.java)
            if (thePackage != null && thePackage.packageKind != "role" && !thePackage.trustsOthers()) {
                val searchScope = LocalSearchScope(thePackage)
                annotateIfUnused(holder, element, searchScope, "Unused private method")
            }
        } else if (element.isSub) {
            // Lexical subroutine declarations not exported or used as a value can be
            // marked as unused too. MAIN is called implicitly.
            val routineName = element.routineName
            if (routineName == null || AUTOCALLED.contains(routineName)) return
            if (element.isExported) return
            var context = element.getParent()
            if (context is RakuScopedDecl) {
                if (context.scope != "my") return
                context = context.getParent()
            }
            if (context !is RakuStatement) return

            // Find enclosing lexical and look for usages.
            val usageScope = PsiTreeUtil.getParentOfType(element, RakuPsiScope::class.java) ?: return
            annotateIfUnused(holder, element, LocalSearchScope(usageScope), "Unused subroutine")
        }
    }

    private fun annotateIfUnused(
        holder: ProblemsHolder,
        routine: RakuRoutineDecl,
        searchScope: LocalSearchScope,
        message: String
    ) {
        val results = ReferencesSearch.search(routine, searchScope)
        if (results.findFirst() == null) {
            val identifier = routine.nameIdentifier
            if (identifier != null) {
                val editor = PsiEditorUtil.findEditor(routine) ?: return

                val name = identifier.text
                if (! name.isNullOrEmpty()) {
                    customHighlight(editor, highlightTextRange(routine), RakuHighlighter.UNUSED)
                }

                val fixes = if (routine.isSub)
                                arrayOf(RemoveUnusedRoutineFix(name), MakeSubroutineExportedFix(name))
                            else
                                arrayOf(RemoveUnusedRoutineFix(name))

                holder.registerProblem(routine, message, ProblemHighlightType.INFORMATION, *fixes)
            }
        }
    }

    private fun highlightTextRange(element: RakuRoutineDecl): TextRange {
        val end = element.textOffset + element.declaratorNode.textLength + element.signature.length - 2
        return TextRange(element.declaratorNode.textOffset, end)
    }
}