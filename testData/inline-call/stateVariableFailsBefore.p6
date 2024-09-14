sub a {
    state @x;
    state $l = 'A';
    @x.push($l++);
};

say <caret>a for 1..6;