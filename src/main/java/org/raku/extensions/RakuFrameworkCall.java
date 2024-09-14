package org.raku.extensions;

import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.project.Project;
import com.intellij.psi.stubs.IndexSink;
import org.raku.psi.RakuPsiElement;
import org.raku.psi.RakuSubCall;
import org.raku.psi.stub.RakuSubCallStub;

import java.util.List;
import java.util.Map;

/** Implemented to provide support for DSL-style declarations provided by a
 * Raku framework. For example, the Cro::HTTP::Router uses a number of subs
 * (get, put, post, etc.) that declare routes. We'd like to index these.
 */
public abstract class RakuFrameworkCall {
    public static final ExtensionPointName<RakuFrameworkCall> EP_NAME = ExtensionPointName.create("org.raku.frameworkCall");

    /** A unique identifier for the framework in question. */
    public abstract String getFrameworkName();

    /** Check if the sub call is applicable to this framework. */
    public abstract boolean isApplicable(RakuSubCall call);

    /** Generate framework data to be associated with the call's
     * stub in the index. This can be used to stash data about the call
     * for display in indexes.*/
    public abstract Map<String, String> getFrameworkData(RakuSubCall call);

    /** Called for applicable calls when indexing, to allow for addition
     * to framework indexes. */
    public abstract void indexStub(RakuSubCallStub stub, Map<String, String> frameworkData, IndexSink sink);

    /** Called to contribute any additional symbol names for Go To Symbol. */
    public abstract void contributeSymbolNames(Project project, List<String> results);

    /** Called to contribute any additional navigation items for Go To Symbol. */
    public abstract void contributeSymbolItems(Project project, String pattern, List<NavigationItem> results);

    /** Get a presentation for the framework call in Go To Symbol context. */
    public abstract ItemPresentation getNavigatePresentation(RakuPsiElement call, Map<String, String> frameworkData);

    /** Get a presentation for the framework call in Structure View context. */
    public abstract ItemPresentation getStructureViewPresentation(RakuPsiElement call, Map<String, String> frameworkData);
}
