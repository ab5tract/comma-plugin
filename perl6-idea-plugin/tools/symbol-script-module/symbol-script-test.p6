use Test;

plan *;

constant $MODULE-SCRIPT = '../../resources/symbols/perl6-module-symbols.p6';

subtest {
    my $proc = run $*EXECUTABLE, $MODULE-SCRIPT, 'use', 'NativeCall', :out;
    my $output = $proc.out.slurp(:close);
    # Subs, constants, types
    like $output, /'V:&explicitly-manage'/, 'Subroutine is exported';
    # Roles
    like $output, /'R:Native'/, 'Native role is exported';
    like $output, /'R:Native' .+? '!setup'/, 'Native role private methods are exported';
    like $output, /'R:Native' .+? 'CALL-ME'/, 'Native role methods are exported';
    like $output, /'R:Native' .+? '$!arity'/, 'Native role attributes are exported';
    # Classes
    like $output, /'C:CArray'/, 'CArray class is exported';
    like $output, /'C:CArray' .+? 'AT-POS'/, 'CArray class methods are exported';
    # Exports
    subtest {
        like $output, /'C:Pointer'/;
        like $output, /'C:OpaquePointer'/;
        like $output, /'C:NativeCall::Types::Pointer'/;
        like $output, /'NativeCall::EXPORT::types::Pointer'/;
        like $output, /'NativeCall::EXPORT::DEFAULT::Pointer'/;
        like $output, /'NativeCall::EXPORT::ALL::Pointer'/;
    }, 'Various symbols to access Pointer are exported';
}, 'Module script test';

subtest {
    my $proc = run $*EXECUTABLE, $MODULE-SCRIPT, 'use', 'SymbolScriptModule', :out;
    my $output = $proc.out.slurp(:close);
    like $output, /'V:$test-variable'/, 'Module variable is exported';
}

done-testing;
