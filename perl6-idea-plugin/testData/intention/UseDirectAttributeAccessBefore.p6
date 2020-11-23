class C {
    has $.attr;
    submethod m() {
        $.at<caret>tr
    }
}