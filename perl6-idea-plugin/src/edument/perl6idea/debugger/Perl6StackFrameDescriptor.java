package edument.perl6idea.debugger;

import org.edument.moarvm.types.StackFrame;

public class Perl6StackFrameDescriptor {
    private final Perl6LoadedFileDescriptor file;
    private final String bytecodeFile;
    private final int line;
    private final String name;
    private final String type;
    private Perl6ValueDescriptor[] lexicals;

    Perl6StackFrameDescriptor(Perl6LoadedFileDescriptor descriptor, StackFrame frame) {
        file = descriptor;
        bytecodeFile = frame.getBytecode_file();
        line = frame.getLine();
        name = frame.getName();
        type = frame.getMethod();
    }

    public Perl6LoadedFileDescriptor getFile() {
        return file;
    }

    public Perl6ValueDescriptor[] getLexicals() {
        return lexicals;
    }

    public void setLexicals(Perl6ValueDescriptor[] lexicals) {
        this.lexicals = lexicals;
    }

    public String getPresentableName() {
        String base = String.format(
            "%s:%s (%s)", name, line,
            !file.getPath().isEmpty() ? file.getPath() : bytecodeFile);
        return type.isEmpty() ? base : String.format("%s (%s)", base, type);
    }

    public int getLine() {
        return line;
    }
}
