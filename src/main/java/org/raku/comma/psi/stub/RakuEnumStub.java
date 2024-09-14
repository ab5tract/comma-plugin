package org.raku.comma.psi.stub;

import org.raku.comma.psi.RakuEnum;

import java.util.Collection;

public interface RakuEnumStub extends RakuTypeStub<RakuEnum> {
    Collection<String> getEnumValues();
}
