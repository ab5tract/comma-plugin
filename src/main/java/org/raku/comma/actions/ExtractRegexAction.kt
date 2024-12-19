package org.raku.comma.actions

import com.intellij.lang.refactoring.RefactoringSupportProvider
import com.intellij.refactoring.RefactoringActionHandler
import com.intellij.refactoring.actions.BasePlatformRefactoringAction
import org.raku.comma.refactoring.RakuExtractRegexPartHandler

class ExtractRegexAction : BasePlatformRefactoringAction() {
    override fun getRefactoringHandler(provider: RefactoringSupportProvider): RefactoringActionHandler? {
        return RakuExtractRegexPartHandler()
    }

    override fun isAvailableInEditorOnly(): Boolean = true
}
