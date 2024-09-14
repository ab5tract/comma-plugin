package org.raku.comma.refactoring;

import com.intellij.refactoring.ui.AbstractMemberSelectionPanel;
import com.intellij.refactoring.ui.AbstractMemberSelectionTable;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.SeparatorFactory;
import org.raku.comma.psi.RakuPsiDeclaration;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class RakuAttributeSelectionPanel extends AbstractMemberSelectionPanel<RakuPsiDeclaration, RakuAttributeInfo> {
    private final RakuAttributeSelectionTable myTable;

    public RakuAttributeSelectionPanel(String title, List<RakuAttributeInfo> attributeInfoList) {
        super();
        setLayout(new BorderLayout());
        myTable = new RakuAttributeSelectionTable(attributeInfoList);
        RakuAttributeSelectionTable.RakuAttributeInfoModel model = new
          RakuAttributeSelectionTable.RakuAttributeInfoModel();
        myTable.setMemberInfoModel(model);
        // Make checkbox column small, instead of it taking 50% of the width
        myTable.getColumnModel().getColumn(0).setMaxWidth(50);
        JScrollPane scrollPane = ScrollPaneFactory.createScrollPane(myTable);
        add(SeparatorFactory.createSeparator(title, myTable), BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    @Override
    public AbstractMemberSelectionTable<RakuPsiDeclaration, RakuAttributeInfo> getTable() {
        return myTable;
    }
}
