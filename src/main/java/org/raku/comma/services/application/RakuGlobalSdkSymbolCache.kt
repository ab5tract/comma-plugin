package org.raku.comma.services.application

import com.intellij.openapi.components.Service
import com.intellij.util.containers.ContainerUtil
import java.util.concurrent.ConcurrentHashMap

@Service(Service.Level.APP)
class RakuGlobalSdkSymbolCache {
    val useNameSymbolCache: ConcurrentHashMap<String, String> = ConcurrentHashMap()
    val needNameSymbolCache: ConcurrentHashMap<String, String> = ConcurrentHashMap()

    val myNeedPackagesStarted: MutableSet<String> = ContainerUtil.newConcurrentSet()
    val myUsePackagesStarted: MutableSet<String> = ContainerUtil.newConcurrentSet()
}