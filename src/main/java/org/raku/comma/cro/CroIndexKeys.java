package org.raku.comma.cro;

import com.intellij.psi.stubs.StubIndexKey;
import org.raku.comma.psi.RakuSubCall;

public class CroIndexKeys {
    public static final StubIndexKey<String, RakuSubCall> CRO_ROUTES
            = StubIndexKey.createIndexKey("raku.cro.routes");
    public static final StubIndexKey<String, RakuSubCall> CRO_TEMPLATE
            = StubIndexKey.createIndexKey("raku.cro.template");
}
