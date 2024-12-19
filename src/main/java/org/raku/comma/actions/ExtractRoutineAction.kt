package org.raku.comma.actions

import com.intellij.lang.refactoring.RefactoringSupportProvider
import com.intellij.refactoring.RefactoringActionHandler
import com.intellij.refactoring.actions.BasePlatformRefactoringAction
import org.raku.comma.refactoring.RakuCodeBlockType
import org.raku.comma.refactoring.RakuExtractCodeBlockHandler

class ExtractRoutineAction : BasePlatformRefactoringAction() {
    init {
        setInjectedContext(true)
    }

    override fun getRefactoringHandler(provider: RefactoringSupportProvider): RefactoringActionHandler? {
        return RakuExtractCodeBlockHandler(RakuCodeBlockType.ROUTINE)
    }

    override fun isAvailableInEditorOnly(): Boolean = true
}
