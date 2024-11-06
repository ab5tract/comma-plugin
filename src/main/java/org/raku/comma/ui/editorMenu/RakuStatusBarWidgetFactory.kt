package org.raku.comma.ui.editorMenu

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.StatusBarWidgetFactory
import kotlinx.coroutines.CoroutineScope
import org.raku.comma.services.project.RakuProjectDetailsService

class RakuStatusBarWidgetFactory : StatusBarWidgetFactory {
    private val ID: String = "RakuStatusBarWidgetFactory"

    override fun getId(): String {
        return ID
    }

    override fun isAvailable(project: Project): Boolean {
        return project.service<RakuProjectDetailsService>().doesProjectContainRakuCode
    }

    override fun createWidget(project: Project, scope: CoroutineScope): StatusBarWidget {
        return RakuStatusBarWidget(project, scope)
    }

    override fun getDisplayName(): String {
        return "Raku Settings"
    }
}