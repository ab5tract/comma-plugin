package edument.perl6idea.extensions;

import com.intellij.navigation.ItemPresentation;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.project.Project;
import com.intellij.psi.stubs.IndexSink;
import edument.perl6idea.psi.Perl6PsiElement;
import edument.perl6idea.psi.Perl6SubCall;
import edument.perl6idea.psi.impl.Perl6SubCallImpl;
import edument.perl6idea.psi.stub.Perl6SubCallStub;

import java.util.List;
import java.util.Map;

/** Implemented to provide support for DSL-style declarations provided by a
 * Perl 6 framework. For example, the Cro::HTTP::Router uses a number of subs
 * (get, put, post, etc.) that declare routes. We'd like to index these.
 */
public abstract class Perl6FrameworkCall {
    public static final ExtensionPointName<Perl6FrameworkCall> EP_NAME = ExtensionPointName.create("edument.perl6idea.frameworkCall");

    /** A unique identifier for the framework in question. */
    public abstract String getFrameworkName();

    /** Check if the sub call is applicable to this framework. */
    public abstract boolean isApplicable(Perl6SubCall call);

    /** Generate framework data to be associated with the call's
     * stub in the index. This can be used to stash data about the call
     * for display in indexes.*/
    public abstract Map<String, String> getFrameworkData(Perl6SubCall call);

    /** Called for applicable calls when indexing, to allow for addition
     * to framework indexes. */
    public abstract void indexStub(Perl6SubCallStub stub, Map<String, String> frameworkData, IndexSink sink);

    /** Called to contribute any additional symbol names for Go To Symbol. */
    public abstract void contributeSymbolNames(Project project, List<String> results);

    /** Called to contribute any additional navigation items for Go To Symbol. */
    public abstract void contributeSymbolItems(Project project, String pattern, List<NavigationItem> results);

    /** Get a presentation for the framework call in Go To Symbol context. */
    public abstract ItemPresentation getNavigatePresentation(Perl6PsiElement call, Map<String, String> frameworkData);

    /** Get a presentation for the framework call in Structure View context. */
    public abstract ItemPresentation getStructureViewPresentation(Perl6PsiElement call, Map<String, String> frameworkData);
}
