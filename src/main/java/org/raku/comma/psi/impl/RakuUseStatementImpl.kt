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
import org.raku.comma.services.project.RakuProjectSdkService

class RakuUseStatementImpl : StubBasedPsiElementBase<RakuUseStatementStub?>, RakuUseStatement {
    constructor(node: ASTNode) : super(node)

    constructor(stub: RakuUseStatementStub, type: RakuUseStatementStubElementType) : super(stub, type)

    override fun contributeLexicalSymbols(collector: RakuSymbolCollector) {
        // TODO: This is too slow to run on EDT. It's also not clear what parts of it actually work, if any.
        val name = moduleName
        if (name != null) {
            val project = project

            // We cannot contribute based on stubs when indexing is in progress
            if (isDumb(getProject())) return

            val index = ProjectModulesStubIndex.getInstance()
            val found = StubIndex.getElements(index.key,
                                              name,
                                              project,
                                              GlobalSearchScope.projectScope(project),
                                              RakuFile::class.java)
            if (!found.isEmpty()) {
                val file = found.iterator().next()
                file.contributeGlobals(collector, HashSet())
                val seen: MutableSet<String> = HashSet()
                seen.add(name)
                file.contributeGlobals(collector, seen)
            } else {
                val elements = StubIndex.getElements(RakuStubIndexKeys.PROJECT_MODULES,
                                                     name,
                                                     project,
                                                     GlobalSearchScope.allScope(project),
                                                     RakuFile::class.java)
                if (! elements.isEmpty()) {
                    elements.iterator().next().contributeGlobals(collector, HashSet())
                }

                if (collector.isSatisfied) return

                val file = project.service<RakuProjectSdkService>().symbolCache.getPsiFileForModule(name, text)
                file?.contributeGlobals(collector, HashSet())
            }
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
