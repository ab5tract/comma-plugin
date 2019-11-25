package edument.perl6idea.vfs;

import com.intellij.openapi.vfs.VirtualFileWithId;
import com.intellij.openapi.vfs.ex.dummy.DummyFileIdGenerator;
import com.intellij.testFramework.LightVirtualFile;

public class Perl6LightVirtualFile extends LightVirtualFile implements VirtualFileWithId {
    private final int myId = DummyFileIdGenerator.next();

    public Perl6LightVirtualFile(String name, String contents) {
        super(name, contents);
    }

    @Override
    public int getId() {
        return myId;
    }
}
