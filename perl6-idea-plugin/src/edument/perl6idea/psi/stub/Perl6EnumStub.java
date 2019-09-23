package edument.perl6idea.psi.stub;

import edument.perl6idea.psi.Perl6Enum;

import java.util.Collection;

public interface Perl6EnumStub extends Perl6TypeStub<Perl6Enum> {
    Collection<String> getEnumValues();
}
