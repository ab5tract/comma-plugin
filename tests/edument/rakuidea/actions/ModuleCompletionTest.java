package edument.rakuidea.actions;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.PsiManagerImpl;
import com.intellij.psi.impl.file.PsiDirectoryImpl;
import edument.rakuidea.CommaFixtureTestCase;

import java.io.IOException;
import java.nio.file.Paths;

public class ModuleCompletionTest extends CommaFixtureTestCase {

    public void testNewModuleProperties() {
        PsiManagerImpl psiManager = (PsiManagerImpl)PsiManager.getInstance(getProject());
        ContentEntry[] entries = ModuleRootManager.getInstance(getModule()).getContentEntries();
        assert entries.length == 1;
        VirtualFile baseDir = entries[0].getFile();
        NewModuleAction newModuleAction = new NewModuleAction();

        // Test project dir case
        PsiDirectoryImpl psiDirectory = new PsiDirectoryImpl(psiManager, baseDir);
        assertEmpty(newModuleAction.processNavigatable(myFixture.getModule(), psiDirectory));
        assertEquals(newModuleAction.getBaseDir(), baseDir.getPath());

        // Test 'lib' case
        newModuleAction.setBaseDir(Paths.get(baseDir.getPath(), "lib").toString());
        VirtualFile lib = getOrCreateDirectory(baseDir, "lib");
        psiDirectory = new PsiDirectoryImpl(psiManager, lib);
        assertEmpty(newModuleAction.processNavigatable(myFixture.getModule(), psiDirectory));
        assertEquals(newModuleAction.getBaseDir(), lib.getPath());

        // Test 'lib/Foo' case
        newModuleAction.setBaseDir(lib.getPath());
        VirtualFile foo = getOrCreateDirectory(lib, "Foo");
        PsiDirectory insideLib = new PsiDirectoryImpl(psiManager, foo);
        assertEquals("Foo::", newModuleAction.processNavigatable(myFixture.getModule(), insideLib));
        assertEquals(newModuleAction.getBaseDir(), lib.getPath());

        // Test 't' case
        String baseDir1 = Paths.get(baseDir.getPath(), "t").toString();
        newModuleAction.setBaseDir(baseDir1);
        VirtualFile t = getOrCreateDirectory(baseDir, "t");
        PsiDirectory insideTests = new PsiDirectoryImpl(psiManager, t);
        assertEmpty(newModuleAction.processNavigatable(myFixture.getModule(), insideTests));
        assertEquals(newModuleAction.getBaseDir(), baseDir1);
    }

    private VirtualFile getOrCreateDirectory(VirtualFile base, String name) {
        if (base.findFileByRelativePath(name) == null)
            ApplicationManager.getApplication().runWriteAction(() -> {
                try {
                    base.createChildDirectory(this, name);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            });
        return base.findFileByRelativePath(name);
    }
}
