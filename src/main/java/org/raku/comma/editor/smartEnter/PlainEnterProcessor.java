//// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
//package org.raku.comma.editor.smartEnter;
//
//import com.intellij.ide.DataManager;
//import com.intellij.openapi.actionSystem.DataContext;
//import com.intellij.openapi.actionSystem.IdeActions;
//import com.intellij.openapi.editor.Document;
//import com.intellij.openapi.editor.Editor;
//import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
//import com.intellij.openapi.editor.actionSystem.EditorActionManager;
//import com.intellij.openapi.editor.ex.EditorEx;
//import com.intellij.openapi.util.TextRange;
//import com.intellij.openapi.util.text.StringUtil;
//import com.intellij.psi.PsiElement;
//import com.intellij.psi.PsiWhiteSpace;
//import com.intellij.psi.util.PsiTreeUtil;
//import com.intellij.util.text.CharArrayUtil;
//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;
//
//public class PlainEnterProcessor {
//    public void doEnter(Editor editor, PsiElement psiElement) {
//        if (processExistingBlankLine(editor, psiElement)) return;
//        EditorActionHandler handler = EditorActionManager.getInstance().getActionHandler(IdeActions.ACTION_EDITOR_START_NEW_LINE);
//        handler.execute(editor, editor.getCaretModel().getCurrentCaret(), ((EditorEx)editor).getDataContext());
//    }
//
//    /**
//     * There is a possible case that target code block already starts with the empty line:
//     * <pre>
//     *   void test(int i) {
//     *     if (i > 1[caret]) {
//     *
//     *     }
//     *   }
//     * </pre>
//     * We want just move caret to correct position at that empty line without creating additional empty line then.
//     *
//     * @param editor    target editor
//     * @param element   target element under caret
//     * @return {@code true} if it was found out that the given code block starts with the empty line and caret
//     * is pointed to correct position there, i.e. no additional processing is required;
//     * {@code false} otherwise
//     */
//    private static boolean processExistingBlankLine(@NotNull Editor editor,
//                                                    @Nullable PsiElement element) {
//        PsiWhiteSpace whiteSpace = null;
//        if (element != null) {
//            final PsiElement next = PsiTreeUtil.nextLeaf(element);
//            if (next instanceof PsiWhiteSpace) {
//                whiteSpace = (PsiWhiteSpace)next;
//            }
//        }
//
//        if (whiteSpace == null) {
//            return false;
//        }
//
//        final TextRange textRange = whiteSpace.getTextRange();
//        final Document document = editor.getDocument();
//        final CharSequence whiteSpaceText = document.getCharsSequence().subSequence(textRange.getStartOffset(), textRange.getEndOffset());
//        if (StringUtil.countNewLines(whiteSpaceText) < 2) {
//            return false;
//        }
//
//        int i = CharArrayUtil.shiftForward(whiteSpaceText, 0, " \t");
//        if (i >= whiteSpaceText.length() - 1) {
//            assert false : String.format("code block: %s, white space: %s",
//                                         "undefined", whiteSpace.getTextRange());
//            return false;
//        }
//
//        editor.getCaretModel().moveToOffset(i + 1 + textRange.getStartOffset());
//        EditorActionManager actionManager = EditorActionManager.getInstance();
//        EditorActionHandler actionHandler = actionManager.getActionHandler(IdeActions.ACTION_EDITOR_MOVE_LINE_END);
//        final DataContext dataContext = DataManager.getInstance().getDataContext(editor.getComponent());
//        actionHandler.execute(editor, editor.getCaretModel().getCurrentCaret(), dataContext);
//        return true;
//    }
//}
