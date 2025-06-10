package org.raku.comma.structureView

import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.util.NlsSafe
import org.raku.comma.RakuIcons
import org.raku.comma.psi.*
import java.lang.String
import javax.swing.Icon
import kotlin.Boolean

fun rakuFileStructureElement(element: RakuFile) =
    object : ItemPresentation {
        override fun getPresentableText(): @NlsSafe kotlin.String? {
            return element.getContainingFile().getName()
        }

        override fun getLocationString(): @NlsSafe kotlin.String? {
            return null
        }

        override fun getIcon(b: Boolean): Icon? {
            return RakuIcons.CAMELIA
        }
    }

fun rakuPackageDeclStructureElement(element: RakuPackageDecl) =
    object : ItemPresentation {
        override fun getPresentableText(): @NlsSafe kotlin.String? {
            return element.getPackageName()
        }

        override fun getLocationString(): @NlsSafe kotlin.String? {
            return null
        }

        override fun getIcon(b: Boolean): Icon? {
            return RakuIcons.iconForPackageDeclarator(element.getPackageKind())
        }
    }

fun rakuRegexDeclStructureElement(element: RakuRegexDecl) =
    object : ItemPresentation {
        override fun getPresentableText(): @NlsSafe kotlin.String? {
            return element.getSignature()
        }

        override fun getLocationString(): @NlsSafe kotlin.String? {
            return null
        }

        override fun getIcon(b: Boolean): Icon? {
            return RakuIcons.REGEX
        }
    }

fun rakuRoutineDeclStructureElement(element: RakuRoutineDecl) =
    object : ItemPresentation {
        override fun getPresentableText(): @NlsSafe kotlin.String? {
            return element.getSignature()
        }

        override fun getLocationString(): @NlsSafe kotlin.String? {
            return null
        }

        override fun getIcon(b: Boolean): Icon? {
            if (element.getRoutineKind() == "method") return RakuIcons.METHOD
            return RakuIcons.SUB
        }
    }

fun rakuConstantStructureElement(element: RakuConstant) =
    object : ItemPresentation {
        override fun getPresentableText(): @NlsSafe kotlin.String? {
            return element.getConstantName()
        }

        override fun getLocationString(): @NlsSafe kotlin.String? {
            return null
        }

        override fun getIcon(b: Boolean): Icon? {
            return RakuIcons.CONSTANT
        }
    }

fun rakuVariableDeclStructureElement(element: RakuVariableDecl) =
    object : ItemPresentation {
        override fun getPresentableText(): @NlsSafe kotlin.String? {
            return String.join(", ", *element.getVariableNames())
        }

        override fun getLocationString(): kotlin.String? {
            return null
        }

        override fun getIcon(b: Boolean): Icon? {
            return RakuIcons.ATTRIBUTE
        }
    }

fun rakuSubsetStructureElement(element: RakuSubset) =
    object : ItemPresentation {
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

fun rakuEnumStructureElement(element: RakuEnum) =
    object : ItemPresentation {
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