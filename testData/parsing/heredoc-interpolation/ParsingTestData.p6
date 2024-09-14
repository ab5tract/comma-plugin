#!/usr/bin/env perl6
sub bold($a) { }

say qq:to/CMDS/;
    &bold("assume no thread")
        Resets the thread selection.
    &bold("abbrev") [abbrev-key]
        Display the full output for any abbreviated field.
    &bold("debug") [on|off]
        Turns debugging information on or off, or display whether it's on or off.
    &bold("color") [on|off]
        Turn ANSI Colors on or of or display whether it's on or off.
    CMDS
