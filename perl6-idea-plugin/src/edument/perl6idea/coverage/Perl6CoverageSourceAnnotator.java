package edument.perl6idea.coverage;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.impl.DocumentMarkupModel;
import com.intellij.openapi.editor.markup.*;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Perl6CoverageSourceAnnotator implements Disposable {
    private Map<Integer, List<String>> lineUsers = new HashMap<>();
    private PsiFile file;
    private Editor editor;

    public Perl6CoverageSourceAnnotator(PsiFile file, Editor editor, Map<String, Set<Integer>> data) {
        this.file = file;
        this.editor = editor;
        for (String key : data.keySet()) {
            for (Integer line : data.get(key)) {
                if (lineUsers.get(line) == null)
                    lineUsers.put(line, new ArrayList<>());
                lineUsers.get(line).add(key);
            }
        }
    }

    public void showAnnotations() {
        ApplicationManager.getApplication().invokeLater(() -> {
            TextAttributes markerStyle = getMarkerStyle();
            Document document = editor.getDocument();
            MarkupModel markupModel = DocumentMarkupModel.forDocument(document, editor.getProject(), true);
            for (int line : lineUsers.keySet()) {
                final int startOffset = document.getLineStartOffset(line - 1);
                final int endOffset = document.getLineEndOffset(line - 1);
                RangeHighlighter highlighter = markupModel.addRangeHighlighter(startOffset, endOffset,
                         HighlighterLayer.SELECTION - 1, null,
                         HighlighterTargetArea.LINES_IN_RANGE);
                highlighter.setLineMarkerRenderer(new Perl6CoverageLineMarkerRenderer(markerStyle));
            }
        });
    }

    @NotNull
    private TextAttributes getMarkerStyle() {
        // TODO: make configurable
        return new TextAttributes(null, Color.green, null, null, 0);
    }

    public void hideAnnotations() {
    }

    @Override
    public void dispose() {
        hideAnnotations();
        this.lineUsers = null;
        this.file = null;
        this.editor = null;
    }
}
