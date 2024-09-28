package org.raku.comma.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;

/*
    This was originally introduced as a way to track is an element belongs to a
    project that `isRakudoCoreProject`, so that certain annotations could be skipped.

    This seemed like it would probably be too heavy, and indeed it was. The better
    solution turned out to be migrating all of those annotation to inspections.

    But rather than totally unwind this, let's leave this wrapper class here in case
    we ever do need to add something to all of our elements.

 */

public class RakuASTWrapperPsiElement extends ASTWrapperPsiElement {
    public RakuASTWrapperPsiElement(ASTNode node) { super(node); }
}
