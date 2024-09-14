macro wait-condition($cond) is export {
    my $cond-attr = get-cond-attr($cond, 'wait-condition');
    quasi { $cond-attr.get_value($*MONITOR).wait() }
}