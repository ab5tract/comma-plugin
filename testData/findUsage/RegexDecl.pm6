grammar One-Two {
    token TOP { <rule-a> <rule-b> }
    token ru<caret>le-a { <?> }
    regex rule-b { <rule-a> }
}