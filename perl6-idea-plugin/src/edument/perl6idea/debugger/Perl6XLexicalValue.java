package edument.perl6idea.debugger;

public class Perl6XLexicalValue extends Perl6XNamedValue {
    private final Perl6StackFrame myStackFrame;

    public Perl6XLexicalValue(Perl6ValueDescriptor descriptor, Perl6StackFrame stackFrame) {
        super(descriptor);
        this.myStackFrame = stackFrame;
    }

    @Override
    protected Perl6DebugThread getDebugThread() {
        return myStackFrame.getDebugThread();
    }
}
