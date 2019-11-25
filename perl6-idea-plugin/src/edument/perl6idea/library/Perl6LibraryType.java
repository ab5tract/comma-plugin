package edument.perl6idea.library;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.DummyLibraryProperties;
import com.intellij.openapi.roots.libraries.LibraryType;
import com.intellij.openapi.roots.libraries.NewLibraryConfiguration;
import com.intellij.openapi.roots.libraries.PersistentLibraryKind;
import com.intellij.openapi.roots.libraries.ui.LibraryEditorComponent;
import com.intellij.openapi.roots.libraries.ui.LibraryPropertiesEditor;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class Perl6LibraryType extends LibraryType<DummyLibraryProperties> {
    public static final PersistentLibraryKind<DummyLibraryProperties> LIBRARY_KIND = new PersistentLibraryKind<DummyLibraryProperties>("perl6") {
        @NotNull
        @Override
        public DummyLibraryProperties createDefaultProperties() {
            return DummyLibraryProperties.INSTANCE;
        }
    };

    protected Perl6LibraryType() {
        super(LIBRARY_KIND);
    }

    @Nullable
    @Override
    public String getCreateActionName() {
        return null;
    }

    @Nullable
    @Override
    public NewLibraryConfiguration createNewLibrary(@NotNull JComponent parentComponent,
                                                    @Nullable VirtualFile contextDirectory,
                                                    @NotNull Project project) {
        return null;
    }

    @Nullable
    @Override
    public LibraryPropertiesEditor createPropertiesEditor(@NotNull LibraryEditorComponent editorComponent) {
        return null;
    }

    @NotNull
    @Override
    public OrderRootType[] getExternalRootTypes() {
        return new OrderRootType[]{OrderRootType.SOURCES};
    }
}
