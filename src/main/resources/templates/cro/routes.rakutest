use Cro::HTTP::Test;
use Test;
use $$MODULE_NAME$$::Routes;

test-service routes, {
    test get('/'),
            status => 200,
            body-text => '<h1> $$MODULE_NAME$$ </h1>';
}

done-testing;