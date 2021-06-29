use Cro::HTTP::Router;

#|( Opening.
    Second sentence)
class QuickDocsLongPre {
    has Int $.attr; #= Attribute

    #| foo docs 1
    method foo(
            #| documented int param 1
            $i<caret>nt, #= documented int param 2
            #| documented named foo 1
            :$foo #= documented named foo 2
               ) {
        $int + $foo;
               }
    #= foo docs 2
}
#=( Ending.
Third sentence )

QuickDocsLongPre;
