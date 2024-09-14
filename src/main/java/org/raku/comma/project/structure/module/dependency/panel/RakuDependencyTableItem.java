package org.raku.comma.project.structure.module.dependency.panel;

import com.intellij.openapi.util.Comparing;

import java.util.Objects;

public class RakuDependencyTableItem {
    protected String myEntry;
    protected RakuDependencyScope myScope;

    protected RakuDependencyTableItem(String dep, RakuDependencyScope scope) {
        myEntry = dep;
        myScope = scope;
    }

    public String getEntry() {
        return myEntry;
    }

    public RakuDependencyScope getScope() {
        return myScope;
    }

    public void setScope(RakuDependencyScope scope) {
        myScope = scope;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        RakuDependencyTableItem item = (RakuDependencyTableItem)obj;
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
