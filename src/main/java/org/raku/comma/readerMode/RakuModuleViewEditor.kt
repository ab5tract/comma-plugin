package org.raku.comma.readerMode

import com.intellij.codeHighlighting.BackgroundEditorHighlighter
import com.intellij.ide.structureView.StructureViewBuilder
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.EDT
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorLocation
import com.intellij.openapi.fileEditor.FileEditorState
import com.intellij.openapi.fileEditor.FileEditorStateLevel
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.util.UserDataHolderBase
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.IdeFocusManager
import com.intellij.pom.Navigatable
import com.intellij.psi.PsiDocumentManager
import com.intellij.ui.JBSplitter
import com.intellij.util.ui.JBUI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.annotations.Nls
import org.raku.comma.services.project.RakuProjectSdkService
import org.raku.comma.structureView.RakuStructureViewBuilder
import java.beans.PropertyChangeListener
import java.util.*
import javax.swing.JComponent

class RakuModuleViewEditor(
    private val myEditor: TextEditor,
    private var podPreviewEditor: PodPreviewEditor?,
    private val myFile: VirtualFile,
    private val myName: String
) : UserDataHolderBase(), TextEditor {
    private var myComponent: JComponent? = null
    private var myTriggerPodRenderCode: Runnable? = null
    var presentedState: RakuReaderModeState? = null
        private set

    override fun getFile(): VirtualFile? {
        return myFile
    }

    override fun getComponent(): JComponent {
        if (myComponent == null) {
            val splitter = JBSplitter(false, 0.5f, 0.15f, 0.85f)
            splitter.splitterProportionKey = "RakuModuleViewEditor.SplitterProportionKey"
            splitter.firstComponent = myEditor.component

            if (podPreviewEditor != null) {
                splitter.secondComponent = podPreviewEditor!!.component
            }
            splitter.dividerWidth = 3

            myComponent = JBUI.Panels.simplePanel(splitter)
            updateState(RakuReaderModeState.CODE)
        }
        return myComponent!!
    }

    private fun invalidateLayout() {
        myComponent!!.repaint()

        val focusComponent = preferredFocusedComponent
        if (focusComponent != null) {
            IdeFocusManager.findInstanceByComponent(focusComponent).requestFocus(focusComponent, true)
        }
    }

    fun updateState(state: RakuReaderModeState) {
        ApplicationManager.getApplication().runReadAction {
            presentedState = state
            val psiFile =
                Objects.requireNonNull(
                    myEditor.editor.project
                )?.let {
                    PsiDocumentManager.getInstance(it).getPsiFile(myEditor.editor.document)
                }
            psiFile?.putUserData(RakuActionProvider.RAKU_EDITOR_MODE_STATE, state)
        }

        myEditor.editor.project!!.service<RakuProjectSdkService>().runScope.launch {
            withContext(Dispatchers.EDT) {
                myTriggerPodRenderCode?.run()
                invalidateLayout()
                myEditor.component.isVisible =
                    state == RakuReaderModeState.CODE || state == RakuReaderModeState.SPLIT
                podPreviewEditor?.component?.isVisible =
                    state == RakuReaderModeState.DOCS || state == RakuReaderModeState.SPLIT
            }
        }
    }

    override fun getPreferredFocusedComponent(): JComponent? {
        if (presentedState == null) {
            presentedState = RakuReaderModeState.CODE
        }
        return when (presentedState) {
            RakuReaderModeState.SPLIT, RakuReaderModeState.CODE -> myEditor.preferredFocusedComponent
            RakuReaderModeState.DOCS -> podPreviewEditor?.preferredFocusedComponent
            null -> null
        }
    }

    override fun getName(): @Nls(capitalization = Nls.Capitalization.Title) String {
        return myName
    }

    override fun setState(state: FileEditorState) {
        if (state is RakuModuleEditorState) {
            if (state.editorState != null) myEditor.setState(state.editorState)
            if (state.viewerState != null) podPreviewEditor?.setState(state.viewerState)
        }
    }

    private fun requestFocus() {
        val focusComponent = preferredFocusedComponent
        if (focusComponent != null) {
            IdeFocusManager.findInstanceByComponent(focusComponent).requestFocus(focusComponent, true)
        }
    }

    override fun getState(level: FileEditorStateLevel): FileEditorState {
        return RakuModuleEditorState(myEditor.getState(level), podPreviewEditor?.getState(level))
    }

    override fun getStructureViewBuilder(): StructureViewBuilder? {
        val project = myEditor.editor.project ?: return null
        val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(myEditor.editor.document)
        return if (psiFile == null) null else RakuStructureViewBuilder(psiFile)
    }

    override fun getBackgroundHighlighter(): BackgroundEditorHighlighter? {
        return myEditor.backgroundHighlighter
    }

    override fun isModified(): Boolean {
        return myEditor.isModified
    }

    override fun isValid(): Boolean {
        return myEditor.isValid
    }

    override fun addPropertyChangeListener(listener: PropertyChangeListener) {
        myEditor.addPropertyChangeListener(listener)
    }

    override fun removePropertyChangeListener(listener: PropertyChangeListener) {
        myEditor.removePropertyChangeListener(listener)
    }

    override fun getCurrentLocation(): FileEditorLocation? {
        return myEditor.currentLocation
    }

    override fun dispose() {
        Disposer.dispose(myEditor)
    }

    fun setViewer(viewer: PodPreviewEditor) {
        podPreviewEditor = viewer
    }

    fun setCallback(triggerPodRenderCode: Runnable?) {
        myTriggerPodRenderCode = triggerPodRenderCode
    }

    override fun getEditor(): Editor {
        return myEditor.editor
    }

    override fun canNavigateTo(navigatable: Navigatable): Boolean {
        return myEditor.canNavigateTo(navigatable)
    }

    override fun navigateTo(navigatable: Navigatable) {
        if (presentedState == RakuReaderModeState.DOCS) {
            updateState(RakuReaderModeState.CODE)
        }
        myEditor.navigateTo(navigatable)
        requestFocus()
    }

    private class RakuModuleEditorState(val editorState: FileEditorState?, val viewerState: FileEditorState?) :
        FileEditorState {
        override fun canBeMergedWith(otherState: FileEditorState, level: FileEditorStateLevel): Boolean {
            return otherState is RakuModuleEditorState
        }
    }
}
