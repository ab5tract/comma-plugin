package org.raku.comma.extensions

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement

class RakuGotoDeclarationsHandler : GotoDeclarationHandler {
    override fun getGotoDeclarationTargets(element: PsiElement?, offsetInFile: Int, editor: Editor?): Array<PsiElement>? {
        TODO("Not yet implemented")
    }
}