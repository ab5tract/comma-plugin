sub find-references(:$pod!, :$url, :$origin) {
        my $index-name-attr is default(Failure.new('missing index link'));
        # this comes from Pod::To::HTML and needs to be moved into a method in said module
}