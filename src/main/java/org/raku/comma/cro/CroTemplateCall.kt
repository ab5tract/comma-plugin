package org.raku.comma.cro

import com.intellij.navigation.ItemPresentation
import com.intellij.navigation.NavigationItem
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.NlsSafe
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.IndexSink
import com.intellij.psi.stubs.StubIndex
import org.raku.comma.RakuIcons
import org.raku.comma.contribution.Filtering
import org.raku.comma.extensions.RakuFrameworkCall
import org.raku.comma.psi.RakuPsiElement
import org.raku.comma.psi.RakuStrLiteral
import org.raku.comma.psi.RakuSubCall
import org.raku.comma.psi.stub.RakuSubCallStub
import java.util.function.Consumer
import javax.swing.Icon

class CroTemplateCall : RakuFrameworkCall() {
    override val frameworkName: String = "Cro Templates"

    override fun isApplicable(call: RakuSubCall): Boolean {
        return call.getCallName() == "template-part" && call.getCallArguments().size > 0 &&
                call.getCallArguments()[0] is RakuStrLiteral
    }

    override fun getFrameworkData(call: RakuSubCall): MutableMap<String, String> {
        val result: MutableMap<String, String> = mutableMapOf()
        result.put("name", (call.getCallArguments()[0] as RakuStrLiteral).getStringText())
        return result
    }

    override fun indexStub(
        stub: RakuSubCallStub,
        frameworkData: MutableMap<String, String>,
        sink: IndexSink
    ) {
        sink.occurrence<RakuSubCall, String>(CroIndexKeys.CRO_TEMPLATE, frameworkData["name"]!!)
    }

    override fun contributeSymbolNames(project: Project, results: MutableList<String>) {
        results.addAll(CroTemplateIndex.getInstance().getAllKeys(project))
    }

    override fun contributeSymbolItems(
        project: Project,
        pattern: String,
        results: MutableList<NavigationItem>
    ) {
        val routeIndex = CroTemplateIndex.getInstance()
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
            override fun getPresentableText(): @NlsSafe String {
                return frameworkData["name"]!!
            }

            override fun getLocationString(): @NlsSafe String? {
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
                return frameworkData["name"]!!
            }

            override fun getIcon(unused: Boolean): Icon {
                return RakuIcons.CRO
            }
        }
    }
}
