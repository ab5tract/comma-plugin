package org.raku.comma.inspection.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.raku.comma.inspection.InspectionConstants.UsedModuleInspection.DESCRIPTION_ECO_FORMAT
import org.raku.comma.inspection.InspectionConstants.UsedModuleInspection.DESCRIPTION_META6_FORMAT
import org.raku.comma.inspection.RakuInspection
import org.raku.comma.inspection.fixes.CreateLocalModuleFix
import org.raku.comma.inspection.fixes.MissingModuleFix
import org.raku.comma.services.project.RakuMetaDataComponent
import org.raku.comma.psi.RakuColonPair
import org.raku.comma.psi.RakuLongName
import org.raku.comma.psi.RakuModuleName
import org.raku.comma.services.project.RakuModuleListFetcher

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

        val service = holder.project.getService(RakuModuleListFetcher::class.java)
        // We don't need to annotate late-bound modules
        if (moduleName.startsWith("::")) return
        if (service.PREINSTALLED_MODULES.contains(moduleName)) return
        if (service.PRAGMAS.contains(moduleName)) return
        if (service.isNotReady) return

        val metadata = holder.project.getService(RakuMetaDataComponent::class.java)

        // No need to annotate "missing" modules, if there are
        // no META data available
        if (! metadata.isMetaDataExist) return

        // There is no need to
        if (element.reference?.resolve() != null) return



        val dependencies: MutableList<String> = ArrayList()
        dependencies.addAll(metadata.getDepends(true))
        dependencies.addAll(metadata.getTestDepends(true))
        dependencies.addAll(metadata.getBuildDepends(true))
;        for (dependency in dependencies) {
            var providesOfDependency = service.getProvidesListByModule(dependency)
            // Maybe it is a part of the distribution, and we can get something out of its parent distribution
            if (providesOfDependency.isEmpty()) {
                val parentModuleName = service.getModuleByProvide(dependency)
                if (parentModuleName != null) {
                    providesOfDependency = service.getProvidesListByModule(parentModuleName)
                }
            }
            // If a module is in dependencies list, do nothing
            if (providesOfDependency.contains(moduleName)) return
        }
        val holderPackage = service.getModuleByProvide(moduleName)
        if (holderPackage != null) {
            holder.registerProblem(element, DESCRIPTION_META6_FORMAT.format(moduleName), MissingModuleFix(moduleName))
        } else {
            holder.registerProblem(element, DESCRIPTION_ECO_FORMAT.format(moduleName), CreateLocalModuleFix(moduleName))
        }
    }
}