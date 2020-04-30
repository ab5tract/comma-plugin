package edument.perl6idea.grammar;

import com.intellij.icons.AllIcons;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
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
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.*;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.containers.ContainerUtil;
import edument.perl6idea.psi.Perl6PackageDecl;
import edument.perl6idea.psi.Perl6RegexDecl;
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
import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class RakuGrammarPreviewer extends JPanel {
    private static final TextAttributes SELECTION_TEXT_ATTRS;
    private static final TextAttributes HIGHWATER_TEXT_ATTRS;
    private static final TextAttributes FAILED_TEXT_ATTRS;

    private static final SimpleTextAttributes NODE_TEXT_ATTRS;
    private static final SimpleTextAttributes CAPTURED_NODE_TEXT_ATTRS;
    private static final SimpleTextAttributes FAILED_NODE_TEXT_ATTRS;
    private static final SimpleTextAttributes FAILED_CAPTURED_NODE_TEXT_ATTRS;
    public static final String NO_GRAMMARS_FOUND_IN_PROJECT = "No grammars found in project";

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
    private boolean showFailedRules = true;
    private boolean showNonCapturingRules = true;
    private RangeHighlighter currentSelectionHighlight;
    private RangeHighlighter highWaterHighlight;
    private RangeHighlighter failHighlight;
    private final ScheduledExecutorService timedExecutor;

    public RakuGrammarPreviewer(Project project) {
        this.myProject = project;
        this.timedExecutor = Executors.newSingleThreadScheduledExecutor();
        initUI();
    }

    private void initUI() {
        setLayout(new MigLayout("fill, insets 0"));
        createUIComponents();
        myMainPanel = new JPanel();
        MigLayout migLayout = new MigLayout();
        myMainPanel.setLayout(migLayout);
        myMainPanel.add(myGrammarComboBox, "wrap, w 100%");
        myMainPanel.add(mySplitter, "w 100%, h 100%");
        add(myMainPanel, "w 100%, h 100%");
    }

    private void createUIComponents() {
        myGrammarComboBox = new JXComboBox();
        myGrammarComboBox.setEnabled(false);
        myGrammarComboBox.setRenderer(new GrammarComboBoxRenderer());
        myGrammarComboBox.addItem("Awaiting indexes...");

        DumbService.getInstance(myProject).smartInvokeLater(this::startWachingGrammars);

        myInputDataEditor = getInputDataEditor();
        myInputDataEditor.setBorder(BorderFactory.createLineBorder(JBColor.BLACK));

        myStatusLabel = new JLabel();
        myStatusLabel.setText("Waiting for input...");
        myStatusLabel.setForeground(JBColor.DARK_GRAY);

        myParseTree = getParseTree();
        myParseTree.setBorder(BorderFactory.createEmptyBorder());

        ToolbarDecorator decorator = ToolbarDecorator.createDecorator(myParseTree);
        decorator.addExtraAction(new AnActionButton("Go To Rule Source", AllIcons.General.Locate) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                navigateToRuleSource();
            }

            @Override
            public void updateButton(@NotNull AnActionEvent e) {
                DefaultMutableTreeNode[] selectedNodes = myParseTree.getSelectedNodes(DefaultMutableTreeNode.class, null);
                e.getPresentation().setEnabled(selectedNodes.length == 1);
            }
        });
        decorator.addExtraAction(new ToggleActionButton("Show Failed Rules", AllIcons.RunConfigurations.TestFailed) {
            @Override
            public boolean isSelected(AnActionEvent e) {
                return showFailedRules;
            }

            @Override
            public void setSelected(AnActionEvent e, boolean state) {
                showFailedRules = state;
                reloadTree();
            }
        });
        decorator.addExtraAction(new ToggleActionButton("Show Non-Captruing Rules", AllIcons.RunConfigurations.TestIgnored) {
            @Override
            public boolean isSelected(AnActionEvent e) {
                return showNonCapturingRules;
            }

            @Override
            public void setSelected(AnActionEvent e, boolean state) {
                showNonCapturingRules = state;
                reloadTree();
            }
        });

        JPanel resultsArea = new JPanel();
        resultsArea.setLayout(new MigLayout("fill, insets 0"));
        resultsArea.add(myStatusLabel, "wrap, w 100%");
        resultsArea.add(decorator.createPanel(), "w 100%, h 100%");

        mySplitter = new JBSplitter(true);
        mySplitter.setProportion(0.3f);
        mySplitter.setFirstComponent(myInputDataEditor.getComponent());
        mySplitter.setSecondComponent(resultsArea);
    }

    private void navigateToRuleSource() {
        DefaultMutableTreeNode[] selectedNodes = myParseTree.getSelectedNodes(DefaultMutableTreeNode.class, null);
        if (selectedNodes.length == 1) {
            Object node = selectedNodes[0].getUserObject();
            if (node instanceof ParseResultsModel.Node) {
                Object selectedGrammar = myGrammarComboBox.getSelectedItem();
                if (selectedGrammar instanceof Perl6PackageDecl) {
                    Perl6PackageDecl grammar = (Perl6PackageDecl)selectedGrammar;
                    if (grammar.isValid()) {
                        Collection<Perl6RegexDecl> decls = PsiTreeUtil.findChildrenOfType(grammar, Perl6RegexDecl.class);
                        String name = ((ParseResultsModel.Node)node).getName();
                        for (Perl6RegexDecl decl : decls) {
                            if (Objects.equals(name, decl.getRegexName())) {
                                decl.navigate(true);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    private void startWachingGrammars() {
        myGrammarComboBox.removeAllItems();
        myGrammarComboBox.addItem(NO_GRAMMARS_FOUND_IN_PROJECT);
        myGrammarComboBox.addItemListener(i -> changeCurrentGrammar());
        timedExecutor.scheduleAtFixedRate(this::syncGrammarList, 0, 1000, TimeUnit.MILLISECONDS);
    }

    private void syncGrammarList() {
        // Gather current set of grammars and sort them.
        List<Perl6IndexableType> packages = new ArrayList<>();
        Application application = ApplicationManager.getApplication();
        application.runReadAction(() -> {
            Perl6GlobalTypeStubIndex globalIndex = Perl6GlobalTypeStubIndex.getInstance();
            Perl6LexicalTypeStubIndex lexicalIndex = Perl6LexicalTypeStubIndex.getInstance();
            addGrammarsFrom(globalIndex, packages);
            addGrammarsFrom(lexicalIndex, packages);
            packages.sort(Comparator.comparing(NavigationItem::getName));
        });

        // Sync the combo box.
        application.invokeLater(() -> {
            if (packages.isEmpty()) {
                // Put the message in place saying no grammars if it's not there
                // already.
                if (myGrammarComboBox.getItemCount() != 1 ||
                        !Objects.equals(myGrammarComboBox.getItemAt(0), NO_GRAMMARS_FOUND_IN_PROJECT)) {
                    myGrammarComboBox.removeAllItems();
                    myGrammarComboBox.addItem(NO_GRAMMARS_FOUND_IN_PROJECT);
                    myGrammarComboBox.setEnabled(false);
                    clearParseTree();
                    myStatusLabel.setText("Waiting for input...");
                    myStatusLabel.setForeground(JBColor.DARK_GRAY);
                }
            }
            else {
                // Check if there's a difference.
                boolean needChange = myGrammarComboBox.getItemCount() != packages.size();
                if (!needChange) {
                    for (int i = 0; i < packages.size(); i++) {
                        Object item = myGrammarComboBox.getItemAt(i);
                        if (!(item instanceof Perl6PackageDecl) ||
                                !Objects.equals(packages.get(i), ((Perl6PackageDecl)item))) {
                            needChange = true;
                            break;
                        }
                    }
                }

                // If so, we need to sync up what's there now with what's new. We shall
                // reslect the current item also.
                if (needChange) {
                    Object previousSelection = myGrammarComboBox.getSelectedItem();
                    String previouslySelectedName = previousSelection instanceof Perl6PackageDecl
                            ? ((Perl6PackageDecl)previousSelection).getName()
                            : null;
                    myGrammarComboBox.removeAllItems();
                    for (Perl6IndexableType grammar : packages){
                        myGrammarComboBox.addItem(grammar);
                        if (Objects.equals(grammar.getName(), previouslySelectedName))
                            myGrammarComboBox.setSelectedIndex(myGrammarComboBox.getItemCount() - 1);
                    }
                    myGrammarComboBox.setEnabled(true);
                    changeCurrentGrammar();
                }
            }
        });
    }

    private void addGrammarsFrom(StringStubIndexExtension<Perl6IndexableType> index, List<Perl6IndexableType> packages) {
        for (String globalType : index.getAllKeys(myProject))
            packages.addAll(index.get(globalType, myProject, GlobalSearchScope.projectScope(myProject))
                .stream()
                .filter(maybeGrammar -> maybeGrammar instanceof Perl6PackageDecl &&
                        Objects.equals(((Perl6PackageDecl)maybeGrammar).getPackageKind(), "grammar"))
                .collect(Collectors.toList()));
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
                if (current != null)
                    performUpdate();
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

    private void performUpdate() {
        current.scheduleUpdate();
        clearCurrentSelectionHighlight();
        clearHighwaterHighlight();
        clearFailHighlight();
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
        if (!(selected instanceof Perl6PackageDecl))
            return;
        if (current != null) {
            if (current.getGrammarName().equals(((Perl6PackageDecl)selected).getPackageName()))
                return;
            current.dispose();
        }
        current = new CurrentGrammar((Perl6PackageDecl)selected,
                myInputDataEditor.getDocument(), this::updateResultsTree,
                this::startedProcessing, timedExecutor);
        clearParseTree();
        if (myInputDataEditor.getDocument().getTextLength() != 0)
            performUpdate();
    }

    private void clearParseTree() {
        ((DefaultTreeModel)myParseTree.getModel()).setRoot(new DefaultMutableTreeNode(""));
    }

    private void startedProcessing() {
        myStatusLabel.setText("Collecting results...");
        myStatusLabel.setForeground(JBColor.YELLOW);
        clearHighwaterHighlight();
        clearFailHighlight();
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
        DefaultMutableTreeNode treeNode = new GrammarTreeNode(node);
        for (ParseResultsModel.Node childNode : node.getChildren())
            treeNode.add(buildTreeModelNode(childNode));
        return treeNode;
    }

    private void reloadTree() {
        ((DefaultTreeModel)myParseTree.getModel()).reload();
    }

    private class GrammarTreeNode extends DefaultMutableTreeNode {
        public GrammarTreeNode(Object userObject) {
            super(userObject, true);
        }

        @Override
        public TreeNode getChildAt(int index) {
            if (children == null)
                throw new ArrayIndexOutOfBoundsException("Node has no children");

            int realIndex = -1;
            int visibleIndex = -1;
            Enumeration e = children.elements();
            while (e.hasMoreElements()) {
                GrammarTreeNode node = (GrammarTreeNode)e.nextElement();
                if (shouldDisplay(node))
                    visibleIndex++;
                realIndex++;
                if (visibleIndex == index)
                    return (TreeNode)children.elementAt(realIndex);
            }

            throw new ArrayIndexOutOfBoundsException("Index unmatched");
        }

        @Override
        public int getChildCount() {
            int count = 0;
            if (children != null) {
                Enumeration e = children.elements();
                while (e.hasMoreElements())
                    if (shouldDisplay((GrammarTreeNode)e.nextElement()))
                        count++;
            }
            return count;
        }

        @Override
        public Enumeration children() {
            int n = getChildCount();
            return new Enumeration() {
                private int i = 0;

                @Override
                public boolean hasMoreElements() {
                    return i < n;
                }

                @Override
                public Object nextElement() {
                    return getChildAt(i++);
                }
            };
        }

        private boolean shouldDisplay(GrammarTreeNode treeNode) {
            ParseResultsModel.Node node = (ParseResultsModel.Node)treeNode.getUserObject();
            if (!showFailedRules && !node.isSuccessful())
                return false;
            if (!showNonCapturingRules && node.getCaptureNames() == null && !node.isProtoCandidate())
                return false;
            return true;
        }
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
