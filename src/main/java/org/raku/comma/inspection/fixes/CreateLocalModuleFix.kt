package org.raku.comma.inspection.fixes

import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.progress.runBlockingCancellable
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.ide.progress.runWithModalProgressBlocking
import com.intellij.util.IncorrectOperationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.raku.comma.actions.NewModuleDialog
import org.raku.comma.language.RakuLanguageVersionService
import org.raku.comma.metadata.RakuMetaDataComponent
import org.raku.comma.module.builder.RakuModuleBuilderModule
import org.raku.comma.psi.RakuModuleName
import java.nio.file.Paths

class CreateLocalModuleFix(private val maybeName: String) : LocalQuickFix {
    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val moduleName = descriptor.psiElement as RakuModuleName

        var moduleLibraryRoot: VirtualFile? = null
        var moduleLibraryPath: String? = null

        val module =  ModuleUtilCore.findModuleForPsiElement(moduleName)
            ?: throw IncorrectOperationException("Unable to find associated module for file '${moduleName.containingFile.name}'")
        val modifiableModel = ModuleRootManager.getInstance(module).modifiableModel
        for (root in modifiableModel.sourceRoots) {
            if (root.name == "lib") {
                moduleLibraryRoot = root
                moduleLibraryPath = root.canonicalPath
            }
        }
        modifiableModel.dispose()
        if (moduleLibraryPath == null) throw IncorrectOperationException()

        val dialog = NewModuleDialog(project, moduleLibraryPath, moduleName.text)
        val isOk = dialog.showAndGet()
        if (! isOk) return
        val langVersionService = project.getService(
            RakuLanguageVersionService::class.java
        )
        val newModulePath = RakuModuleBuilderModule.stubModule(
            project,
            Paths.get(moduleLibraryPath),
            dialog.moduleName, false, true,
            moduleLibraryRoot!!.parent, dialog.moduleType, false, langVersionService.version
        )

        val moduleFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(Paths.get(newModulePath).toFile())

        // If the user changed stubbed module name in form,
        // we update its usage too
        if (dialog.moduleName != moduleName.text) {
            ApplicationManager.getApplication().runWriteAction {
                moduleName.setName(dialog.moduleName)
            }
        }

        if (moduleFile != null) {
            val fileDescriptor = OpenFileDescriptor(project, moduleFile)
            fileDescriptor.navigate(true)
        }
    }

    override fun getName(): String { return "Stub a local module '%s'".format(maybeName) }
    override fun getFamilyName(): String { return "Stub a local module" }

    // So that we can open the dialog window
    override fun startInWriteAction(): Boolean { return false }
}
