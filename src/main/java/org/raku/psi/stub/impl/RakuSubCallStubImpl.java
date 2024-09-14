package org.raku.psi.stub.impl;

import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import org.raku.parsing.RakuElementTypes;
import org.raku.psi.RakuSubCall;
import org.raku.psi.stub.RakuSubCallStub;

import java.util.Map;

public class RakuSubCallStubImpl extends StubBase<RakuSubCall> implements RakuSubCallStub {
    private final String name;
    private final Map<String, String> frameworkData;

    public RakuSubCallStubImpl(StubElement parent, String name, Map<String, String> frameworkData) {
        super(parent, RakuElementTypes.SUB_CALL);
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
