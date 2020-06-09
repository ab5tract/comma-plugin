// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package edument.perl6idea.project.structure.ui;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.roots.ProjectModelExternalSource;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class ModificationOfImportedModelWarningComponent {
    private final JLabel myLabel;

    public ModificationOfImportedModelWarningComponent() {
        myLabel = new JLabel();
        hideWarning();
    }

    public JLabel getLabel() {
        return myLabel;
    }

    public void showWarning(@NotNull String elementDescription, @NotNull ProjectModelExternalSource externalSource) {
        myLabel.setVisible(true);
        myLabel.setBorder(JBUI.Borders.empty(5, 5));
        myLabel.setIcon(AllIcons.General.BalloonWarning);
        myLabel.setText(UIUtil.toHtml(getWarningText(elementDescription, externalSource)));
    }

    @NotNull
    public static String getWarningText(@NotNull String elementDescription, @NotNull ProjectModelExternalSource externalSource) {
        return elementDescription +
               " is imported from " +
               externalSource.getDisplayName() +
               ". Any changes made in its configuration may be lost after reimporting.";
    }

    public void hideWarning() {
        myLabel.setVisible(false);
    }
}
