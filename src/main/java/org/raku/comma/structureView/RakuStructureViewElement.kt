package org.raku.comma.structureView

import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.navigation.ItemPresentation
import com.intellij.psi.util.PsiTreeUtil
import org.raku.comma.RakuIcons
import org.raku.comma.extensions.RakuFrameworkCall
import org.raku.comma.psi.*
import java.lang.String
import javax.swing.Icon
import kotlin.Array
import kotlin.Boolean
import kotlin.IllegalArgumentException

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
        return structureElements.toArray<StructureViewTreeElement?>(StructureViewTreeElement.EMPTY_ARRAY)
    }

    override fun getPresentation(): ItemPresentation {
        if (calculatedPresentation != null) return calculatedPresentation

        if (element is RakuFile) {
            return object : ItemPresentation {
                override fun getPresentableText(): String? {
                    return element.getContainingFile().getName()
                }

                override fun getLocationString(): String? {
                    return null
                }

                override fun getIcon(b: Boolean): Icon? {
                    return RakuIcons.CAMELIA
                }
            }
        }
        if (element is RakuPackageDecl) return object : ItemPresentation {
            override fun getPresentableText(): String? {
                val pkg = element
                return pkg.getPackageName()
            }

            override fun getLocationString(): String? {
                return null
            }

            override fun getIcon(b: Boolean): Icon? {
                return RakuIcons.iconForPackageDeclarator(element.getPackageKind())
            }
        }
        if (element is RakuRegexDecl) return object : ItemPresentation {
            override fun getPresentableText(): String? {
                return element.getSignature()
            }

            override fun getLocationString(): String? {
                return null
            }

            override fun getIcon(b: Boolean): Icon? {
                return RakuIcons.REGEX
            }
        }
        if (element is RakuRoutineDecl) return object : ItemPresentation {
            override fun getPresentableText(): String? {
                return element.getSignature()
            }

            override fun getLocationString(): String? {
                return null
            }

            override fun getIcon(b: Boolean): Icon? {
                if (element.getRoutineKind() == "method") return RakuIcons.METHOD
                return RakuIcons.SUB
            }
        }
        if (element is RakuConstant) return object : ItemPresentation {
            override fun getPresentableText(): String? {
                return element.getConstantName()
            }

            override fun getLocationString(): String? {
                return null
            }

            override fun getIcon(b: Boolean): Icon? {
                return RakuIcons.CONSTANT
            }
        }
        if (element is RakuVariableDecl) return object : ItemPresentation {
            override fun getPresentableText(): String? {
                return String.join(", ", *element.getVariableNames())
            }

            override fun getLocationString(): kotlin.String? {
                return null
            }

            override fun getIcon(b: Boolean): Icon? {
                return RakuIcons.ATTRIBUTE
            }
        }
        if (element is RakuSubset) return object : ItemPresentation {
            override fun getPresentableText(): kotlin.String? {
                return element.getSubsetName()
            }

            override fun getLocationString(): kotlin.String? {
                return null
            }

            override fun getIcon(b: Boolean): Icon? {
                return RakuIcons.SUBSET
            }
        }
        if (element is RakuEnum) return object : ItemPresentation {
            override fun getPresentableText(): kotlin.String? {
                return element.getEnumName()
            }

            override fun getLocationString(): kotlin.String? {
                return null
            }

            override fun getIcon(b: Boolean): Icon? {
                return RakuIcons.ENUM
            }
        }
        throw IllegalArgumentException()
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
