package org.raku.comma.inspection.fixes

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import org.raku.comma.psi.RakuElementFactory
import org.raku.comma.psi.RakuRoutineDecl

class MakeSubroutineExportedFix(private val name: String) : LocalQuickFix {
    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
// TODO: Verify if there is any reason to use this caret approach
//        val caretEl: PsiElement = file.findElementAt(editor.getCaretModel().getOffset()) ?: return
//        val decl = PsiTreeUtil.getParentOfType(
//            caretEl,
//            RakuRoutineDecl::class.java
//        ).
        val declaration = descriptor.psiElement as RakuRoutineDecl
        val trait = RakuElementFactory.createTrait(project, "is", "export")
        declaration.addBefore(trait, declaration.lastChild)
    }

    override fun getName(): String { return "Add export trait to '%s'".format(name) }
    override fun getFamilyName(): String { return "Add export trait" }
}