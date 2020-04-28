package edument.perl6idea.grammar;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.ColoredTreeCellRenderer;
import com.intellij.ui.JBColor;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;
import edument.perl6idea.psi.Perl6PackageDecl;
import edument.perl6idea.psi.stub.index.Perl6GlobalTypeStubIndex;
import edument.perl6idea.psi.stub.index.Perl6IndexableType;
import edument.perl6idea.psi.stub.index.Perl6LexicalTypeStubIndex;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.JXComboBox;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RakuGrammarPreviewer extends JPanel {
    private final Project myProject;
    private JComboBox myGrammarComboBox;
    private Editor myInputDataEditor;
    private Tree myParseTree;
    private JPanel myMainPanel;
    private JBSplitter mySplitter;
    private CurrentGrammar current;

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
            myGrammarComboBox.addItemListener(i -> changeCurrentGrammar());
            changeCurrentGrammar();
        });

        myInputDataEditor = getInputDataEditor();
        myInputDataEditor.setBorder(BorderFactory.createLineBorder(JBColor.BLACK));
        myParseTree = getParseTree();
        myParseTree.setBorder(BorderFactory.createLineBorder(JBColor.BLACK));
        mySplitter = new JBSplitter(true);
        mySplitter.setProportion(0.5f);
        mySplitter.setFirstComponent(myInputDataEditor.getComponent());
        JBScrollPane rightPane = new JBScrollPane(myParseTree);
        mySplitter.setSecondComponent(rightPane);
    }

    @NotNull
    private Editor getInputDataEditor() {
        EditorFactory factory = EditorFactory.getInstance();
        EditorEx editor = (EditorEx)factory.createEditor(factory.createDocument(""));
        editor.setPlaceholder("Enter input to test the grammar with here.");
        editor.getSettings().setLineNumbersShown(true);
        editor.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(@NotNull DocumentEvent event) {
                if (current != null)
                    current.scheduleUpdate();
            }
        });
        return editor;
    }

    @NotNull
    private Tree getParseTree() {
        Tree tree = new Tree();
        tree.setCellRenderer(new ColoredTreeCellRenderer() {
            @Override
            public void customizeCellRenderer(@NotNull JTree tree, Object value,
                    boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode)value;
                if (treeNode.getUserObject() instanceof ParseResultsModel.Node) {
                    ParseResultsModel.Node node = (ParseResultsModel.Node)treeNode.getUserObject();
                    append(node.getName());
                }
            }
        });
        return tree;
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

    private void changeCurrentGrammar() {
        Object selected = myGrammarComboBox.getSelectedItem();
        if (selected instanceof Perl6PackageDecl) {
            if (current != null)
                current.dispose();
            current = new CurrentGrammar((Perl6PackageDecl)selected,
                    myInputDataEditor.getDocument(), this::updateResultsTree);
        }
    }

    private void updateResultsTree(ParseResultsModel modelData) {
        myParseTree.setModel(new DefaultTreeModel(buildTreeModelNode(modelData.getTop())));
    }

    private MutableTreeNode buildTreeModelNode(ParseResultsModel.Node node) {
        DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(node);
        for (ParseResultsModel.Node childNode : node.getChildren())
            treeNode.add(buildTreeModelNode(childNode));
        return treeNode;
    }
}
