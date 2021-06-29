package edument.perl6idea.readerMode;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.ui.jcef.JBCefBrowser;
import com.intellij.ui.jcef.JBCefJSQuery;
import com.intellij.ui.jcef.JCEFHtmlPanel;
import edument.perl6idea.editor.podPreview.HtmlStringResourceHandler;
import edument.perl6idea.editor.podPreview.NoContentResourceHandler;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefRequestHandlerAdapter;
import org.cef.handler.CefResourceHandler;
import org.cef.handler.CefResourceRequestHandler;
import org.cef.handler.CefResourceRequestHandlerAdapter;
import org.cef.misc.BoolRef;
import org.cef.network.CefRequest;
import org.cef.network.CefResponse;
import org.cef.network.CefURLRequest;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.util.Locale;

public class PodPreviewEditor extends UserDataHolderBase implements FileEditor {
    private static final String FILE_SCHEME = "file://";
    private static final String EXTERNAL_SCHEME = "raku://";

    private final JCEFHtmlPanel htmlPanel;
    private final Project project;
    private final VirtualFile previewOf;
    public final EditorImpl editor;
    private final Perl6ModuleViewEditor moduleViewEditor;
    private String latestContent = "";
    private int curOffset = 0;
    private JBCefJSQuery myCodeModeQuery;
    private JBCefJSQuery mySplitModeQuery;
    private JBCefJSQuery myDocModeQuery;

    public PodPreviewEditor(Project project,
                            VirtualFile previewOf,
                            EditorImpl editor, Perl6ModuleViewEditor moduleViewEditor) {
        this.project = project;
        this.previewOf = previewOf;
        this.editor = editor;
        this.moduleViewEditor = moduleViewEditor;
        String previewUrl = previewOf.getUrl();
        htmlPanel = new JCEFHtmlPanel(previewUrl);
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
                    boolean isFile = url.startsWith(FILE_SCHEME) || url.startsWith(EXTERNAL_SCHEME);
                    if (isFile && !url.endsWith("about:blank")) {
                        // If it's the file we're previewing, then hand back the rendered content.
                        if (url.equals(previewUrl)) {
                            return new CefResourceRequestHandlerAdapter() {
                                @Override
                                public CefResourceHandler getResourceHandler(CefBrowser browser, CefFrame frame, CefRequest request) {
                                    String withHandlers = latestContent.replace("</body>", getModeChangeHandlers() + "</body>");
                                    return new HtmlStringResourceHandler(withHandlers);
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

    public String getModeChangeHandlers() {
        CefBrowser browser = htmlPanel.getCefBrowser();
        JBCefBrowser jbBrowser = JBCefBrowser.getJBCefBrowser(browser);
        if (jbBrowser == null)
            return "";
        if (myCodeModeQuery == null) {
            myCodeModeQuery = JBCefJSQuery.create(jbBrowser);
            myCodeModeQuery.addHandler((ignore) -> {
                moduleViewEditor.updateState(Perl6ReaderModeState.CODE);
                return null;
            });
        }
        if (mySplitModeQuery == null) {
            mySplitModeQuery = JBCefJSQuery.create(jbBrowser);
            mySplitModeQuery.addHandler((ignore) -> {
                moduleViewEditor.updateState(Perl6ReaderModeState.SPLIT);
                return null;
            });
        }
        if (myDocModeQuery == null) {
            myDocModeQuery = JBCefJSQuery.create(jbBrowser);
            myDocModeQuery.addHandler((ignore) -> {
                moduleViewEditor.updateState(Perl6ReaderModeState.DOCS);
                return null;
            });
        }
        return
            "<script>window.JavaPanelBridge = {" +
            "    goToCodeMode : function() {" +
            myCodeModeQuery.inject(null) +
            "    }," +
            "    goToSplitMode : function() {" +
            mySplitModeQuery.inject(null) +
            "    }," +
            "    goToDocumentationMode : function() {" +
            myDocModeQuery.inject(null) +
            "    }" +
            "}</script>\n";
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
        if (myCodeModeQuery != null && mySplitModeQuery != null) {
            Disposer.dispose(myCodeModeQuery);
            Disposer.dispose(myDocModeQuery);
            Disposer.dispose(mySplitModeQuery);
        }
    }

    public void setPodHtml(String html) {
        ApplicationManager.getApplication().invokeLater(() -> {
            latestContent = html;
            htmlPanel.loadURL(previewOf.getUrl());
        });
    }

    public void scrollTo(int offset) {
        if (offset == curOffset)
            return;
        CefBrowser browser = htmlPanel.getCefBrowser();
        // We may not have an exact position due to leading removed whitespace, so try
        // positions beyond the chosen one.
        browser.executeJavaScript(
                "for (var i = " + offset + "; i < " + (offset + 32) + "; i++) {\n" +
                "    var el = document.getElementById(\"scroll-pos-" + offset + "\");\n" +
                "    if (el != null) {\n" +
                "        el.scrollIntoView();\n" +
                "        break;\n" +
                "    }\n" +
                "}",
                browser.getURL(), 0);
        curOffset = offset;
    }
}
