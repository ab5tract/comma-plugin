my $counter;

for ^4 {
    start {
        for ^100 { $counter++ }
    }
}