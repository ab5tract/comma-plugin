package edument.perl6idea.actions;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

public class UpdateToCommaCompleteAction extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {
      BrowserUtil.browse("https://commaide.com/");
    }
}
