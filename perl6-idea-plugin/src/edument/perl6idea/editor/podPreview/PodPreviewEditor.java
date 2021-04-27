package edument.perl6idea.editor.podPreview;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.VirtualFileSystem;
import com.intellij.ui.jcef.JCEFHtmlPanel;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.*;
import org.cef.misc.BoolRef;
import org.cef.network.CefRequest;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.util.Locale;

public class PodPreviewEditor extends UserDataHolderBase implements FileEditor {
    private static final String FILE_SCHEME = "file://";

    private final JCEFHtmlPanel htmlPanel = new JCEFHtmlPanel(null);
    private final Project project;
    private final VirtualFile previewOf;
    private String latestContent = "";

    public PodPreviewEditor(Project project, VirtualFile previewOf) {
        this.project = project;
        this.previewOf = previewOf;
        htmlPanel.getJBCefClient().addRequestHandler(new CefRequestHandlerAdapter() {
            @Override
            public CefResourceRequestHandler getResourceRequestHandler(CefBrowser browser,
                                                                       CefFrame frame,
                                                                       CefRequest request,
                                                                       boolean isNavigation,
                                                                       boolean isDownload,
                                                                       String requestInitiator,
                                                                       BoolRef disableDefaultHandling) {
                String url = request.getURL();
                if (url != null) {
                    // For file:// URLs, try to resolve them in the project.
                    boolean isFile = url.startsWith(FILE_SCHEME);
                    if (isFile && !url.endsWith("about:blank")) {
                        // If it's the file we're previewing, then hand back the rendered content.
                        if (url.equals(previewOf.getUrl())) {
                            return new CefResourceRequestHandlerAdapter() {
                                @Override
                                public CefResourceHandler getResourceHandler(CefBrowser browser, CefFrame frame, CefRequest request) {
                                    return new HtmlStringResourceHandler(latestContent);
                                }
                            };
                        }

                        // Otherwise, see if it resolves to a virtual file within the project, and
                        // set us up to open an editor to it if so.
                        VirtualFile found = resolveFile(url);
                        if (found != null) {
                            ApplicationManager.getApplication().invokeLater(() -> FileEditorManager.getInstance(project)
                                    .openEditor(new OpenFileDescriptor(project, found, 0), true));
                        }
                    }

                    // For non-file://, try to open it in the browser.
                    else if (!isFile)
                        BrowserUtil.browse(url);
                }

                // Return a 204 so we don't go anywhere.
                return new CefResourceRequestHandlerAdapter() {
                    @Override
                    public CefResourceHandler getResourceHandler(CefBrowser browser, CefFrame frame, CefRequest request) {
                        return new NoContentResourceHandler();
                    }
                };
            }
        }, htmlPanel.getCefBrowser());
    }

    @Nullable
    private VirtualFile resolveFile(String url) {
        // First, see if we can resolve the file by the exact URL.
        VirtualFile found = VirtualFileManager.getInstance().findFileByUrl(url);

        // If not, we'll try some heuristics. These are mostly aimed at the likes
        // of the Raku doc project, which serve Pod content according to a URL
        // mangling scheme.
        if (found == null) {
            // Mangle :: into /, and then split into path parts.
            String path = url.substring(FILE_SCHEME.length() + 1);
            path = path.replace("::", "/");
            String[] parts = path.split("/");

            // Look up the directory tree to see if we can resolve the file.
            VirtualFile curStartDirectory = previewOf.getParent();
            while (curStartDirectory != null && found == null) {
                // In this parent directory, walk down the path parts, case insensitively,
                // and for the final part, don't worry about extensions.
                VirtualFile curDirectory = curStartDirectory;
                for (int i = 0; curDirectory.exists() && i < parts.length; i++) {
                    if (i == parts.length - 1) {
                        // Final part. We want a file. Permit extensions.
                        for (VirtualFile child : curDirectory.getChildren()) {
                            if (child.isDirectory())
                                continue;
                            String lowerGot = child.getName().toLowerCase(Locale.ROOT);
                            String lowerWant = parts[i].toLowerCase(Locale.ROOT);
                            if (lowerGot.equals(lowerWant) || lowerGot.startsWith(lowerWant + ".")) {
                                found = child;
                                break;
                            }
                        }
                    }
                    else {
                        // Non-final part. We want a directory.
                        for (VirtualFile child : curDirectory.getChildren()) {
                            if (!child.isDirectory())
                                continue;
                            if (child.getName().equalsIgnoreCase(parts[i])) {
                                curDirectory = child;
                                break;
                            }
                        }
                    }
                }
                curStartDirectory = curStartDirectory.getParent();
            }
        }

        // Make sure anything we found is within the target project.
        if (found != null) {
            Project targetProject = ProjectUtil.guessProjectForFile(found);
            if (targetProject == project)
                return found;
        }
        return null;
    }

    @Override
    public @NotNull JComponent getComponent() {
        return htmlPanel.getComponent();
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return htmlPanel.getComponent();
    }

    @Override
    public @Nls(capitalization = Nls.Capitalization.Title) @NotNull String getName() {
        return "Pod Preview";
    }

    @Override
    public void setState(@NotNull FileEditorState state) {
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void addPropertyChangeListener(@NotNull PropertyChangeListener listener) {
    }

    @Override
    public void removePropertyChangeListener(@NotNull PropertyChangeListener listener) {
    }

    @Override
    public @Nullable FileEditorLocation getCurrentLocation() {
        return null;
    }

    @Override
    public void dispose() {
    }

    public void setPodHtml(String html) {
        ApplicationManager.getApplication().invokeLater(() -> {
            latestContent = html;
            htmlPanel.loadURL(previewOf.getUrl());
        });
    }
}
