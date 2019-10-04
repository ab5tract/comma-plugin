.say;

#| Base class for magicians
class Magician is Cool does Int {
  has Int $.level;
  has Str @.spells;
}
#= Trailing one

Magician<caret>;