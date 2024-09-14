package org.raku.comma.sdk;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.extensions.InternalIgnoreDependencyViolation;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import org.jetbrains.annotations.NotNull;

@Service
@InternalIgnoreDependencyViolation
public final class RakuSdkCacheManager implements ProjectManagerListener {
    @Override
    public void projectClosed(@NotNull Project project) {
        RakuSdkType.getInstance().invalidateFileCaches(project);
    }
}
