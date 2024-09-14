grammar G {
    token kok { <.end_keyword> <?before \s || \# || $ > <.ws> }
}