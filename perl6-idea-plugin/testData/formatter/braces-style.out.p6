say 1 +
    41;

class Foo
{
    method foo
    {
        if True
        { say "One-liner" }
        else
        {}

        for ^42
        {
            LAST
            { say "LAST" }
        }
    }

    method answer
    {
        -> {}
    }
    method empty
    {}

    class Empty
    {}
}

grammar Bar
{
    token a
    {}
    token long
    {
        \w \w \w \w
    }
}