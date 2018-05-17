use Java::Generate::CompUnit;
use Java::Generate::Expression;
use Java::Generate::Interface;
use Java::Generate::Literal;
use Java::Generate::Variable;

sub generate-perl6-element-types(@element-names) is export {
    my @fields;
    push @fields, InterfaceField.new: :type<IStubFileElementType>, :name<FILE>, :default(
        ConstructorCall.new(:name<Perl6FileElementType>, :arguments()));

    my %custom := set 'PACKAGE_DECLARATION', 'ROUTINE_DECLARATION', 'SUBSET', 'ENUM',
        'CONSTANT', 'REGEX_DECLARATION', 'VARIABLE_DECLARATION', 'SCOPED_DECLARATION';
    push @fields, InterfaceField.new: :type<IStubElementType>, :name<PACKAGE_DECLARATION>,
        :default(ConstructorCall.new(:name<Perl6PackageDeclStubElementType>));
    push @fields, InterfaceField.new: :type<IStubElementType>, :name<ROUTINE_DECLARATION>,
        :default(ConstructorCall.new(:name<Perl6RoutineDeclStubElementType>));
    push @fields, InterfaceField.new: :type<IStubElementType>, :name<ENUM>,
        :default(ConstructorCall.new(:name<Perl6EnumStubElementType>));
    push @fields, InterfaceField.new: :type<IStubElementType>, :name<SUBSET>,
        :default(ConstructorCall.new(:name<Perl6SubsetStubElementType>));
    push @fields, InterfaceField.new: :type<IStubElementType>, :name<CONSTANT>,
        :default(ConstructorCall.new(:name<Perl6ConstantStubElementType>));
    push @fields, InterfaceField.new: :type<IStubElementType>, :name<REGEX_DECLARATION>,
        :default(ConstructorCall.new(:name<Perl6RegexDeclStubElementType>));
    push @fields, InterfaceField.new: :type<IStubElementType>, :name<VARIABLE_DECLARATION>,
        :default(ConstructorCall.new(:name<Perl6VariableDeclStubElementType>));
    push @fields, InterfaceField.new: :type<IStubElementType>, :name<SCOPED_DECLARATION>,
        :default(ConstructorCall.new(:name<Perl6ScopedDeclStubElementType>));

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
