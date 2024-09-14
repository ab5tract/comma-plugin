package org.raku.comma.refactoring;

import com.intellij.refactoring.classMembers.MemberInfoChange;
import com.intellij.refactoring.classMembers.MemberInfoModel;
import com.intellij.refactoring.ui.AbstractMemberSelectionTable;
import org.raku.comma.psi.RakuPsiDeclaration;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

public class RakuAttributeSelectionTable extends AbstractMemberSelectionTable<RakuPsiDeclaration, RakuAttributeInfo> {
    public RakuAttributeSelectionTable(List<RakuAttributeInfo> infos) {
        super(infos, null, null);
    }

    @Override
    protected @Nullable Object getAbstractColumnValue(RakuAttributeInfo memberInfo) {
        return false;
    }

    @Override
    protected boolean isAbstractColumnEditable(int rowIndex) {
        return true;
    }

    @Override
    protected Icon getOverrideIcon(RakuAttributeInfo memberInfo) {
        return EMPTY_OVERRIDE_ICON;
    }

    public static class RakuAttributeInfoModel implements MemberInfoModel<RakuPsiDeclaration, RakuAttributeInfo> {
        @Override
        public void memberInfoChanged(@NotNull MemberInfoChange<RakuPsiDeclaration, RakuAttributeInfo> event) {}

        @Override
        public boolean isMemberEnabled(RakuAttributeInfo member) {
            return true;
        }

        @Override
        public boolean isCheckedWhenDisabled(RakuAttributeInfo member) {
            return false;
        }

        @Override
        public boolean isAbstractEnabled(RakuAttributeInfo member) {
            return false;
        }

        @Override
        public boolean isAbstractWhenDisabled(RakuAttributeInfo member) {
            return false;
        }

        @Override
        public Boolean isFixedAbstract(RakuAttributeInfo member) {
            return null;
        }

        @Override
        public int checkForProblems(@NotNull RakuAttributeInfo member) {
            return 0;
        }

        @Nls
        @Override
        public String getTooltipText(RakuAttributeInfo member) {
            return null;
        }
    }
}
