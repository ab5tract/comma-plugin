package edument.perl6idea.psi.stub.impl;

import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import edument.perl6idea.parsing.Perl6ElementTypes;
import edument.perl6idea.psi.Perl6SubCall;
import edument.perl6idea.psi.stub.Perl6SubCallStub;

import java.util.Map;

public class Perl6SubCallStubImpl extends StubBase<Perl6SubCall> implements Perl6SubCallStub {
    private final String name;
    private final Map<String, String> frameworkData;

    public Perl6SubCallStubImpl(StubElement parent, String name, Map<String, String> frameworkData) {
        super(parent, Perl6ElementTypes.SUB_CALL);
        this.name = name;
        this.frameworkData = frameworkData;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Map<String, String> getAllFrameworkData() {
        return frameworkData;
    }

    @Override
    public String getFrameworkData(String frameworkName, String key) {
        return frameworkData.get(frameworkName + "." + key);
    }
}
