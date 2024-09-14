use Cro::WebApp::Template;

route {
    template-location "lib/IdeaFoo";
    my $res = template "content.crotmp";
}

route {
    template-location "lib/IdeaFoo/templates2";
    my $res = template "inner-content2.crotmp";
}

route {
    template-location "lib/IdeaFoo/templates";
    my $res = template "inner-content.crotmp";
    my $res = template "inner-content2.crotmp";
    my $res = template "lib/IdeaFoo/templates/inner-content2.crotmp";
}