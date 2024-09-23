package org.raku.comma.inspection.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.raku.comma.inspection.InspectionConstants.UsedModuleInspection.DESCRIPTION_ECO_FORMAT
import org.raku.comma.inspection.InspectionConstants.UsedModuleInspection.DESCRIPTION_META6_FORMAT
import org.raku.comma.inspection.RakuInspection
import org.raku.comma.inspection.fixes.CreateLocalModuleFix
import org.raku.comma.inspection.fixes.MissingModuleFix
import org.raku.comma.metadata.RakuMetaDataComponent
import org.raku.comma.psi.RakuColonPair
import org.raku.comma.psi.RakuLongName
import org.raku.comma.psi.RakuModuleName
import org.raku.comma.utils.RakuModuleListFetcher

class UsedModuleInspection : RakuInspection() {
    override fun provideVisitFunction(holder: ProblemsHolder, element: PsiElement) {

        if (element !is RakuModuleName) return

        val moduleNameNode = PsiTreeUtil.findChildOfType(element, RakuLongName::class.java) ?: return
        val moduleName = moduleNameNode.firstChild.text

        val params = PsiTreeUtil.findChildrenOfType(moduleNameNode, RakuColonPair::class.java)
        for (colonPair in params) {
            val key = colonPair.key
            if (key == "from") return
        }


        // We don't need to annotate late-bound modules
        if (moduleName.startsWith("::")) return
        if (RakuModuleListFetcher.PREINSTALLED_MODULES.contains(moduleName)) return
        if (RakuModuleListFetcher.PRAGMAS.contains(moduleName)) return

        val module = ModuleUtilCore.findModuleForPsiElement(element) ?: return
        val metaData = module.getService(RakuMetaDataComponent::class.java)


        // No need to annotate "missing" modules, if there are
        // no META data available
        if (!metaData.isMetaDataExist) return

        // There is no need to
        if (element.reference?.resolve() != null) return

        val project = element.getProject()

        if (!RakuModuleListFetcher.isReady()) {
            RakuModuleListFetcher.populateModules(project)
            return
        }

        val dependencies: MutableList<String> = ArrayList()
        dependencies.addAll(metaData.getDepends(true))
        dependencies.addAll(metaData.getTestDepends(true))
        dependencies.addAll(metaData.getBuildDepends(true))
        for (dependency in dependencies) {
            var providesOfDependency = RakuModuleListFetcher.getProvidesByModule(project, dependency, HashSet())
            // Maybe it is a part of the distribution, and we can get something out its parent distribution
            if (providesOfDependency.isEmpty()) {
                val parentModuleName = RakuModuleListFetcher.getModuleByProvide(project, dependency)
                if (parentModuleName != null) {
                    providesOfDependency = RakuModuleListFetcher.getProvidesByModule(project,
                                                                                     parentModuleName,
                                                                                     HashSet())
                }
            }
            // If a module is in dependencies list, do nothing
            if (providesOfDependency.contains(moduleName)) return
        }
        val holderPackage = RakuModuleListFetcher.getModuleByProvide(project, moduleName)
        if (holderPackage != null) {
            holder.registerProblem(element, DESCRIPTION_META6_FORMAT.format(moduleName), MissingModuleFix(moduleName))
        } else {
            holder.registerProblem(element, DESCRIPTION_ECO_FORMAT.format(moduleName), CreateLocalModuleFix(moduleName))
        }
    }
}