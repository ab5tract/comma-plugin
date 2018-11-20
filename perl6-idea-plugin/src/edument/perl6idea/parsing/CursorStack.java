package edument.perl6idea.parsing;

import com.intellij.psi.tree.IElementType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a stack of Cursor objects, the top one being the current one in use.
 */
public class CursorStack {
    /* The stack of cursors, current one at the top. */
    public List<Cursor> cursors = new ArrayList<Cursor>();

    /* Target of the parse. */
    public CharSequence target;

    /* The current token we're going to produce. */
    public IElementType token;
    public int tokenStart;

    /* Arguments being passed between rules. */
    public Object[] args;

    /* Set of markers. */
    private Map<String, Integer> markers = new HashMap<>();

    /* Heredoc queue. */
    public List<Heredoc> heredocs = new ArrayList<>();

    /* Start of current heredoc delimiter being parsed. */
    public int heredocDelimStart = -1;

    /* If we're in a lookahed or not. */
    public int inLookahead = 0;

    public CursorStack(CharSequence target) {
        this.target = target;
    }

    public void push(Cursor c) {
        cursors.add(c);
    }

    public Cursor peek() {
        return cursors.get(cursors.size() - 1);
    }

    public Cursor pop() {
        int top = cursors.size() - 1;
        Cursor result = cursors.get(top);
        cursors.remove(top);
        if (top != 0)
            cursors.get(top - 1).lastResult = result;
        return result;
    }

    public boolean isEmpty() {
        return cursors.isEmpty();
    }

    public CursorStack snapshot() {
        for (Cursor c : this.cursors)
            c.freeze();
        CursorStack snapped = new CursorStack(this.target);
        snapped.cursors = new ArrayList<>(this.cursors);
        snapped.token = this.token;
        snapped.tokenStart = this.tokenStart;
        snapped.args = this.args;
        snapped.markers = new HashMap<>(this.markers);
        snapped.heredocs = new ArrayList<>(this.heredocs);
        snapped.heredocDelimStart = this.heredocDelimStart;
        return snapped;
    }

    public CursorStack resume(CharSequence target) {
        CursorStack res = new CursorStack(target);
        res.cursors = new ArrayList<>(this.cursors);
        res.token = this.token;
        res.tokenStart = this.tokenStart;
        res.args = this.args;
        res.markers = new HashMap<>(this.markers);
        res.heredocs = new ArrayList<>(this.heredocs);
        res.heredocDelimStart = this.heredocDelimStart;
        return res;
    }

    public void ensureTopNotFrozen() {
        int top = cursors.size() - 1;
        if (top >= 0)
            cursors.set(top, cursors.get(top).copyForStack(this));
    }

    public Object findDynamicVariable(String variableName) {
        int top = cursors.size() - 1;
        while (top >= 0) {
            Object found = cursors.get(top).getDynamicVariable(variableName);
            if (found != null)
                return found;
            top--;
        }
        return null;
    }

    public void assignDynamicVariable(String variableName, Object value) {
        int top = cursors.size() - 1;
        while (top >= 0) {
            if (cursors.get(top).getDynamicVariable(variableName) != null) {
                cursors.get(top).setDynamicVariable(variableName, value);
                return;
            }
            top--;
        }
        throw new RuntimeException("Dynamic variable " + variableName + " not found");
    }

    public void setMarker(String name, int pos) {
        markers.put(name, pos);
    }

    public int getMarker(String name) {
        return markers.getOrDefault(name, -1);
    }
}
