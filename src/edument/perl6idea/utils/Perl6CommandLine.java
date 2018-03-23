package edument.perl6idea.utils;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.vfs.CharsetToolkit;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;

public class Perl6CommandLine {
    private static Logger LOG = Logger.getInstance(Perl6CommandLine.class);

    @NotNull
    public static GeneralCommandLine getPerl6CommandLine(String workingPath, String homePath, File script) throws ExecutionException {
        GeneralCommandLine commandLine = new GeneralCommandLine()
                .withWorkDirectory(workingPath)
                .withExePath(Paths.get(homePath, "perl6").toString())
                .withCharset(CharsetToolkit.UTF8_CHARSET);
        try {
            commandLine.addParameter(script.getCanonicalPath());
            commandLine.addParameter("-Ilib");
        } catch (IOException e) {
            LOG.error(e);
            throw new ExecutionException("Could not create execution script");
        }
        return commandLine;
    }

    public static File getResourceAsFile(Object object, String resourcePath) {
        try {
            InputStream in = object.getClass().getClassLoader().getResourceAsStream(resourcePath);
            if (in == null)
                return null;

            File tempFile = File.createTempFile(String.valueOf(in.hashCode()), ".tmp");
            tempFile.deleteOnExit();

            try (FileOutputStream out = new FileOutputStream(tempFile)) {
                //copy stream
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
            return tempFile;
        } catch (IOException e) {
            LOG.error(e);
            return null;
        }
    }
}
