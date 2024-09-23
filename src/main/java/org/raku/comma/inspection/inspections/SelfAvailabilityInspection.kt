package org.raku.comma.inspection.inspections

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.raku.comma.inspection.RakuInspection
import org.raku.comma.inspection.fixes.UseDirectAttributeAccessFix
import org.raku.comma.psi.*

class SelfAvailabilityInspection : RakuInspection() {
    private enum class Availability { NONE, PARTIAL, FULL }

    override fun provideVisitFunction(holder: ProblemsHolder, element: PsiElement) {
        // The self PSI element shows up when we literally have the `self` token, or
        // when we have `$.foo` (it's the `$` in this case). Since annotators can only
        // be used to annotate things below what they're invoked on, we'll have to
        // handle these cases separately.
        if (element is RakuSelf && element.getTextLength() == 4) {
            // Literal self. If there's no self available at all, complain about that.
            if (getAvailability(element) == Availability.NONE) {
                holder.registerProblem(element, "No invocant is available here", ProblemHighlightType.ERROR)
            }
        } else if (element is RakuPostfixApplication) {
            val caller = element.operand
            if (caller is RakuSelf && caller.getTextLength() == 1) {
                val availability = getAvailability(element)
                if (availability == Availability.NONE) {
                    holder.registerProblem(element, "No invocant is available here", ProblemHighlightType.ERROR)
                } else if (availability == Availability.PARTIAL) {
                    val description = "Virtual method calls are not allowed on partially constructed objects"

                    // It's probably of the form $.foo, so offer an intention to turn it
                    // into $!foo.
                    val postfix = element.postfix
                    if (postfix is RakuMethodCall && postfix.callArguments.isEmpty()) {
                        val name = postfix.simpleName
                        if (name != null) {
                            val newAttributeName = caller.getText() + "!" + name.text
                            holder.registerProblem(element, description, UseDirectAttributeAccessFix(newAttributeName))
                        }
                    } else {
                        holder.registerProblem(element, description, ProblemHighlightType.ERROR)
                    }
                }
            }
        }
    }

    private fun getAvailability(element: PsiElement): Availability {
        var from: PsiElement? = element
        while (true) {
            // self can be provided by being in a method or the initializer of a variable
            // declaration of scope `has`.
            val possibleSelfProvider: RakuPsiDeclaration? = PsiTreeUtil.getParentOfType(
                from,
                RakuRoutineDecl::class.java,
                RakuRegexDecl::class.java,
                RakuVariableDecl::class.java
            )
            if (possibleSelfProvider is RakuRoutineDecl) {
                val kind = possibleSelfProvider.routineKind
                if ("method" == kind) return Availability.FULL
                if ("submethod" == kind) return Availability.PARTIAL
                // Could be a sub within a context that provides a self, so keep going.
                from = possibleSelfProvider
            } else if (possibleSelfProvider is RakuRegexDecl) {
                return Availability.FULL
            } else if (possibleSelfProvider is RakuVariableDecl) {
                val scope = possibleSelfProvider.getScope()
                if (scope == "has" || scope == "HAS") return Availability.PARTIAL
                // Could just be a variable decl in a context that provides a self, so
                // keep searching.
                from = possibleSelfProvider
            } else {
                return Availability.NONE
            }
        }
    }
}