# Perl 6 Color Scheme Principles

## The basic idea

* Brackets, braces, parentheses, etc. are neutrel color
* All kinds of keyword, even if they can be customized, default to the same
  color, which is unused for anything else
* Variables have a distinct color
* Names of things that are callable, both usage and declaration wise, have a
  distinct color
* Types and terms have a distinct color
* Operators have a distinct color, including regex things that feel quite
  operator-like
* Comments have a distinct color
* Literals have a distinct color
* Literal escapes have a distinct color
* Numeric literals have a distinct color
* Sigspace has a differnet background color
* Bad escapes have a different background color

This gives us these colors for the elements:

* Neutrel (ENSURE NEUTRAL IN DRACULA)
    * Argument capture
    * Array composer
    * Array indexer
    * Block clurly brackets
    * Hash indexer
    * Lambda
    * Named parameter colon and parentheses
    * Only Star (protos)
    * Parameter separator
    * Parentheses
    * Regex assertion angle brackets
    * Regex character class syntax
    * Regex Group
    * Return type arrow
    * Statement terminaotr
    * Term declaration backslash
    * Type coercion parentheses
* Keyword
    * Multi keyword
    * Package keyword
    * Parameter or variable constraint
    * Phaser
    * Routine keyword
    * Scope keyword
    * Statement control
    * Statement modifier
    * Statement prefix
    * Trait keyword
    * Type declarator
* Variable (SET IN COLOR SCHEMES)
    * Current object
    * Named parameter name alias
    * Regex capture
    * Variable
    * Variable shape declaration
* Callable (SET IN COLOR SCHEMES)
    * Method call name
    * Routine name
    * Sub call name
* Types and terms (SET IN COLOR SCHEMES)
    * Other term
    * Type name
    * Type parameter brackets
    * Whatever
* Operator (SET IN COLOR SCHEMES)
    * Contextualizer
    * Infix operator
    * Metaoperator
    * Parameter quantifier
    * Postix operator
    * Prefix operator
    * Regex anchor
    * Regex modifier
    * Regex infix
    * Regex lookaround
    * Regex quantifier
    * Transliteration range syntax
* Comment
    * Comment
    * Stub code
* String Literal
    * Pair (colon pair or key before =>)
    * Quote modifer
    * Quote pair
    * Regex literal quote
    * String literal quote
    * String literal value
    * Transliteration literal character
* Numeric literal
    * Numeric literal
    * Version literal
* String literal escape
    * Regex built-in character class
    * Regex invalid backslash (plus background)
    * String literal escape
    * String literal invalid escape (plus background)
    * Transliteration escape
    * Transliteration invalid syntax (plus background)
