package edument.perl6idea.psi.stub.index;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import edument.perl6idea.psi.Perl6RoutineDecl;
import org.jetbrains.annotations.NotNull;

public class Perl6AllRoutinesStubIndex extends StringStubIndexExtension<Perl6RoutineDecl> {
    private static final int INDEX_VERSION = 4;
    private static final Perl6AllRoutinesStubIndex instance = new Perl6AllRoutinesStubIndex();

    public static Perl6AllRoutinesStubIndex getInstance() {
        return instance;
    }

    @Override
    public int getVersion() {
        return INDEX_VERSION;
    }

    @NotNull
    @Override
    public StubIndexKey<String, Perl6RoutineDecl> getKey() {
        return Perl6StubIndexKeys.ALL_ROUTINES;
    }
}
