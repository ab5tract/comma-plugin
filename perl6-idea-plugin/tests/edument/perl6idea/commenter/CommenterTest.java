package edument.perl6idea.commenter;

import com.intellij.codeInsight.generation.actions.CommentByLineCommentAction;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import edument.perl6idea.filetypes.Perl6ScriptFileType;

public class CommenterTest extends LightCodeInsightFixtureTestCase {
    public void testCommenter() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "<caret>say 'foo';");
        CommentByLineCommentAction commentAction = new CommentByLineCommentAction();
        commentAction.actionPerformedImpl(getProject(), myFixture.getEditor());
        myFixture.checkResult("#say 'foo';");
        commentAction.actionPerformedImpl(getProject(), myFixture.getEditor());
        myFixture.checkResult("say 'foo';");
    }

    public void testMultilineCommenter() {
        myFixture.configureByText(Perl6ScriptFileType.INSTANCE, "<selection>say 'foo';\nsay 'bar';\n\n</selection>");
        CommentByLineCommentAction commentAction = new CommentByLineCommentAction();
        commentAction.actionPerformedImpl(getProject(), myFixture.getEditor());
        myFixture.checkResult("#say 'foo';\n#say 'bar';\n#\n");
        commentAction.actionPerformedImpl(getProject(), myFixture.getEditor());
        myFixture.checkResult("say 'foo';\nsay 'bar';\n\n");
    }
}
