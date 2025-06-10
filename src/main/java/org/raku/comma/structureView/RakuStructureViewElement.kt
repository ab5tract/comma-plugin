package org.raku.comma.structureView

import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.containers.toArray
import org.raku.comma.extensions.RakuFrameworkCall
import org.raku.comma.psi.*

class RakuStructureViewElement : StructureViewTreeElement {
    private val element: RakuPsiElement
    private val calculatedPresentation: ItemPresentation?

    constructor(element: RakuPsiElement) {
        this.element = element
        this.calculatedPresentation = getPresentation()
    }

    constructor(element: RakuPsiElement, calculatedPresentation: ItemPresentation?) {
        this.element = element
        this.calculatedPresentation = calculatedPresentation
    }

    override fun getChildren(): Array<TreeElement?> {
        val structureElements: MutableList<StructureViewTreeElement?> = ArrayList<StructureViewTreeElement?>()
        if (element is RakuPsiScope) {
            // Add declarations.
            val declarations = element.getDeclarations()
            val declSet: MutableSet<RakuPsiDeclaration?> = HashSet<RakuPsiDeclaration?>()
            for (decl in declarations) {
                if (applicable(decl)) {
                    structureElements.add(RakuStructureViewElement(decl!!))
                    declSet.add(decl)
                }
            }

            // Look through statements to see if there's any DSL-like framework
            // calls.
            val extensions = RakuFrameworkCall.EP_NAME.extensions
            collectFrameworkCalls(structureElements, extensions, declSet, element)
        }
        return structureElements.toArray<TreeElement?>(StructureViewTreeElement.EMPTY_ARRAY as Array<TreeElement?>)
    }

    override fun getPresentation(): ItemPresentation {
        if (calculatedPresentation != null) return calculatedPresentation

        return when (element) {
            is RakuFile         -> rakuFileStructureElement(element)
            is RakuPackageDecl  -> rakuPackageDeclStructureElement(element)
            is RakuRegexDecl    -> rakuRegexDeclStructureElement(element)
            is RakuRoutineDecl  -> rakuRoutineDeclStructureElement(element)
            is RakuConstant     -> rakuConstantStructureElement(element)
            is RakuVariableDecl -> rakuVariableDeclStructureElement(element)
            is RakuSubset       -> rakuSubsetStructureElement(element)
            is RakuEnum         -> rakuEnumStructureElement(element)

            else                -> throw IllegalArgumentException()
        }
    }

    override fun getValue(): RakuPsiElement {
        return element
    }

    override fun navigate(requestFocus: Boolean) {
        element.navigate(requestFocus)
    }

    override fun canNavigate(): Boolean {
        return element.canNavigate()
    }

    override fun canNavigateToSource(): Boolean {
        return element.canNavigateToSource()
    }

    companion object {
        private fun collectFrameworkCalls(
            structureElements: MutableList<StructureViewTreeElement?>,
            extensions: Array<RakuFrameworkCall>,
            stoppers: MutableSet<RakuPsiDeclaration?>,
            search: RakuPsiElement?
        ) {
            // If we hit a stopper, don't enter it; it's another declaration in the tree.
            if (search is RakuPsiDeclaration && stoppers.contains(search)) return

            // See if this element is a DSL call.
            if (search is RakuSubCall) {
                for (ext in extensions) {
                    if (ext.isApplicable(search)) {
                        val presentation = ext.getStructureViewPresentation(search, ext.getFrameworkData(search))
                        structureElements.add(RakuStructureViewElement(search, presentation))
                        return
                    }
                }
            }

            // Otherwise, walk children.
            val children = PsiTreeUtil.getChildrenOfType<RakuPsiElement?>(search, RakuPsiElement::class.java)
            if (children != null) for (child in children) collectFrameworkCalls(
                structureElements,
                extensions,
                stoppers,
                child
            )
        }

        private fun applicable(child: RakuPsiElement?): Boolean {
            return child is RakuFile || child is RakuPackageDecl && child.getName() != null || child is RakuRoutineDecl && child.getName() != null || child is RakuRegexDecl && child.getName() != null || child is RakuConstant && child.getName() != null ||
                    (child is RakuVariableDecl && child.getScope() == "has"
                            && child.getName() != null) || child is RakuSubset && child.getName() != null || child is RakuEnum && child.getName() != null
        }
    }
}
