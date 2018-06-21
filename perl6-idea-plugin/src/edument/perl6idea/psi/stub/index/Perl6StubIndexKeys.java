package edument.perl6idea.psi.stub.index;

import com.intellij.psi.stubs.StubIndexKey;
import edument.perl6idea.psi.*;

public class Perl6StubIndexKeys {
    public static final StubIndexKey<String, Perl6File> PROJECT_MODULES
        = StubIndexKey.createIndexKey("perl6.projectModules");
    public static final StubIndexKey<String, Perl6IndexableType> GLOBAL_TYPES
        = StubIndexKey.createIndexKey("perl6.globalTypes");
    public static final StubIndexKey<String, Perl6IndexableType> LEXICAL_TYPES
        = StubIndexKey.createIndexKey("perl6.lexicalTypes");
    public static final StubIndexKey<String, Perl6Constant> ALL_CONSTANTS
        = StubIndexKey.createIndexKey("perl6.allConstants");
    public static final StubIndexKey<String, Perl6VariableDecl> ALL_ATTRIBUTES
        = StubIndexKey.createIndexKey("perl6.allAttributes");
    public static final StubIndexKey<String, Perl6RoutineDecl> ALL_ROUTINES
        = StubIndexKey.createIndexKey("perl6.allRoutines");
    public static final StubIndexKey<String, Perl6RegexDecl> ALL_REGEXES
        = StubIndexKey.createIndexKey("perl6.allRegexes");
}
