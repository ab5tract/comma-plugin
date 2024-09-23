package org.raku.comma.inspection.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import org.raku.comma.inspection.RakuInspection
import org.raku.comma.inspection.fixes.ReplaceHyphenWithRangeFix
import org.raku.comma.parsing.RakuTokenTypes
import org.raku.comma.psi.RakuRegexCClassElem

class HyphenInCharacterClassInspection : RakuInspection() {
    private val description =
            """
            A hyphen is used in a character class, maybe '..' was intended to denote a range? 
            Otherwise a hyphen should be at the end of the character class.
            """


    override fun provideVisitFunction(holder: ProblemsHolder, element: PsiElement) {
        if (element is RakuRegexCClassElem) {
            var child = element.getFirstChild()
            while (child != null) {
                if (child.node.elementType !== RakuTokenTypes.REGEX_CCLASS_SYNTAX && child.text == "-") {
                    val prev = child.node.treePrev
                    if (prev != null && prev.elementType === RakuTokenTypes.REGEX_CCLASS_SYNTAX && prev.text == "[") {
                        child = child.nextSibling
                        continue
                    }
                    holder.registerProblem(child, description, ReplaceHyphenWithRangeFix())
                }
                child = child.nextSibling
                val sibling = child?.nextSibling
                if (sibling != null && sibling.text == "]") return
            }
        }
    }
}