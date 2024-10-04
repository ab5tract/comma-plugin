package org.raku.comma.sdk.chooser

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.StatusBarWidgetFactory
import org.raku.comma.utils.CommaProjectUtil

//class RakuSdkStatusBarWidgetFactory : StatusBarWidgetFactory {
//    override fun getId(): String {
//        return "RAKU_SDK_STATUS_BAR_WIDGET"
//    }
//
//    override fun getDisplayName(): String {
//        return "Raku SDK"
//    }
//
//    override fun isAvailable(project: Project): Boolean {
//        return CommaProjectUtil.projectHasMetaFile(project)
//    }
//
//    override fun createWidget(project: Project): StatusBarWidget {
//        return RakuSdkStatusBarWidget()
//    }
//}