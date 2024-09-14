role R0 { method base {...} }; role R does R0 { method foo($a) {...}; method bar($a) {...} };
class C<caret> does R {
}