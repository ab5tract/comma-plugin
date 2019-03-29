package edument.perl6idea.extensions;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.psi.stubs.IndexSink;
import edument.perl6idea.psi.Perl6SubCall;
import edument.perl6idea.psi.stub.Perl6SubCallStub;

import java.util.Map;

/** Implemented to provide support for DSL-style declarations provided by a
 * Perl 6 framework. For example, the Cro::HTTP::Router uses a number of subs
 * (get, put, post, etc.) that declare routes. We'd like to index these.
 */
public abstract class Perl6FrameworkCall {
    public static final ExtensionPointName<Perl6FrameworkCall> FRAMEWORK_CALL_EP =
            ExtensionPointName.create("edument.perl6idea.frameworkCall");

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
    public abstract void indexStub(Perl6SubCallStub stub, IndexSink sink);
}
