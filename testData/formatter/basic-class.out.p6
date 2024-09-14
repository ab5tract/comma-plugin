class Human {
    has $.name;
    has $.age;
    method introduce-yourself {
        say 'Hi I am a human being, my name is ' ~ self.name;
    }
}

class Employee is Human {
    has $.company;
    has $.salary;
}