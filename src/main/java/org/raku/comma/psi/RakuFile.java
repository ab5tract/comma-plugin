package org.raku.comma.psi;

import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.meta.PsiMetaOwner;
import org.raku.comma.psi.symbols.RakuSymbolCollector;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface RakuFile extends RakuPsiElement, RakuPsiScope, PsiNameIdentifierOwner, PsiFile, PsiMetaOwner {
    void contributeGlobals(RakuSymbolCollector collector, Set<String> seen);
    Map<Integer, List<Integer>> getStatementLineMap();
    boolean isReal();
    String renderPod();

    void setModuleName(String moduleName);
    String getModuleName();

    void setOriginalPath(String originalPath);
    String getOriginalPath();

    void setDependencyFile(Boolean dependencyFile);
    Boolean getDependencyFile();
}
