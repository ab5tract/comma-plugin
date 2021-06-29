use Cro::HTTP::Router;

#|( Opening.
    Second sentence)
class QuickDocsLongPre {
    has Int $.attr; #= Attribute

    #| foo docs 1
    method foo(
            #| documented int param 1
            $int, #= documented int param 2
            #| documented named foo 1
            :$f<caret>oo #= documented named foo 2
            #= documented named foo 3
               ) {
        $int + $foo;
               }
    #= foo docs 2
}
#=( Ending.
Third sentence )

QuickDocsLongPre;
