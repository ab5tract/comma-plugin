use IdeaFoo2::Base;

class User does Base {
    method test {
        $!private++;
        $!pri<caret>vate--;
        $!private++;
        $!private--;

        $.visible;
        $!visible;
        say self.visible;
    }
}
