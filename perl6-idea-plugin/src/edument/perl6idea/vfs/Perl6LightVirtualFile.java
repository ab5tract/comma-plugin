package edument.perl6idea.vfs;

import com.intellij.openapi.vfs.VirtualFileWithId;
import com.intellij.testFramework.LightVirtualFile;

import java.util.concurrent.atomic.AtomicInteger;

public class Perl6LightVirtualFile extends LightVirtualFile implements VirtualFileWithId {
    private static final AtomicInteger ourId = new AtomicInteger(Integer.MAX_VALUE / 2);
    private final int myId = ourId.getAndIncrement();

    public Perl6LightVirtualFile(String name, String contents) {
        super(name, contents);
    }

    @Override
    public int getId() {
        return myId;
    }
}
