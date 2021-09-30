package edument.perl6idea.utils;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.io.FileUtil;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Perl6Utils {
    public static final Logger LOG = Logger.getInstance(Perl6Utils.class);

    public static void writeCodeToPath(Path codePath, List<String> lines) {
        ApplicationManager.getApplication().runWriteAction(() -> {
            try {
                if (!codePath.getParent().toFile().exists())
                    Files.createDirectories(codePath.getParent());
                Files.write(codePath, lines, StandardCharsets.UTF_8);
            } catch (IOException e) {
                LOG.error(e);
            }
        });
    }

    @Nullable
    public static File getResourceAsFile(String resourcePath) {
        File tempFile;
        try {
            tempFile = FileUtil.createTempFile("comma", ".tmp");
        }
        catch (IOException e) {
            LOG.error(e);
            return null;
        }

        try (
            InputStream in = Perl6Utils.class.getClassLoader().getResourceAsStream(resourcePath);
            FileOutputStream out = new FileOutputStream(tempFile)
        ) {
            if (in != null)
                in.transferTo(out);
        } catch (IOException e) {
            LOG.error(e);
        }
        return tempFile;
    }

    public static List<String> getResourceAsLines(String filepath) {
        List<String> lines = new ArrayList<>();
        try (
            InputStream resourceFileStream = Perl6Utils.class.getClassLoader().getResourceAsStream(filepath);
            BufferedReader inputStreamReader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(resourceFileStream), StandardCharsets.UTF_8))
        ) {
            while (inputStreamReader.ready())
                lines.add(inputStreamReader.readLine());
        }
        catch (IOException|NullPointerException e) {
            LOG.error(e);
        }
        return lines;
    }

    public static String getResourceAsString(String filepath) {
        return String.join("\n", getResourceAsLines(filepath));
    }

    public static String formatDelimiters(int originalText, String delimiter, int each) {
        String text = new StringBuilder(String.valueOf(originalText)).reverse().toString();
        StringBuilder builder = new StringBuilder(text.length() + text.length() / each + 1);
        int index = 0;
        while (index < text.length())  {
            builder.append(text, index, Math.min(index + 3, text.length()));
            index += each;

            if (index < text.length())
                builder.append(delimiter);
        }
        return builder.reverse().toString();
    }

    public static String getNameExtension(@Nullable String filename) {
        return filename != null && filename.contains(".") ? filename.substring(filename.lastIndexOf(".") + 1) : "";
    }
}
