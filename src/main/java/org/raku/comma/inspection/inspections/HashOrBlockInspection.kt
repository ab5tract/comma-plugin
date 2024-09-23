package org.raku.comma.inspection.inspections

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import org.raku.comma.inspection.RakuInspection
import org.raku.comma.parsing.RakuTokenTypes
import org.raku.comma.psi.RakuBlockOrHash

class HashOrBlockInspection : RakuInspection() {
    override fun provideVisitFunction(holder: ProblemsHolder, element: PsiElement) {

        // If it looks nothing like a hash, nothing to do.
        if (element !is RakuBlockOrHash) return
        if (! element.isHashish) return


        // If it looks like a hash, but actually isn't one, then warn; perhaps a thinko.
        if (! element.isHash) {
            val description = "This will be taken as a block, not as a hash as may have been intended"
            holder.registerProblem(element, description, ProblemHighlightType.WARNING)
        } else {
            val blockoid = element.block
            if (blockoid != null) {
                val node = blockoid.node
                hashComposer(holder, node.findChildByType(RakuTokenTypes.BLOCK_CURLY_BRACKET_OPEN))
                hashComposer(holder, node.findChildByType(RakuTokenTypes.BLOCK_CURLY_BRACKET_CLOSE))
            }
        }
    }

    private fun hashComposer(holder: ProblemsHolder, brace: ASTNode?) {
        if (brace != null) {
            brace.psi.reference?.let { holder.registerProblem(it, ProblemHighlightType.INFORMATION) }
        }
    }
}