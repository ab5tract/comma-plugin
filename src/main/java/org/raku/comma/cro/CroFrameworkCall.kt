package org.raku.comma.cro

import com.intellij.navigation.ItemPresentation
import com.intellij.navigation.NavigationItem
import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation
import com.intellij.openapi.project.Project
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubIndex
import com.intellij.psi.util.PsiTreeUtil
import org.raku.comma.RakuIcons
import org.raku.comma.contribution.Filtering
import org.raku.comma.extensions.RakuFrameworkCall
import org.raku.comma.psi.*
import org.raku.comma.psi.stub.RakuSubCallStub
import java.util.function.Consumer
import javax.swing.Icon

class CroFrameworkCall : RakuFrameworkCall() {
    override val frameworkName: String = "Cro Router"

    override fun isApplicable(call: RakuSubCall): Boolean {
        // We can't resolve the symbol at indexing time to check if it's coming from
        // the Cro HTTP router, so we just go on callee name and it having a single
        // sub argument for now.
        val calleeName = call.getCallName()
        return ROUTE_VERBS.contains(calleeName) && getRouteSignature(call) != null
    }

    override fun getFrameworkData(call: RakuSubCall): MutableMap<String, String> {
        val buffer = StringBuilder()
        val params: Array<RakuParameter>? = getRouteSignature(call)
        val result: MutableMap<String, String> = mutableMapOf()
        if (params != null) {
            for (param in params) renderParameter(buffer, param)
            result.put("method", call.getCallName())
            result.put("path", if (buffer.isEmpty()) "/" else buffer.toString())
        }
        return result
    }

    override fun indexStub(
        stub: RakuSubCallStub,
        frameworkData: MutableMap<String, String>,
        sink: IndexSink
    ) {
        sink.occurrence<RakuSubCall, String>(CroIndexKeys.CRO_ROUTES, frameworkData["path"]!!)
    }

    override fun contributeSymbolNames(project: Project, results: MutableList<String>) {
        results.addAll(CroRouteIndex.getInstance().getAllKeys(project))
    }

    override fun contributeSymbolItems(
        project: Project,
        pattern: String,
        results: MutableList<NavigationItem>
    ) {
        val routeIndex = CroRouteIndex.getInstance()
        Filtering.simpleMatch(routeIndex.getAllKeys(project), pattern).forEach(Consumer { route: String ->
            results.addAll(
                StubIndex.getElements<String, RakuSubCall>(
                    routeIndex.key,
                    route,
                    project,
                    GlobalSearchScope.projectScope(project),
                    RakuSubCall::class.java
                )
            )
        })
    }

    override fun getNavigatePresentation(
        call: RakuPsiElement,
        frameworkData: MutableMap<String, String>
    ): ItemPresentation {
        return object : ItemPresentation {
            override fun getPresentableText(): String {
                return frameworkData["method"]!!.uppercase() + " " + frameworkData["path"]
            }

            override fun getLocationString(): String? {
                return call.getEnclosingRakuModuleName()
            }

            override fun getIcon(unused: Boolean): Icon {
                return RakuIcons.CRO
            }
        }
    }

    override fun getStructureViewPresentation(
        call: RakuPsiElement,
        frameworkData: MutableMap<String, String>
    ): ItemPresentation {
        return object : ItemPresentation {
            override fun getPresentableText(): String {
                return frameworkData["method"]!!.uppercase() + " " + frameworkData["path"]
            }

            override fun getLocationString(): String? {
                return null
            }

            override fun getIcon(unused: Boolean): Icon {
                return RakuIcons.CRO
            }
        }
    }

    companion object {
        private val ROUTE_VERBS = HashSet<String?>()

        init {
            ROUTE_VERBS.addAll(mutableListOf<String?>("get", "put", "post", "delete", "patch"))
        }

        private fun getRouteSignature(call: RakuSubCall?): Array<RakuParameter>? {
            val pointyBlock = PsiTreeUtil.getChildOfType<RakuPointyBlock?>(call, RakuPointyBlock::class.java)
            if (pointyBlock != null) return pointyBlock.getParams()
            val routine = PsiTreeUtil.getChildOfType<RakuRoutineDecl?>(call, RakuRoutineDecl::class.java)
            if (routine != null) return routine.getParams()
            return null
        }

        private fun renderParameter(buffer: StringBuilder, param: RakuParameter) {
            // We'll only deal with positional parameters, which are part of the
            // route path.
            if (param.isPositional()) {
                buffer.append("/")
                val varName = param.getVariableName()
                val haveVarName = varName != null && !varName.isEmpty()
                if (param.isSlurpy()) {
                    buffer.append("{")
                    if (haveVarName) buffer.append(varName)
                    buffer.append("*}")
                } else if (haveVarName) {
                    buffer.append("{")
                    buffer.append(varName)
                    if (param.isOptional()) buffer.append("?")
                    buffer.append("}")
                } else {
                    val value = param.getValueConstraint()
                    if (value is RakuStrLiteral) {
                        buffer.append(value.getStringText())
                    } else {
                        buffer.append("<unknown>")
                    }
                }
            }
        }
    }
}
