package org.raku.comma.debugger;

import org.edument.moarvm.types.StackFrame;

public class RakuStackFrameDescriptor {
    private final RakuLoadedFileDescriptor file;
    private final String bytecodeFile;
    private final int line;
    private final String name;
    private final String type;
    private RakuValueDescriptor[] lexicals;

    RakuStackFrameDescriptor(RakuLoadedFileDescriptor descriptor, StackFrame frame) {
        file = descriptor;
        bytecodeFile = frame.getBytecode_file();
        line = frame.getLine();
        name = frame.getName();
        type = frame.getMethod();
    }

    public RakuLoadedFileDescriptor getFile() {
        return file;
    }

    public RakuValueDescriptor[] getLexicals() {
        return lexicals;
    }

    public void setLexicals(RakuValueDescriptor[] lexicals) {
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
