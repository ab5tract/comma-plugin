throws-like { for [:a] X [:b] -> ($i, $j) { } },
        Exception,
        message => / '<anon>' /,
        "anonymous subs get '<anon>' in arity error messages";