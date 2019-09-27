.say;

#| Base role for magicians
role Magician {
  has Int $.level;
  has Str @.spells;
}
#= Trailing one

Magician<caret>;