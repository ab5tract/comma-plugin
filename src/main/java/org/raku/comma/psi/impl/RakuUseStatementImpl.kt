package org.raku.comma.psi.impl

import com.intellij.extapi.psi.StubBasedPsiElementBase
import com.intellij.lang.ASTNode
import com.intellij.openapi.components.service
import com.intellij.openapi.project.DumbService.Companion.isDumb
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import org.raku.comma.psi.RakuFile
import org.raku.comma.psi.RakuModuleName
import org.raku.comma.psi.RakuUseStatement
import org.raku.comma.psi.stub.RakuUseStatementStub
import org.raku.comma.psi.stub.RakuUseStatementStubElementType
import org.raku.comma.psi.stub.index.ProjectModulesStubIndex
import org.raku.comma.psi.stub.index.RakuStubIndexKeys
import org.raku.comma.psi.symbols.RakuSymbolCollector
import org.raku.comma.services.project.RakuDependencyDetailsService
import org.raku.comma.services.project.RakuProjectSdkService
import org.raku.comma.utils.RakuUtils

class RakuUseStatementImpl : StubBasedPsiElementBase<RakuUseStatementStub?>, RakuUseStatement {
    constructor(node: ASTNode) : super(node)

    constructor(stub: RakuUseStatementStub, type: RakuUseStatementStubElementType) : super(stub, type)

    override fun contributeLexicalSymbols(collector: RakuSymbolCollector) {
        // This function has been changed so that it no longer uses the StubIndex at all and instead grabs the RakuFile
        // from the dependency service.

        // TODO: Figure out what to do about multiple versions of a dependency. This feels like something that Raku
        // only theoretically supports. Let's deal with it when we encounter it as a problem.
        if (moduleName != null) {
            // We cannot contribute based on stubs when indexing is in progress
            if (isDumb(project)) return

            val shortName = RakuUtils.stripAuthVerApi(moduleName)
            val file = project.service<RakuDependencyDetailsService>().provideToRakuFile(shortName) as? RakuFile
                            ?: return

            file.contributeGlobals(collector, HashSet())
            file.contributeGlobals(collector, mutableSetOf(shortName, moduleName))
        }
    }

    override fun getModuleName(): String? {
        val stub = stub
        if (stub != null) return stub.moduleName

        val moduleName = findChildByClass(RakuModuleName::class.java)
        return moduleName?.text
    }

    override fun toString(): String {
        return javaClass.simpleName + "(Raku:USE_STATEMENT)"
    }
}
