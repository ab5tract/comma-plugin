package edument.perl6idea.coverage;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.impl.DocumentMarkupModel;
import com.intellij.openapi.editor.markup.*;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiFile;
import com.intellij.util.ArrayUtil;
import edument.perl6idea.psi.Perl6File;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Perl6CoverageSourceAnnotator implements Disposable {
    public static final Key<List<RangeHighlighter>> PERL6_COVERAGE_HIGHLIGHTERS = Key.create("PERL6_COVERAGE_HIGHLIGHTERS");
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
            if (editor.getUserData(PERL6_COVERAGE_HIGHLIGHTERS) != null)
                return;
            TextAttributes markerStyle = getMarkerStyle();
            Document document = editor.getDocument();
            MarkupModel markupModel = DocumentMarkupModel.forDocument(document, editor.getProject(), true);
            if (!(file instanceof Perl6File) || !((Perl6File)file).isReal())
                return;
            Map<Integer, List<Integer>> lineMap = ((Perl6File)file).getStatementLineMap();
            List<RangeHighlighter> highlighters = new ArrayList<>();
            for (int line : lineUsers.keySet()) {
                int zeroBasedLine = line - 1;
                if (lineMap.containsKey(zeroBasedLine)) {
                    String info = formCoverageInfo(lineUsers.get(line));
                    for (int markLine : lineMap.get(zeroBasedLine)) {
                        final int startOffset = document.getLineStartOffset(markLine);
                        final int endOffset = document.getLineEndOffset(markLine);
                        RangeHighlighter highlighter = markupModel.addRangeHighlighter(startOffset, endOffset,
                                                                                       HighlighterLayer.LAST, null,
                                                                                       HighlighterTargetArea.LINES_IN_RANGE);
                        highlighter.setLineMarkerRenderer(new Perl6CoverageLineMarkerRenderer(markerStyle, info));
                        highlighters.add(highlighter);
                    }
                }
            }
            editor.putUserData(PERL6_COVERAGE_HIGHLIGHTERS, highlighters);
        });
    }

    private String formCoverageInfo(List<String> strings) {
        if (strings.size() == 0 || strings.get(0).equals("main"))
            return null;
        return String.join("\n", ArrayUtil.toStringArray(strings));
    }

    @NotNull
    private TextAttributes getMarkerStyle() {
        // TODO: make configurable
        return new TextAttributes(null, Color.decode("#0f6403"), null, null, 0);
    }

    public void hideAnnotations() {
        Editor editor = this.editor;
        PsiFile file = this.file;
        if (editor.isDisposed() || file == null)
            return;
        final List<RangeHighlighter> highlighters = editor.getUserData(PERL6_COVERAGE_HIGHLIGHTERS);
        if (highlighters != null) {
            for (final RangeHighlighter highlighter : highlighters) {
                ApplicationManager.getApplication().invokeLater(() -> highlighter.dispose());
            }
            editor.putUserData(PERL6_COVERAGE_HIGHLIGHTERS, null);
        }
    }

    @Override
    public void dispose() {
        hideAnnotations();
        this.lineUsers = null;
        this.file = null;
        this.editor = null;
    }
}
