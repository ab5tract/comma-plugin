package edument.perl6idea.grammar;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.editor.event.EditorMouseEvent;
import com.intellij.openapi.editor.event.EditorMouseListener;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.markup.*;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.ColoredTreeCellRenderer;
import com.intellij.ui.JBColor;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.containers.ContainerUtil;
import edument.perl6idea.psi.Perl6PackageDecl;
import edument.perl6idea.psi.stub.index.Perl6GlobalTypeStubIndex;
import edument.perl6idea.psi.stub.index.Perl6IndexableType;
import edument.perl6idea.psi.stub.index.Perl6LexicalTypeStubIndex;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.JXComboBox;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

public class RakuGrammarPreviewer extends JPanel {
    private static final TextAttributes SELECTION_TEXT_ATTRS;
    private static final TextAttributes HIGHWATER_TEXT_ATTRS;
    private static final TextAttributes FAILED_TEXT_ATTRS;

    private static final SimpleTextAttributes NODE_TEXT_ATTRS;
    private static final SimpleTextAttributes CAPTURED_NODE_TEXT_ATTRS;
    private static final SimpleTextAttributes FAILED_NODE_TEXT_ATTRS;
    private static final SimpleTextAttributes FAILED_CAPTURED_NODE_TEXT_ATTRS;

