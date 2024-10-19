package org.raku.comma.pm

import com.intellij.openapi.components.*
import com.intellij.openapi.project.Project
import org.raku.comma.pm.impl.RakuZefPM
import org.raku.comma.services.RakuServiceConstants
import java.nio.file.Paths
import java.util.*

@Service(Service.Level.PROJECT)
@State(name = "Raku.PackageManagers", storages = [Storage(value = RakuServiceConstants.PROJECT_SETTINGS_FILE)])
class RakuPackageManagerManager(private val project: Project) : PersistentStateComponent<PMState> {

    private var pmState = PMState()
    var currentPM: RakuPackageManager? = null
        get() {
            val location = pmState.location ?: return null
            return RakuZefPM(location, project)
        }
        private set


    override fun getState(): PMState {
        return refresh()
    }

    override fun loadState(state: PMState) {
        pmState = refresh(state)
    }

    private fun refresh(state: PMState = pmState): PMState {
        if (state.location.isNullOrEmpty()) {
            state.location = detectPMs().first()
            state.type = "zef"
        }
        return state
    }

    fun detectPMs() : List<String> {
        val execNames: MutableMap<RakuPackageManagerKind, Array<String>> = EnumMap(RakuPackageManagerKind::class.java)
        execNames[RakuPackageManagerKind.ZEF] = arrayOf("zef", "zef.exe", "zef.bat")

        val detected = mutableListOf<String>()
        for (kind in execNames.keys) {
            val strings = execNames[kind]!!
            for (name in strings) {
                val paths = System.getenv("PATH").split(":".toRegex())
                                                       .dropLastWhile { it.isEmpty() }
                for (path in paths) {
                    val pathWithName = Paths.get(path, name)
                    val file = pathWithName.toFile()
                    if (file.exists() && file.canExecute()) {
                        detected.add(pathWithName.toString())
                    }
                }
            }
        }
        return detected
    }
}

class PMState : BaseState() {
    var location by string()
    var type by string()
}