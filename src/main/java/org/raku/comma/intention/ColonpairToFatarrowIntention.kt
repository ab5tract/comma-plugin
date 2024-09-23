package org.raku.comma.intention

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInspection.util.IntentionFamilyName
import com.intellij.codeInspection.util.IntentionName
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.IncorrectOperationException
import org.raku.comma.psi.RakuColonPair
import org.raku.comma.psi.RakuElementFactory
import org.raku.comma.inspection.InspectionConstants.NamedPairArgument.getSimplifiedPair

class ColonpairToFatarrowIntention : IntentionAction {
    override fun getText(): @IntentionName String {
        return "Convert to fat arrow form"
    }

    override fun getFamilyName(): @IntentionFamilyName String {
        return text
    }

    override fun isAvailable(project: Project, editor: Editor, file: PsiFile): Boolean {
        val pair = PsiTreeUtil.getParentOfType(
            file.findElementAt(editor.caretModel.offset),
            RakuColonPair::class.java
        )
        return pair != null && getSimplifiedPair(pair, pair.key, pair.statement.firstChild) == null
    }

    @Throws(IncorrectOperationException::class)
    override fun invoke(project: Project, editor: Editor, file: PsiFile) {
        val pair = checkNotNull(
            PsiTreeUtil.getParentOfType(
                file.findElementAt(editor.caretModel.offset),
                RakuColonPair::class.java
            )
        )
        pair.replace(RakuElementFactory.createFatArrow(project, pair.key, pair.statement))
    }

    override fun startInWriteAction(): Boolean {
        return true
    }
}
