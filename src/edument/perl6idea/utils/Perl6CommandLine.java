package edument.perl6idea.utils;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.vfs.CharsetToolkit;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Perl6CommandLine {
    private static Logger LOG = Logger.getInstance(Perl6CommandLine.class);

    @NotNull
    public static GeneralCommandLine getPerl6CommandLine(String workingPath, String homePath) {
        return new GeneralCommandLine()
                .withWorkDirectory(workingPath)
                .withExePath(Paths.get(homePath, "perl6").toString())
                .withCharset(CharsetToolkit.UTF8_CHARSET);
    }

    public static GeneralCommandLine pushFile(GeneralCommandLine cmd, File script) throws ExecutionException {
        try {
            // We pass -Ilib after script path, because it is the script argument
            cmd.addParameter(script.getCanonicalPath());
            cmd.addParameter("-Ilib");
            return cmd;
        } catch (IOException e) {
            LOG.error(e);
            throw new ExecutionException("Could not get execution script");
        }
    }

    public static GeneralCommandLine pushLine(GeneralCommandLine cmd, String line) {
        cmd.addParameter("-e");
        cmd.addParameter(line);
        return cmd;
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

    public static List<String> execute(GeneralCommandLine cmd) {
        List<String> results = new ArrayList<>();
        AtomicBoolean died = new AtomicBoolean(false);
        try {
            Process p = cmd.createProcess();
            final Thread read = new Thread(() -> {
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String result;
                    while ((result = reader.readLine()) != null)
                        results.add(result);
                    reader.close();
                } catch (IOException e) {
                    died.set(true);
                    LOG.error(e);
                }
            });
            read.start();
            p.waitFor();
            if (died.get()) return null;
        } catch (InterruptedException | ExecutionException e) {
            LOG.error(e);
            return null;
        }
        return results;
    }
}
