package edument.perl6idea.debugger;

import org.edument.moarvm.types.StackFrame;

public class Perl6StackFrameDescriptor {
    private Perl6LoadedFileDescriptor file;
    private String bytecodeFile;
    private int line;
    private String name;
    private String type;
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
        if (name.isEmpty()) {
            return String.format("%s:%s",
                                 !file.getPath().isEmpty() ? file.getPath() : bytecodeFile,
                                 line);
        } else {
            return String.format("%s:%s (%s) (%s)",
                                 name, line,
                                 !file.getPath().isEmpty() ? file.getPath() : bytecodeFile,
                                 type);
        }
    }

    public int getLine() {
        return line;
    }
}
