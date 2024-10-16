package org.raku.comma.psi

import com.intellij.openapi.components.service
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import com.intellij.util.IncorrectOperationException
import org.raku.comma.psi.stub.index.ProjectModulesStubIndex
import org.raku.comma.psi.stub.index.RakuStubIndexKeys
import org.raku.comma.services.RakuModuleDetailsService
import org.raku.comma.services.RakuServiceConstants
import java.util.function.Consumer

class RakuModuleReference(moduleName: RakuModuleName) :
    PsiReferenceBase<RakuModuleName?>(moduleName, TextRange(0, moduleName.textLength)) {
    private val project = moduleName.project
    private val psiManager = PsiManager.getInstance(project)

    override fun resolve(): PsiElement? {
        val keys = StubIndex.getElements(
            RakuStubIndexKeys.PROJECT_MODULES,
            value,
            project,
            GlobalSearchScope.projectScope(project),
            RakuFile::class.java
        )
        val rakuFile = if (keys.isNotEmpty()) keys.first() else return resolveExternal()

        return psiManager.findFile(rakuFile.virtualFile)
    }

    private fun resolveExternal(): PsiFile? {
        return project.service<RakuModuleDetailsService>().provideToPsiFile(value)
    }

    override fun getVariants(): Array<Any> {
        val project = this.element.project
        val projectModules = ProjectModulesStubIndex.getInstance().getAllKeys(project)
        val reallyInThisProject: MutableList<String> = ArrayList()
        projectModules.forEach(Consumer { module: String ->
            val index = ProjectModulesStubIndex.getInstance()
            val matching = StubIndex.getElements(
                index.key,
                module,
                project,
                GlobalSearchScope.projectScope(project),
                RakuFile::class.java
            )
            if (!matching.isEmpty()) {
                reallyInThisProject.add(module)
            }
        })

        reallyInThisProject.addAll(project.service<RakuModuleDetailsService>().allProvides())
        reallyInThisProject.addAll(RakuServiceConstants.PREINSTALLED_MODULES)
        reallyInThisProject.addAll(RakuServiceConstants.PRAGMAS)

        return reallyInThisProject.toTypedArray()
    }

    @Throws(IncorrectOperationException::class)
    override fun handleElementRename(newElementName: String): PsiElement {
        val name = element
        return name.setName(newElementName)
    }

    @Throws(IncorrectOperationException::class)
    override fun bindToElement(element: PsiElement): PsiElement {
        // Our RakuFile, so can calculate new path
        if (element is RakuPsiElement) {
            getElement().setName(element.enclosingRakuModuleName)
        }
        return element
    }
}
