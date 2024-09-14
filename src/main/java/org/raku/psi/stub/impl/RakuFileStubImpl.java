package org.raku.psi.stub.impl;

import com.intellij.psi.stubs.PsiFileStubImpl;
import com.intellij.psi.stubs.Stub;
import com.intellij.psi.tree.IStubFileElementType;
import org.raku.parsing.RakuElementTypes;
import org.raku.psi.RakuFile;
import org.raku.psi.RakuPsiDeclaration;
import org.raku.psi.stub.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class RakuFileStubImpl extends PsiFileStubImpl<RakuFile> implements RakuFileStub {
    private final String compilationUnitName;

    public RakuFileStubImpl(RakuFile file, String compilationUnitName) {
        super(file);
        this.compilationUnitName = compilationUnitName;
    }

    @NotNull
    @Override
    public IStubFileElementType<?> getType() {
        return RakuElementTypes.FILE;
    }

    @Override
    public String getCompilationUnitName() {
        return compilationUnitName;
    }

    @Override
    public List<RakuPsiDeclaration> getExports() {
        List<RakuPsiDeclaration> exports = new ArrayList<>();
        List<Stub> toTry = new ArrayList<>();
        toTry.add(this);
        while (!toTry.isEmpty()) {
            Stub current = toTry.remove(0);
            for (Stub child : current.getChildrenStubs()) {
                if (child instanceof RakuDeclStub<?> declStub) {
                    if (declStub.isExported())
                        exports.add(declStub.getPsi());
                }
                toTry.add(child);
            }
        }
        return exports;
    }
}
