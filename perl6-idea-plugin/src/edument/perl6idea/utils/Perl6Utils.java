package edument.perl6idea.utils;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.io.FileUtil;
import edument.perl6idea.module.Perl6ModuleBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Perl6Utils {
    public static final Logger LOG = Logger.getInstance(Perl6Utils.class);

    public static void writeCodeToPath(Path codePath, List<String> lines) {
        ApplicationManager.getApplication().runWriteAction(() -> {
            try {
                if (!codePath.getParent().toFile().exists())
                    Files.createDirectories(codePath.getParent());
                Files.write(codePath, lines, StandardCharsets.UTF_8);
            } catch (IOException e) {
                Logger.getInstance(Perl6ModuleBuilder.class).error(e);
            }
        });
    }

    public static File getResourceAsFile(Object object, String resourcePath) {
        InputStream in = object.getClass().getClassLoader().getResourceAsStream(resourcePath);
        FileOutputStream out = null;
        try {
            if (in == null) return null;
            File tempFile = FileUtil.createTempFile(String.valueOf(in.hashCode()), ".tmp");
            tempFile.deleteOnExit();
            out = new FileOutputStream(tempFile);
            //copy stream
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1)
                out.write(buffer, 0, bytesRead);
            return tempFile;
        } catch (IOException e) {
            LOG.error(e);
            return null;
        } finally {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
            } catch (IOException e) {
                LOG.error(e);
            }
        }
    }
}