    static {
        SELECTION_TEXT_ATTRS = new TextAttributes();
        SELECTION_TEXT_ATTRS.setEffectColor(JBColor.black);
        SELECTION_TEXT_ATTRS.setEffectType(EffectType.BOXED);
        SELECTION_TEXT_ATTRS.setBackgroundColor(JBColor.lightGray);

        HIGHWATER_TEXT_ATTRS = new TextAttributes();
        HIGHWATER_TEXT_ATTRS.setBackgroundColor(JBColor.yellow);

        FAILED_TEXT_ATTRS = new TextAttributes();
        FAILED_TEXT_ATTRS.setBackgroundColor(JBColor.red);

        NODE_TEXT_ATTRS = new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, JBColor.BLACK);
        CAPTURED_NODE_TEXT_ATTRS = new SimpleTextAttributes(SimpleTextAttributes.STYLE_BOLD, JBColor.BLACK);
        FAILED_NODE_TEXT_ATTRS = new SimpleTextAttributes(SimpleTextAttributes.STYLE_PLAIN, JBColor.RED);
        FAILED_CAPTURED_NODE_TEXT_ATTRS = new SimpleTextAttributes(SimpleTextAttributes.STYLE_BOLD, JBColor.RED);
    }

    private final Project myProject;
    private JComboBox myGrammarComboBox;
    private Editor myInputDataEditor;
    private Tree myParseTree;
    private JLabel myStatusLabel;
    private JPanel myMainPanel;
    private JBSplitter mySplitter;
    private CurrentGrammar current;
    private RangeHighlighter currentSelectionHighlight;
    private RangeHighlighter highWaterHighlight;
    private RangeHighlighter failHighlight;

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
        myMainPanel.add(myGrammarComboBox, "wrap, w 100%");
        myMainPanel.add(mySplitter, "w 100%, h 100%");
        myMainPanel.add(myStatusLabel, "newline, w 100%");
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
        mySplitter.setProportion(0.3f);
        mySplitter.setFirstComponent(myInputDataEditor.getComponent());
        mySplitter.setSecondComponent(new JBScrollPane(myParseTree));

        myStatusLabel = new JLabel();
        myStatusLabel.setText("Waiting for input...");
        myStatusLabel.setForeground(JBColor.DARK_GRAY);
    }

    @NotNull
    private Editor getInputDataEditor() {
        EditorFactory factory = EditorFactory.getInstance();
        EditorEx editor = (EditorEx)factory.createEditor(factory.createDocument(""));
        editor.setPlaceholder("Enter input to test the grammar with here.");
        editor.getSettings().setLineNumbersShown(true);
        editor.getSettings().setMouseClickSelectionHonorsCamelWords(false);
        editor.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(@NotNull DocumentEvent event) {
                if (current != null) {
                    current.scheduleUpdate();
                    clearCurrentSelectionHighlight();
                    clearHighwaterHighlight();
                    clearFailHighlight();
                }
            }
        });
        editor.addEditorMouseListener(new EditorMouseListener() {
            private int clickOffset;
            @Override
            public void mouseClicked(@NotNull EditorMouseEvent editorEvent) {
                MouseEvent event = editorEvent.getMouseEvent();
                if (event.getButton() == MouseEvent.BUTTON1) {
                    if (event.getClickCount() == 2) {
                        editor.getSelectionModel().removeSelection();
                        navigateParseTreeToNodeAt(clickOffset);
                    }
                    else {
                        clickOffset = editor.getCaretModel().getOffset();
                    }
                }
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
                    if (node.isSuccessful()) {
                        appendNode(node, NODE_TEXT_ATTRS, CAPTURED_NODE_TEXT_ATTRS);
                    }
                    else {
                        appendNode(node, FAILED_NODE_TEXT_ATTRS, FAILED_CAPTURED_NODE_TEXT_ATTRS);
                    }
                }
            }

            private void appendNode(ParseResultsModel.Node node, SimpleTextAttributes anonAttrs, SimpleTextAttributes capturedAttrs) {
                List<String> captureNames = node.getCaptureNames();
                if (captureNames == null || captureNames.isEmpty()) {
                    // Not captured
                    append(node.getName(), anonAttrs);
                }
                else if (captureNames.size() == 1 && captureNames.get(0).equals(node.getName())) {
                    // Common case of being captured under the node's own name
                    append(node.getName(), capturedAttrs);
                }
                else {
                    List<String> aliases = ContainerUtil.filter(captureNames, n -> n != node.getName());
                    append(node.getName(), aliases.size() == captureNames.size() ? anonAttrs : capturedAttrs);
                    append(" (");
                    boolean first = true;
                    for (String alias : aliases) {
                        if (first)
                            first = false;
                        else
                            append(", ");
                        append(alias, capturedAttrs);
                    }
                    append(")");
                }
            }
        });
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode[] selectedNodes = myParseTree.getSelectedNodes(DefaultMutableTreeNode.class, null);
                if (selectedNodes.length == 1) {
                    Object modelNode = selectedNodes[0].getUserObject();
                    if (modelNode instanceof ParseResultsModel.Node)
                        highlightCurrentSelection((ParseResultsModel.Node)modelNode);
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
        // Update tree view.
        ParseResultsModel.Node top = modelData.getTop();
        if (top != null) {
            clearCurrentSelectionHighlight();
            myParseTree.clearSelection();
            ((DefaultTreeModel)myParseTree.getModel()).setRoot(buildTreeModelNode(top));
            highlightFailAndHighwater(modelData);
        }

        // Update status.
        String errorMessage = null;
        if (modelData.getError() != null) {
            errorMessage = "Error: " + modelData.getError();
        }
        else if (modelData.getFailurePositions() != null) {
            errorMessage = top.isSuccessful()
                    ? "Grammar didn't match all input"
                    : "Grammar failed to match";
        }
        if (errorMessage != null) {
            myStatusLabel.setText("✘ " + errorMessage);
            myStatusLabel.setToolTipText(errorMessage);
            myStatusLabel.setForeground(JBColor.RED);
        }
        else {
            myStatusLabel.setText("✔ Parsed successfully");
            myStatusLabel.setForeground(JBColor.GREEN);
        }
    }

    private MutableTreeNode buildTreeModelNode(ParseResultsModel.Node node) {
        DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(node);
        for (ParseResultsModel.Node childNode : node.getChildren())
            treeNode.add(buildTreeModelNode(childNode));
        return treeNode;
    }

    private void highlightCurrentSelection(ParseResultsModel.Node node) {
        clearCurrentSelectionHighlight();
        myInputDataEditor.getCaretModel().moveToOffset(node.getStart());
        myInputDataEditor.getScrollingModel().scrollToCaret(ScrollType.MAKE_VISIBLE);
        if (node.isSuccessful()) {
            currentSelectionHighlight = myInputDataEditor.getMarkupModel().addRangeHighlighter(
                    node.getStart(), node.getEnd(),
                    HighlighterLayer.SELECTION, SELECTION_TEXT_ATTRS,
                    HighlighterTargetArea.EXACT_RANGE);
        }
    }

    private void clearCurrentSelectionHighlight() {
        if (currentSelectionHighlight != null) {
            myInputDataEditor.getMarkupModel().removeHighlighter(currentSelectionHighlight);
            currentSelectionHighlight = null;
        }
    }

    private void highlightFailAndHighwater(ParseResultsModel data) {
        clearHighwaterHighlight();
        clearFailHighlight();
        ParseResultsModel.FailurePositions positions = data.getFailurePositions();
        if (positions == null)
            return;
        if (positions.getHighwaterStart() != -1 && positions.getHighwaterStart() < positions.getFailureStart()) {
            highWaterHighlight = myInputDataEditor.getMarkupModel().addRangeHighlighter(
                    positions.getHighwaterStart(), positions.getFailureStart(),
                    HighlighterLayer.ERROR, HIGHWATER_TEXT_ATTRS, HighlighterTargetArea.EXACT_RANGE);
        }
        if (positions.getFailureStart() != -1 && positions.getFailureStart() < positions.getFailureEnd()) {
            failHighlight = myInputDataEditor.getMarkupModel().addRangeHighlighter(
                    positions.getFailureStart(), positions.getFailureEnd(),
                    HighlighterLayer.ERROR, FAILED_TEXT_ATTRS, HighlighterTargetArea.EXACT_RANGE);
        }
    }

    private void clearHighwaterHighlight() {
        if (highWaterHighlight != null) {
            myInputDataEditor.getMarkupModel().removeHighlighter(highWaterHighlight);
            highWaterHighlight = null;
        }
    }

    private void clearFailHighlight() {
        if (failHighlight != null) {
            myInputDataEditor.getMarkupModel().removeHighlighter(failHighlight);
            failHighlight = null;
        }
    }

    private void navigateParseTreeToNodeAt(int offset) {
        Object root = myParseTree.getModel().getRoot();
        if (root instanceof DefaultMutableTreeNode) {
            // First try with only successful nodes, and then retry with those
            // ultimately unsuccessful (e.g. in the highwater).
            if (!selectNodeCoveringOffset((DefaultMutableTreeNode)root, offset, false))
                selectNodeCoveringOffset((DefaultMutableTreeNode)root, offset, true);
        }
    }

    // Look for the deepest node in the tree that covers the offset and select
    // it.
    private boolean selectNodeCoveringOffset(DefaultMutableTreeNode treeNode, int offset, boolean visitUnsuccessful) {
        Object maybeNode = treeNode.getUserObject();
        if (maybeNode instanceof ParseResultsModel.Node) {
            // See if this node covers.
            ParseResultsModel.Node node = (ParseResultsModel.Node)maybeNode;
            boolean covers = false;
            if (node.isSuccessful()) {
                if (offset >= node.getStart() && offset <= node.getEnd()) {
                    covers = true;
                }
            }

            // If it covers, or we're visiting unsuccessful nodes, see if any of its
            // children do.
            boolean childCovers = false;
            if (covers || visitUnsuccessful) {
                for (Enumeration e = treeNode.children(); e.hasMoreElements(); ) {
                    TreeNode child = (TreeNode)e.nextElement();
                    if (child instanceof DefaultMutableTreeNode) {
                        if (selectNodeCoveringOffset((DefaultMutableTreeNode)child, offset, visitUnsuccessful)) {
                            childCovers = true;
                            break;
                        }
                    }
                }
            }

            // If we cover but no child covers, then pick this node to select.
            if (covers && !childCovers) {
                TreePath path = new TreePath(treeNode.getPath());
                myParseTree.getSelectionModel().setSelectionPath(path);
                myParseTree.scrollPathToVisible(path);
            }

            return covers || childCovers;
        }
        return false;
    }
}
