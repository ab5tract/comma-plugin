package org.raku.comma.project.activity

import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.platform.ide.progress.withBackgroundProgress
import com.intellij.platform.util.progress.reportProgress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.raku.comma.services.application.RakuEcosystem
import org.raku.comma.services.project.*
import org.raku.comma.utils.CommaProjectUtil
import java.util.concurrent.CompletableFuture

class RakuServiceStarter : ProjectActivity {
    override suspend fun execute(project: Project) {
        CommaProjectUtil.refreshProjectState(project)
    }
}
