package edument.perl6idea.psi.stub;

import com.intellij.psi.stubs.StubElement;
import edument.perl6idea.psi.Perl6SubCall;

import java.util.Map;

public interface Perl6SubCallStub extends StubElement<Perl6SubCall> {
    String getName();
    Map<String, String> getAllFrameworkData();
    String getFrameworkData(String frameworkName, String key);
}
