package org.raku.comma.inspection.fixes

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.raku.comma.services.project.RakuDependencyService
import org.raku.comma.services.project.RakuModuleInstallPrompt
import org.raku.comma.services.project.RakuProjectDetailsService
import org.raku.comma.utils.CommaProjectUtil

class InstallMissingDependencyFix(private val moduleName: String) : LocalQuickFix {

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val installer = project.service<RakuModuleInstallPrompt>()

        installer.runScope.launch {
            withContext(Dispatchers.Default) {
                installer.install(moduleName).join()
                CommaProjectUtil.refreshProjectState(project)
            }
        }
    }



    override fun getName(): String { return "Install missing dependency: $moduleName" }
    override fun getFamilyName(): String { return "Install missing dependency" }
}