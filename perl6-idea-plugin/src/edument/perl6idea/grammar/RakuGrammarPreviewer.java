package edument.perl6idea.grammar;

import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.JBColor;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.components.JBScrollPane;
import edument.perl6idea.psi.Perl6PackageDecl;
import edument.perl6idea.psi.stub.index.Perl6GlobalTypeStubIndex;
import edument.perl6idea.psi.stub.index.Perl6IndexableType;
import edument.perl6idea.psi.stub.index.Perl6LexicalTypeStubIndex;
import net.miginfocom.layout.CC;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.JXComboBox;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RakuGrammarPreviewer extends JPanel {
    private final Project myProject;
    private JComboBox myGrammarComboBox;
    private JEditorPane myInputDataEditor;
    private JLabel myParseTree;
    private JPanel myMainPanel;
    private JBSplitter mySplitter;

    public RakuGrammarPreviewer(Project project) {
        this.myProject = project;
        initUI();
    }

    private void initUI() {
        setLayout(new MigLayout());
        createUIComponents();
        myMainPanel = new JPanel();
        MigLayout migLayout = new MigLayout();
        myMainPanel.setLayout(migLayout);
        myMainPanel.add(myGrammarComboBox, "wrap, w 25%, gapright push");
        myMainPanel.add(new JScrollPane(mySplitter), "w 100%, h 100%");
        add(myMainPanel);
    }

    private void createUIComponents() {
        myGrammarComboBox = new JXComboBox();
        myGrammarComboBox.setEnabled(false);
        myGrammarComboBox.setRenderer(new GrammarComboBoxRenderer());
        myGrammarComboBox.addItem("Awaiting indexes...");

        DumbService.getInstance(myProject).smartInvokeLater(() -> {
            List<Perl6IndexableType> packages = new ArrayList<>();

            Perl6GlobalTypeStubIndex globalIndex = Perl6GlobalTypeStubIndex.getInstance();
            for (String globalType : globalIndex.getAllKeys(myProject))
                packages.addAll(globalIndex.get(globalType, myProject, GlobalSearchScope.projectScope(myProject)));

            Perl6LexicalTypeStubIndex lexicalIndex = Perl6LexicalTypeStubIndex.getInstance();
            for (String lexicalType : lexicalIndex.getAllKeys(myProject))
                packages.addAll(lexicalIndex.get(lexicalType, myProject, GlobalSearchScope.projectScope(myProject)));

            myGrammarComboBox.removeAllItems();
            for (Perl6IndexableType type : packages) {
                if (type instanceof Perl6PackageDecl) {
                    if (Objects.equals(((Perl6PackageDecl)type).getPackageKind(), "grammar")) {
                        myGrammarComboBox.addItem(type);
                    }
                }
            }
            myGrammarComboBox.setEnabled(true);
        });

        myInputDataEditor = getInputDataEditor();
        myInputDataEditor.setBorder(BorderFactory.createLineBorder(JBColor.BLACK));
        myParseTree = getParseTree();
        myParseTree.setBorder(BorderFactory.createLineBorder(JBColor.BLACK));
        mySplitter = new JBSplitter(true);
        mySplitter.setProportion(0.5f);
        JBScrollPane leftPane = new JBScrollPane(myInputDataEditor);
        mySplitter.setFirstComponent(leftPane);
        JBScrollPane rightPane = new JBScrollPane(myParseTree);
        mySplitter.setSecondComponent(rightPane);
    }

    @NotNull
    private JEditorPane getInputDataEditor() {
        return new JEditorPane() {
            @Override
            public boolean getScrollableTracksViewportWidth() {
                return true;
            }
        };
    }

    @NotNull
    private JLabel getParseTree() {
        return new JLabel("Parse tree");
    }

    private static class GrammarComboBoxRenderer implements ListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            if (value instanceof String) {
                return new JLabel((String)value);
            } else if (value instanceof Perl6PackageDecl) {
                return new JLabel(((Perl6PackageDecl)value).getPackageName());
            }
            return new JLabel();
        }
    }
}
