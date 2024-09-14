package org.raku.psi.stub;

import org.raku.psi.RakuEnum;

import java.util.Collection;

public interface RakuEnumStub extends RakuTypeStub<RakuEnum> {
    Collection<String> getEnumValues();
}
