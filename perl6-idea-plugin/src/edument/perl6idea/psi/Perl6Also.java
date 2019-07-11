// Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package edument.perl6idea.psi;

import edument.perl6idea.psi.symbols.Perl6MOPSymbolContributor;

public interface Perl6Also extends Perl6MOPSymbolContributor {
    Perl6Trait getTrait();
}
