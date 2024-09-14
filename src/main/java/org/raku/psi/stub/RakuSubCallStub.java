package org.raku.psi.stub;

import com.intellij.psi.stubs.StubElement;
import org.raku.psi.RakuSubCall;

import java.util.Map;

public interface RakuSubCallStub extends StubElement<RakuSubCall> {
    String getName();
    Map<String, String> getAllFrameworkData();
    String getFrameworkData(String frameworkName, String key);
}
