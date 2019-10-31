sub foo(#| an important parameter
        :$parameter = 42, #= documented nicely
        *%foo) {
  say $parame<caret>ter;
}