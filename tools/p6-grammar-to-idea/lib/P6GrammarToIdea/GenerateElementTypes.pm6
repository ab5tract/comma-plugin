use Java::Generate::CompUnit;
use Java::Generate::Expression;
use Java::Generate::Interface;
use Java::Generate::Literal;
use Java::Generate::Variable;

sub generate-perl6-element-types(@element-names) is export {
    my @fields;
    push @fields, InterfaceField.new: :type<IStubFileElementType>, :name<FILE>, :default(
        ConstructorCall.new(:name<Perl6FileElementType>, :arguments()));

    my %custom := set 'PACKAGE_DECLARATION';
    push @fields, InterfaceField.new: :type<IStubElementType>, :name<PACKAGE_DECLARATION>,
        :default(ConstructorCall.new(:name<Perl6PackageDeclStubElementType>));

    for @element-names.sort -> $name {
        next if %custom{$name};
        push @fields, InterfaceField.new: :type<IElementType>, :$name, :default(
            ConstructorCall.new(:name<Perl6ElementType>, :arguments(
                StringLiteral.new(:value($name)))));
    }
    my $interface = Interface.new: :access<public>, :name<Perl6ElementTypes>, :@fields;
    my $comp-unit = CompUnit.new:
        package => 'edument.perl6idea.parsing',
        imports => <
            com.intellij.psi.tree.IElementType
            com.intellij.psi.tree.IStubFileElementType
            com.intellij.psi.stubs.IStubElementType
            edument.perl6idea.psi.stub.*
        >,
        type => $interface;
    return $comp-unit.generate;
}
