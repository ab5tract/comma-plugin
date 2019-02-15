package edument.perl6idea.profiler;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import edument.perl6idea.Perl6Icons;
import edument.perl6idea.psi.Perl6File;
import edument.perl6idea.psi.Perl6PsiElement;
import edument.perl6idea.psi.symbols.Perl6Symbol;
import edument.perl6idea.psi.symbols.Perl6SymbolKind;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.treetable.TreeTableModel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

public class Perl6ProfileTreeTable extends JXTreeTable {
    private final Perl6ProfileModel myModel;
    private final Project project;
    /* State of column may be: 1(sorted by this column),
     * 0(not sorted), -1(sorted by this column reversed).
     * Click on column `i` goes by this algorithm:
     * If column is 1  then set it to -1 and reverse
     * If column is 0  then set it to 1 and set all other values to 0
     * If column is -1 then set it to 1 and reverse
    */
    byte[] sorted = {0, 1, 0, 0};

    @Override
    protected void processMouseEvent(MouseEvent e) {
        super.processMouseEvent(e);
        JTable table =(JTable) e.getSource();
        Point point = e.getPoint();
        int row = table.rowAtPoint(point);
        if (e.getClickCount() == 2 && table.getSelectedRow() != -1) {
            ProfilerNode call = myModel.getCall(row);
            String filename = call.getFilename();
            String path = project.getBaseDir().getCanonicalPath();
            if (path == null) return;
            if (filename.startsWith(path)) {
                VirtualFile file = LocalFileSystem.getInstance().findFileByPath(filename);
                if (file != null) {
                    PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
                    if (!(psiFile instanceof Perl6File))
                        return;
                    psiFile.navigate(true);
                    Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
                    int offset = StringUtil.lineColToOffset(editor.getDocument().getText(), call.getLine() - 1, 0);
                    editor.getCaretModel().moveToOffset(offset);
                }
            }
        }
    }

    public Perl6ProfileTreeTable(Project project, Perl6ProfileModel model) {
        super(model);
        myModel = model;
        this.project = project;

        getTableHeader().addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = columnModel.getColumnIndexAtX(e.getX());
                if (index == -1) return;
                TreeTableModel model = getTreeTableModel();
                boolean reverse = false;
                switch (sorted[index]) {
                    case 1: {
                        sorted[index] = -1;
                        reverse = true;
                        break;
                    }
                    case 0: {
                        sorted = new byte[]{0, 0, 0, 0};
                        sorted[index] = 1;
                        break;
                    }
                    case -1: {
                        sorted[index] = 1;
                        reverse = true;
                        break;
                    }
                }
                if (model instanceof Perl6ProfileModel) {
                    ((Perl6ProfileModel)model).sortRoutines(index, reverse);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        // Set proper icons
        setLeafIcon(Perl6Icons.SUB);
        setCollapsedIcon(Perl6Icons.SUB);
        setExpandedIcon(Perl6Icons.SUB);
        setClosedIcon(Perl6Icons.SUB);
        setOpenIcon(Perl6Icons.SUB);
    }
}
