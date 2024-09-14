use Cro::HTTP::Log::File;
use Cro::HTTP::Server;
use $$MODULE_NAME$$::Routes;

my Cro::Service $http = Cro::HTTP::Server.new(
    http => <1.1>,
    host => %*ENV<$$HOST_VARIABLE$$> ||
        die("Missing $$HOST_VARIABLE$$ in environment"),
    port => %*ENV<$$PORT_VARIABLE$$> ||
        die("Missing $$PORT_VARIABLE$$ in environment"),
    application => routes(),
    after => [
        Cro::HTTP::Log::File.new(logs => $*OUT, errors => $*ERR)
    ]
);
$http.start;
say "Listening at http://%*ENV<$$HOST_VARIABLE$$>:%*ENV<$$PORT_VARIABLE$$>";
react {
    whenever signal(SIGINT) {
        say "Shutting down...";
        $http.stop;
        done;
    }
}