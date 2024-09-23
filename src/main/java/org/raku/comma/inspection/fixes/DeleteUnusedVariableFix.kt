package org.raku.comma.inspection.fixes

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import com.intellij.psi.util.PsiTreeUtil
import org.raku.comma.psi.RakuStatement
import org.raku.comma.psi.RakuVariable
import org.raku.comma.psi.RakuVariableDecl
import org.raku.comma.utils.RakuPsiUtil

class DeleteUnusedVariableFix(private val name: String) : LocalQuickFix {

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val element = descriptor.psiElement
        val variable = PsiTreeUtil.getNonStrictParentOfType(element, RakuVariable::class.java)
        val declaration = PsiTreeUtil.getParentOfType(element, RakuVariableDecl::class.java) ?: return

        RakuPsiUtil.deleteElementDocComments(PsiTreeUtil.getParentOfType(declaration, RakuStatement::class.java))
        declaration.removeVariable(variable)
    }

    override fun getName(): String { return "Safe delete unused variable '%s'".format(name) }
    override fun getFamilyName(): String { return "Safe delete unused variable" }
}