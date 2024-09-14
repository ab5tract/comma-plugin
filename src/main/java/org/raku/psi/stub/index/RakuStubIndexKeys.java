package org.raku.psi.stub.index;

import com.intellij.psi.stubs.StubIndexKey;
import org.raku.psi.*;

public class RakuStubIndexKeys {
    public static final StubIndexKey<String, RakuFile> PROJECT_MODULES
        = StubIndexKey.createIndexKey("raku.projectModules");
    public static final StubIndexKey<String, RakuIndexableType> GLOBAL_TYPES
        = StubIndexKey.createIndexKey("raku.globalTypes");
    public static final StubIndexKey<String, RakuIndexableType> LEXICAL_TYPES
        = StubIndexKey.createIndexKey("raku.lexicalTypes");
    public static final StubIndexKey<String, RakuConstant> ALL_CONSTANTS
        = StubIndexKey.createIndexKey("raku.allConstants");
    public static final StubIndexKey<String, RakuVariableDecl> ALL_ATTRIBUTES
        = StubIndexKey.createIndexKey("raku.allAttributes");
    public static final StubIndexKey<String, RakuRoutineDecl> ALL_ROUTINES
        = StubIndexKey.createIndexKey("raku.allRoutines");
    public static final StubIndexKey<String, RakuRegexDecl> ALL_REGEXES
        = StubIndexKey.createIndexKey("raku.allRegexes");
    public static final StubIndexKey<String, RakuVariableDecl> DYNAMIC_VARIABLES
        = StubIndexKey.createIndexKey("raku.dynamicVariables");
}
