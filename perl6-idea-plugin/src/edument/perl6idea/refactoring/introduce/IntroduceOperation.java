package edument.perl6idea.refactoring.introduce;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

public class IntroduceOperation {
    private final Project myProject;
    private final Editor myEditor;
    private final PsiFile myFile;
    private String myName;
    private final EnumSet<IntroduceHandler.InitPlace> myAvailableInitPlaces = EnumSet.of(IntroduceHandler.InitPlace.SAME_BLOCK);
    private IntroduceHandler.InitPlace myInitPlace = IntroduceHandler.InitPlace.SAME_BLOCK;
    private IntroduceHandler.InitPlace myInplaceInitPlace = IntroduceHandler.InitPlace.SAME_BLOCK;
    private Boolean myReplaceAll;
    private PsiElement myElement;
    private PsiElement myInitializer;
    private List<PsiElement> myOccurrences = Collections.emptyList();
    private Collection<String> mySuggestedNames;

    public IntroduceOperation(Project project, Editor editor,
                              PsiFile file, String name) {
        myProject = project;
        myEditor = editor;
        myFile = file;
        myName = name;
    }

    public PsiFile getFile() {
        return myFile;
    }

    public Editor getEditor() {
        return myEditor;
    }

    public void setElement(PsiElement element) {
        myElement = element;
    }

    public Project getProject() {
        return myProject;
    }

    public PsiElement getElement() {
        return myElement;
    }

    public void setInitializer(PsiElement initializer) {
        myInitializer = initializer;
    }

    public void setOccurrences(List<PsiElement> occurrences) {
        myOccurrences = occurrences;
    }

    public void setSuggestedNames(Collection<String> suggestedNames) {
        mySuggestedNames = suggestedNames;
    }

    public List<PsiElement> getOccurrences() {
        return myOccurrences;
    }

    public void setReplaceAll(boolean replaceAll) {
        myReplaceAll = replaceAll;
    }

    public Boolean isReplaceAll() {
        return myReplaceAll;
    }

    public PsiElement getInitializer() {
        return myInitializer;
    }

    public String getName() {
        return myName;
    }

    public Collection<String> getSuggestedNames() {
        return mySuggestedNames;
    }

    public void setName(String name) {
        myName = name;
    }
}
