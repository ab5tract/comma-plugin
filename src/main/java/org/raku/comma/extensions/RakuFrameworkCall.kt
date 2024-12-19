package org.raku.comma.extensions

import com.intellij.navigation.ItemPresentation
import com.intellij.navigation.NavigationItem
import com.intellij.openapi.extensions.ExtensionPointName
import com.intellij.openapi.project.Project
import com.intellij.psi.stubs.IndexSink
import org.raku.comma.psi.RakuPsiElement
import org.raku.comma.psi.RakuSubCall
import org.raku.comma.psi.stub.RakuSubCallStub

/** Implemented to provide support for DSL-style declarations provided by a
 * Raku framework. For example, the Cro::HTTP::Router uses a number of subs
 * (get, put, post, etc.) that declare routes. We'd like to index these.
 */
abstract class RakuFrameworkCall {
    /** A unique identifier for the framework in question.  */
    abstract val frameworkName: String

    /** Check if the sub call is applicable to this framework.  */
    abstract fun isApplicable(call: RakuSubCall): Boolean

    /** Generate framework data to be associated with the call's
     * stub in the index. This can be used to stash data about the call
     * for display in indexes. */
    abstract fun getFrameworkData(call: RakuSubCall): MutableMap<String, String>

    /** Called for applicable calls when indexing, to allow for addition
     * to framework indexes.  */
    abstract fun indexStub(stub: RakuSubCallStub, frameworkData: MutableMap<String, String>, sink: IndexSink)

    /** Called to contribute any additional symbol names for Go To Symbol.  */
    abstract fun contributeSymbolNames(project: Project, results: MutableList<String>)

    /** Called to contribute any additional navigation items for Go To Symbol.  */
    abstract fun contributeSymbolItems(project: Project, pattern: String, results: MutableList<NavigationItem>)

    /** Get a presentation for the framework call in Go To Symbol context.  */
    abstract fun getNavigatePresentation(
        call: RakuPsiElement,
        frameworkData: MutableMap<String, String>
    ): ItemPresentation

    /** Get a presentation for the framework call in Structure View context.  */
    abstract fun getStructureViewPresentation(
        call: RakuPsiElement,
        frameworkData: MutableMap<String, String>
    ): ItemPresentation

    companion object {
        @JvmField
        val EP_NAME: ExtensionPointName<RakuFrameworkCall> =
            ExtensionPointName.create<RakuFrameworkCall>("org.raku.comma.frameworkCall")
    }
}
