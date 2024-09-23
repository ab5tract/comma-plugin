package org.raku.comma.inspection

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiElementVisitor

class RakuPsiElementVisitor(
    val holder: ProblemsHolder,
    val visitElementFunction: (ProblemsHolder, PsiElement) -> Unit
) : PsiElementVisitor()
{
    override fun visitElement(element: PsiElement) {
        visitElementFunction(holder, element)
    }
}