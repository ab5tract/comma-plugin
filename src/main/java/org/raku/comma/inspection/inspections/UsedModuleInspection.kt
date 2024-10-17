package org.raku.comma.inspection.inspections

import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.raku.comma.inspection.InspectionConstants.UsedModuleInspection.DESCRIPTION_ECO_FORMAT
import org.raku.comma.inspection.InspectionConstants.UsedModuleInspection.DESCRIPTION_META6_FORMAT
import org.raku.comma.inspection.RakuInspection
import org.raku.comma.inspection.fixes.CreateLocalModuleFix
import org.raku.comma.inspection.fixes.MissingModuleFix
import org.raku.comma.psi.RakuColonPair
import org.raku.comma.psi.RakuLongName
import org.raku.comma.psi.RakuModuleName
import org.raku.comma.services.RakuModuleDetailsService
import org.raku.comma.services.RakuServiceConstants
import org.raku.comma.utils.CommaProjectUtil

class UsedModuleInspection : RakuInspection() {
    override fun provideVisitFunction(holder: ProblemsHolder, element: PsiElement) {
        if (element !is RakuModuleName) return

        val project = holder.project
        val moduleNameNode = PsiTreeUtil.findChildOfType(element, RakuLongName::class.java) ?: return
        val moduleName = moduleNameNode.firstChild.text

        val params = PsiTreeUtil.findChildrenOfType(moduleNameNode, RakuColonPair::class.java)
        for (colonPair in params) {
            val key = colonPair.key
            if (key == "from") return
        }

        // We don't need to annotate late-bound modules
        if (moduleName.startsWith("::")) return
        // ... or preinstalled ones ...
        if (RakuServiceConstants.PREINSTALLED_MODULES.contains(moduleName)) return
        // ... or pragmas ...
        if (RakuServiceConstants.PRAGMAS.contains(moduleName)) return
        if (checkDependency(moduleName, project, element, holder.file.virtualFile)) return

        val holderPackage = project.service<RakuModuleDetailsService>().moduleByProvide(moduleName)
        if (holderPackage != null) {
            holder.registerProblem(element, DESCRIPTION_META6_FORMAT.format(moduleName), MissingModuleFix(moduleName))
        } else {
            holder.registerProblem(element, DESCRIPTION_ECO_FORMAT.format(moduleName), CreateLocalModuleFix(moduleName))
        }
    }

    private fun checkDependency(
        moduleName: String,
        project: Project,
        element: PsiElement,
        file: VirtualFile
    ): Boolean {
        val service = project.service<RakuModuleDetailsService>()
        return CommaProjectUtil.projectHasMetaFile(project)
                && service.dependencyInMeta(moduleName)
                || (element.reference?.resolve() != null || file.url.startsWith("mock://"))
    }
}