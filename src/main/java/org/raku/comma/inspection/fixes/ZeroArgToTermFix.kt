package org.raku.comma.inspection.fixes

import com.intellij.codeInsight.intention.LowPriorityAction
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.util.IntentionName
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.psi.util.PsiEditorUtil
import com.intellij.psi.util.childrenOfType
import org.raku.comma.psi.RakuLongName
import org.raku.comma.psi.RakuRoutineDecl
import org.raku.comma.psi.RakuSignature

class ZeroArgToTermFix(private val name: String) : LocalQuickFix, LowPriorityAction {

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val declaration: RakuRoutineDecl = descriptor.psiElement as RakuRoutineDecl
        val editor = PsiEditorUtil.findEditor(declaration.containingFile) ?: return

        val longName: RakuLongName = declaration.childrenOfType<RakuLongName>().firstOrNull() ?: return
        val maybeSpace = if (longName.nextSibling is RakuSignature) " " else ""

        WriteCommandAction.runWriteCommandAction(project) {
            editor.document.replaceString(longName.textOffset,
                                          longName.textOffset + longName.textLength,
                                          "term:<%s>%s".format(name, maybeSpace))
        }
    }

    override fun getName(): @IntentionName String {
        return "Convert sub '%s' to term".format(name)
    }

    override fun getFamilyName(): String {
        return "Convert parameter-less sub to term"
    }
}