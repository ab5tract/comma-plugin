package edument.perl6idea.cro;

import com.intellij.psi.stubs.IndexSink;
import edument.perl6idea.extensions.Perl6FrameworkCall;
import edument.perl6idea.psi.Perl6SubCall;
import edument.perl6idea.psi.stub.Perl6SubCallStub;

import java.util.Map;

public class CroFrameworkCall extends Perl6FrameworkCall {
    @Override
    public String getFrameworkName() {
        return "Cro Router";
    }

    @Override
    public boolean isApplicable(Perl6SubCall call) {
        return false;
    }

    @Override
    public Map<String, String> getFrameworkData(Perl6SubCall call) {
        return null;
    }

    @Override
    public void indexStub(Perl6SubCallStub stub, IndexSink sink) {

    }
}
