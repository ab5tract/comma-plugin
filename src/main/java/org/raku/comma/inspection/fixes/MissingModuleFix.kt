package org.raku.comma.inspection.fixes

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.openapi.project.Project
import org.raku.comma.metadata.RakuMetaDataComponent

class MissingModuleFix(private val moduleName: String) : LocalQuickFix {
    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {
        val file = descriptor.psiElement.containingFile

        // TODO: We want to move away from the RakuMetaDataComponent stuff to a Facet implementation
        val metadata = project.getService(RakuMetaDataComponent::class.java)
        metadata.addDepends(moduleName)
        DaemonCodeAnalyzer.getInstance(project).restart(file)
    }

    override fun getFamilyName(): String { return "Add module to META6.json" }
    override fun getName(): String { return "Add module %s to META6.json".format(moduleName) }
}