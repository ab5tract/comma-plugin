package org.raku.coverage;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.markup.ActiveGutterRenderer;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.ui.ColorUtil;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.event.MouseEvent;

public class RakuCoverageLineMarkerRenderer implements ActiveGutterRenderer {
    private final TextAttributes style;
    private final String info;

    public RakuCoverageLineMarkerRenderer(TextAttributes style, String info) {
        this.style = style;
        this.info = info;
    }

    @Nullable
    @Override
    public String getTooltipText() {
        return info;
    }

    @Override
    public void doAction(Editor editor, MouseEvent e) {
    }

    @Override
    public boolean canDoAction(MouseEvent e) {
        return info != null;
    }

    @Override
    public void paint(Editor editor, Graphics g, Rectangle r) {
        Color bgColor = style.getBackgroundColor();
        if (bgColor == null) {
            bgColor = style.getForegroundColor();
        }
        if (editor.getSettings().isLineNumbersShown() || editor.getGutter().isAnnotationsShown()) {
            if (bgColor != null) {
                bgColor = ColorUtil.toAlpha(bgColor, 150);
            }
        }
        if (bgColor != null) {
            g.setColor(bgColor);
        }
        g.fillRect(r.x, r.y, r.width, r.height);
    }
}
