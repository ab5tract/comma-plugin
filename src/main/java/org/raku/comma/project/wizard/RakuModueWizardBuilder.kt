package org.raku.comma.project.wizard

import com.intellij.ide.util.projectWizard.*
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.WriteAction
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.module.ModifiableModuleModel
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ContentEntry
import com.intellij.openapi.roots.ModifiableRootModel
import com.intellij.openapi.roots.ui.configuration.ModulesProvider
import com.intellij.openapi.util.Pair
import com.intellij.openapi.vfs.LocalFileSystem
import org.raku.comma.language.RakuLanguageVersion
import org.raku.comma.language.RakuLanguageVersionService
import org.raku.comma.services.project.RakuMetaDataComponent
import org.raku.comma.module.RakuModuleType
import org.raku.comma.module.builder.*
import org.raku.comma.project.wizard.steps.RakuModuleNameStep
import org.raku.comma.project.wizard.steps.RakuProjectTypeStep
import org.raku.comma.services.project.RakuProjectSdkService
import org.raku.comma.utils.RakuProjectType
import java.lang.reflect.InvocationTargetException
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import javax.swing.BorderFactory
import javax.swing.border.Border
import javax.swing.border.EtchedBorder.LOWERED


class RakuModuleWizardBuilder : ModuleBuilder() {
    companion object {
        @JvmStatic
        private val LOG: Logger = Logger.getInstance(RakuModuleWizardBuilder::class.java)
    }

    var rakuProjectType = RakuProjectType.RAKU_SCRIPT
    var builder: RakuModuleBuilderGeneric = RakuModuleBuilderScript()

    var sdkPath: String? = null
    var moduleName: String? = null

    val safeModuleName: String?
        get() = moduleName?.replace("::", "-")
    var projectPath: String? = null

    var projectName: String? = null

    override fun getModuleType(): ModuleType<*> {
        return RakuModuleType.getInstance()
    }

    override fun setupRootModel(model: ModifiableRootModel) {
        updateBuilder()
        val contentEntry = doAddContentEntry(model) ?: return
        updateBuilder()
        val sourcePaths: List<Pair<String, String>> = builder.getSourcePaths(contentEntryPath)
        for (sourcePathPair in sourcePaths) {
            val sourcePath: Path = addSourceRoot(contentEntry, sourcePathPair)
            val project = model.project
            var prefix: RakuLanguageVersion?
            val langVersionService = project.getService(RakuLanguageVersionService::class.java)
            prefix = if (langVersionService.isExplicit) langVersionService.version else null
            builder.setupRootModelOfPath(model, sourcePath, prefix)
        }
    }

    override fun createWizardSteps(
        wizardContext: WizardContext,
        modulesProvider: ModulesProvider
    ): Array<ModuleWizardStep> {
        return arrayOf(
            RakuModuleNameStep(this)
        )
    }

    override fun commit(
        project: Project,
        model: ModifiableModuleModel?,
        modulesProvider: ModulesProvider?
    ): List<Module>? {
        val modules = super.commit(project, model, modulesProvider)
        if (model != null) {
            WriteAction.run<RuntimeException> { model.commit() }
        }

        if (! modules.isNullOrEmpty()) {
            LOG.warn("Having more than one module is NOT SUPPORTED")
        }

        project.service<RakuProjectSdkService>().sdkPath = sdkPath
        project.service<RakuMetaDataComponent>().triggerMetaBuild(refreshDependencies = true)

        return modules
    }

    override fun getCustomOptionsStep(context: WizardContext, parentDisposable: Disposable): ModuleWizardStep {
        return RakuProjectTypeStep(this)
    }

    override fun modifySettingsStep(settingsStep: SettingsStep): ModuleWizardStep? {
        val nameField: ModuleNameLocationSettings = settingsStep.moduleNameLocationSettings ?: return null
        nameField.moduleName = safeModuleName ?: return null
        return super.modifySettingsStep(settingsStep)
    }

    private fun addSourceRoot(contentEntry: ContentEntry, sourcePathPair: Pair<String, String>): Path {
        val sourcePath = Paths.get(sourcePathPair.first, sourcePathPair.second)
        val directory = sourcePath.toFile()
        check(directory.exists() || directory.mkdirs()) { "Could not find or create directory: $directory" }
        if (builder.shouldBeMarkedAsRoot(sourcePathPair.second)) {
            val sourceRoot = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(sourcePath.toFile())
            if (sourceRoot != null) {
                contentEntry.addSourceFolder(sourceRoot, sourcePathPair.second == "t", sourcePathPair.second)
            }
        }
        return sourcePath
    }

    private fun updateBuilder() {
        val typeToBuilderPairs: MutableMap<RakuProjectType, Class<*>> = EnumMap(org.raku.comma.utils.RakuProjectType::class.java)
        typeToBuilderPairs[RakuProjectType.RAKU_SCRIPT] = RakuModuleBuilderScript::class.java
        typeToBuilderPairs[RakuProjectType.RAKU_MODULE] = RakuModuleBuilderModule::class.java
        typeToBuilderPairs[RakuProjectType.RAKU_APPLICATION] = RakuModuleBuilderApplication::class.java
//        typeToBuilderPairs[RakuProjectType.CRO_WEB_APPLICATION] = CroModuleBuilderApplication::class.java
        val currentTypeBuilder = typeToBuilderPairs[rakuProjectType]!!

        if (currentTypeBuilder.isInstance(builder)) return

        try {
            builder = currentTypeBuilder.getConstructor().newInstance() as RakuModuleBuilderGeneric
        } catch (e: NoSuchMethodException) {
            LOG.error("Could not update builder", e)
        } catch (e: InstantiationException) {
            LOG.error("Could not update builder", e)
        } catch (e: IllegalAccessException) {
            LOG.error("Could not update builder", e)
        } catch (e: InvocationTargetException) {
            LOG.error("Could not update builder", e)
        }
    }

    fun updateLocalBuilder(formData: Map<String?, String?>?) {
        updateBuilder()
        builder.loadFromDialogData(formData)
    }

    fun makeBorder(text: String): Border {
        return BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(LOWERED), text)
    }

    fun ensureEntryPoint(value: String) {
        projectPath = value
        builder.setEntryPoint(value)
    }

    fun ensureModuleName(value: String) {
        moduleName = value
        builder.setName(value)
    }

    fun ensureProjectType(type: RakuProjectType) {
        rakuProjectType = type
        updateBuilder()
    }
}