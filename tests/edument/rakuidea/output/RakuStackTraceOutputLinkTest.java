package edument.rakuidea.output;

import com.intellij.execution.filters.Filter;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.psi.PsiFile;
import edument.rakuidea.CommaFixtureTestCase;
import edument.rakuidea.run.RakuOutputLinkFilter;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class RakuStackTraceOutputLinkTest extends CommaFixtureTestCase {
    @Override
    protected String getTestDataPath() {
        return "rakuidea-idea-plugin/testData/outputFilter";
    }

    public void testStackTraceFilterRegex() throws TimeoutException, ExecutionException {
        doTest("    in method with-connection at %s (Easii::DB) line 15\n", 14);
        doTest("    in block at %s (Easii::Input::Store) line 32\n", 31);
        doTest("in block <unit> at %s line 2", 1);
    }

    private void doTest(String stackTraceLine, int number) throws TimeoutException, ExecutionException {
        PsiFile file = myFixture.configureByFiles("IdeaFoo/Prefix.p6", "IdeaFoo/Test.pm6")[1];
        RakuOutputLinkFilter filter = new RakuOutputLinkFilter(getProject());
        String line = String.format(stackTraceLine, file.getVirtualFile().getPath());
        Filter.Result result = filter.applyFilter(line, line.length());
        List<Filter.ResultItem> items = result.getResultItems();
        assertNotEmpty(items);

        items.get(0).getHyperlinkInfo().navigate(getProject());

        FileEditor[] editors = FileEditorManager.getInstance(getProject()).getSelectedEditors();
        assertEquals(1, editors.length);
        assertEquals(editors[0].getFile(), file.getVirtualFile());
        Editor editor = DataManager.getInstance().getDataContextFromFocusAsync().blockingGet(1).getData(CommonDataKeys.EDITOR);
        assertEquals(number, editor.getCaretModel().getOffset());
    }
}
