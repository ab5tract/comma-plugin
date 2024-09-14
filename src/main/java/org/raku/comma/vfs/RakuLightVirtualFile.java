package org.raku.comma.vfs;

import com.intellij.openapi.vfs.VirtualFileWithId;
import com.intellij.testFramework.LightVirtualFile;

import java.util.concurrent.atomic.AtomicInteger;

public class RakuLightVirtualFile extends LightVirtualFile implements VirtualFileWithId {
    private static final AtomicInteger ourId = new AtomicInteger(Integer.MAX_VALUE / 2);
    private final int myId = ourId.getAndIncrement();

    public RakuLightVirtualFile(String name, String contents) {
        super(name, contents);
    }

    @Override
    public int getId() {
        return myId;
    }
}
