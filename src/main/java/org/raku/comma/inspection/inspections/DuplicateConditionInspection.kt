package org.raku.comma.inspection.inspections

import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import com.intellij.util.containers.ContainerUtil
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.persistentSetOf
import org.raku.comma.inspection.RakuInspection
import org.raku.comma.psi.RakuIfStatement
import org.raku.comma.psi.RakuInfixApplication
import org.raku.comma.psi.RakuPsiElement
import org.raku.comma.utils.RakuOperatorUtils
import org.raku.comma.utils.RakuPsiUtil
import java.util.*
import java.util.stream.Collectors
import kotlin.Pair
import kotlin.collections.HashMap

class DuplicateConditionInspection : RakuInspection() {

    private val COMMUTERS: Set<String> = persistentSetOf(
        "==", "!=", "eq", "ne", "===", "cmp", "eqv", "=~=", "=:=", "(==)", "+", "*", "&", "|"
    )

    private val REVERSES: Map<String, String> = persistentMapOf(
        Pair(">", "<"),
        Pair(">=", "<="),
        Pair("gt", "lt"),
        Pair("ge", "le"),
        Pair("(<)", "(>)"),
        Pair("(<=)", "(>=)"),
        Pair("(<+)", "(>+)")
    )

    private val UNINORM: Map<String, String>

    init {
        val realPairs: List<Pair<String, String>> = ContainerUtil.zip(
            RakuOperatorUtils.unicodeOperators.stream().map { it.toString() }.toList(),
            RakuOperatorUtils.asciiOperators
        ).map { inPair -> Pair(inPair.first, inPair.second) }.toList()
        UNINORM = realPairs.stream().collect(Collectors.toMap({ it.first }, { it.second }))
    }

    override fun provideVisitFunction(holder: ProblemsHolder, element: PsiElement) {
        // Make sure it's an if/with/elsif/orwith structure at least two conditions.
        if (element !is RakuIfStatement) return
        val branches = element.branches
        if (branches.size < 2) return

        // Compute a normalization of all the conditions group them on that.
        val duplicated: MutableMap<String, MutableList<PsiElement>> = HashMap()
        for (branch in branches) {
            val condition = branch.condition ?: continue
            duplicated.computeIfAbsent(
                normalize(branch.term, condition)
            ) { _: String? -> ArrayList() }.add(condition)
        }

        // If there are any identical conditions, report them.
        if (duplicated.size == branches.size) return // Fail fast, all distinct

        val description = "An identical condition appears in a previous branch"
        for ((_, identical) in duplicated) {
            if (identical.size > 1) for (i in 1 until identical.size) {
                holder.registerProblem(identical[i], description, ProblemHighlightType.WARNING)
            }
        }
    }

    private fun normalize(term: PsiElement, condition: PsiElement): String {
        return "%s%s%s".format(normalize(term.text), "\u0000", normalize(condition))
    }

    private fun normalize(prefix: String): String {
        return when (prefix) {
            "if", "elsif" -> "if"
            "with", "orwith" -> "with"
            "without" -> "without"
            "unless" -> "unless"
            else -> ""
        }
    }

    private fun normalize(condition: PsiElement): String {
        // We normalize some binary infix operators.
        if (condition is RakuInfixApplication) {
            var op = condition.operator
            val operands = condition.operands
            if (op != null && operands.size == 2
                && operands[0] is RakuPsiElement
                && operands[1] is RakuPsiElement
            ) {
                // Calculate the normalization fo the operands.
                val lhsNorm = normalize(operands[0])
                val rhsNorm = normalize(operands[1])

                // Normalize any Unicode/ASCII variants.
                op = UNINORM.getOrDefault(op, op)

                // If it commutes, sort the normalization of the arguments.
                return if (COMMUTERS.contains(op)) {
                    if (lhsNorm.compareTo(rhsNorm) < 0) {
                        lhsNorm + "\u0000" + op + "\u0000" + rhsNorm
                    } else {
                        rhsNorm + "\u0000" + op + "\u0000" + lhsNorm
                    }
                } else if (REVERSES.containsKey(op)) {
                    rhsNorm + "\u0000" + REVERSES[op] + "\u0000" + lhsNorm
                } else {
                    rhsNorm + "\u0000" + op + "\u0000" + lhsNorm
                }
            }
        }

        // Fallback semantics is to go through the children minus whitespace and
        // nul-separate them.
        val parts = StringJoiner("\u0000")
        var current = RakuPsiUtil.skipSpaces(condition.firstChild, true, true)
        while (current != null) {
            parts.add(
                if (current is RakuPsiElement) normalize(current) else current.text
            )
            current = RakuPsiUtil.skipSpaces(current.nextSibling, true, true)
        }
        return parts.toString()
    }
}
