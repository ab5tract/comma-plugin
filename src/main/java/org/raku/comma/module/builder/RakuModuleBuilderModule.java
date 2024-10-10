package org.raku.comma.module.builder;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.raku.comma.filetypes.RakuModuleFileType;
import org.raku.comma.filetypes.RakuTestFileType;
import org.raku.comma.language.RakuLanguageVersion;
import org.raku.comma.metadata.RakuMetaDataComponent;
import org.raku.comma.utils.RakuUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class RakuModuleBuilderModule implements RakuModuleBuilderGeneric {
    private static final Logger LOG = Logger.getInstance(RakuModuleBuilderModule.class);
    private String myModuleName;

    @Override
    public void setupRootModelOfPath(@NotNull ModifiableRootModel model,
                                     Path path,
                                     RakuLanguageVersion languageVersion)
    {
        VirtualFile sourceRoot = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(path.toFile());
        Path directoryName = path.getFileName();
        if (Objects.equals(directoryName.toString(), "lib")) {
            stubModule(model.getProject(),
                       path,
                       myModuleName,
                       true,
                       false,
                       sourceRoot == null
                           ? null
                           : sourceRoot.getParent(),
                       "Empty",
                       false,
                       languageVersion);
        } else if (Objects.equals(directoryName.toString(), "t")) {
            stubTest(path,
                     "00-sanity.rakutest",
                     Collections.singletonList(myModuleName),
                     languageVersion);
        }
    }

    @Override
    public String[] getSourceDirectories() {
        return new String[]{"lib", "t"};
    }

    public static String stubModule(Project project,
                                    Path moduleLibraryPath,
                                    String moduleName,
                                    boolean firstModule,
                                    boolean shouldOpenEditor,
                                    VirtualFile root,
                                    String moduleType,
                                    boolean isUnitScoped,
                                    RakuLanguageVersion languageVersion)
    {
        // TODO: We do eventually want more than one module per project. But for now, that's a lot to think about
        var metadata = project.getService(RakuMetaDataComponent.class);

        if (firstModule) {
            try {
                metadata.createStubMetaFile(moduleName, root, shouldOpenEditor);
            } catch (IOException e) {
                LOG.warn(e);
            }
        }
        if (moduleLibraryPath.endsWith("lib")) {
            ApplicationManager.getApplication()
                              .invokeLater(() -> metadata.addNamespaceToProvides(moduleName, RakuModuleFileType.INSTANCE.getDefaultExtension()));
        }
        String modulePath = Paths.get(moduleLibraryPath.toString(), moduleName.split("::")) + "." + RakuModuleFileType.INSTANCE.getDefaultExtension();
        List<String> code = new ArrayList<>(getModuleCodeByType(moduleType, moduleName, isUnitScoped));
        if (languageVersion != null) {
            code.addFirst(String.format("use v%s;\n", languageVersion));
        }
        RakuUtils.writeCodeToPath(Paths.get(modulePath), code);
        if (moduleType.equals("Monitor")) {
            metadata.addDepends("OO::Monitors");
        }
        if (moduleType.equals("Model")) {
            metadata.addDepends("Red");
        }

        return modulePath;
    }

    private static List<String> getModuleCodeByType(String type,
                                                    String name,
                                                    boolean isUnitScoped)
    {
        if (isUnitScoped) {
            String declText = String.format("unit %s %s;\n", type.toLowerCase(Locale.ENGLISH), name);
            return switch (type) {
                case "Class", "Role", "Grammar", "Module", "Package" -> Arrays.asList(declText, "");
                case "Monitor" -> Arrays.asList("use OO::Monitors;", "", declText, "");
                case "Model" -> Arrays.asList("use Red;", "", declText, "");
                default -> Collections.singletonList("");
            };
        } else {
            String declText = String.format("%s %s {", type.toLowerCase(Locale.ENGLISH), name);
            return switch (type) {
                case "Class", "Role", "Grammar", "Module", "Package" -> Arrays.asList(declText, "", "}");
                case "Monitor" -> Arrays.asList("use OO::Monitors;", "", declText, "", "}");
                case "Model" -> Arrays.asList("use Red;", "", declText, "", "}");
                default -> Collections.singletonList("");
            };
        }
    }

    public static String stubTest(Path testDirectoryPath,
                                  String fileName,
                                  List<String> imports,
                                  RakuLanguageVersion languageVersion)
    {
        Path testPath = testDirectoryPath.resolve(fileName);
        // If no extension, add default `.t`
        if (!fileName.contains(".")) {
            testPath = Paths.get(testDirectoryPath.toString(), fileName + "." + RakuTestFileType.INSTANCE.getDefaultExtension());
        }
        List<String> lines = new LinkedList<>();
        lines.add(String.format("use v%s;", languageVersion));
        imports.forEach(i -> lines.add(String.format("use %s;", i)));
        lines.addAll(Arrays.asList("use Test;", "", "done-testing;"));
        RakuUtils.writeCodeToPath(testPath, lines);
        return testPath.toString();
    }

    @Override
    public void setName(@NotNull String name) {
        myModuleName = name;
    }
}
