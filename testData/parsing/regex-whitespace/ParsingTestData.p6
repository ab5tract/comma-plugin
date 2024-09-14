for @files -> $file {
    my @lines;
    my @links = $file.IO.lines.grep( * ~~ / https?\: /)
            .grep( * !~~ /\#\s+OUTPUT/);       # eliminates output lines
    my @links-not-links;
}