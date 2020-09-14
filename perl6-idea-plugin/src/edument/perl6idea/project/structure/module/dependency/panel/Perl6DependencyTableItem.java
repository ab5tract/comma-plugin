package edument.perl6idea.project.structure.module.dependency.panel;

import com.intellij.openapi.util.Comparing;

import java.util.Objects;

public class Perl6DependencyTableItem {
    protected String myEntry;
    protected Perl6DependencyScope myScope;

    protected Perl6DependencyTableItem(String dep, Perl6DependencyScope scope) {
        myEntry = dep;
        myScope = scope;
    }

    public String getEntry() {
        return myEntry;
    }

    public Perl6DependencyScope getScope() {
        return myScope;
    }

    public void setScope(Perl6DependencyScope scope) {
        myScope = scope;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Perl6DependencyTableItem item = (Perl6DependencyTableItem)obj;
        return Objects.equals(myEntry, item.myEntry) && Comparing.equal(myScope, item.getScope());
    }

    @Override
    public int hashCode() {
        return myEntry != null ? myEntry.hashCode() : 0;
    }

    @Override
    public String toString() {
        return myEntry;
    }
}
