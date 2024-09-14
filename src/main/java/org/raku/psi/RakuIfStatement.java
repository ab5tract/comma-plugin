package org.raku.psi;

public interface RakuIfStatement extends RakuExtractable, RakuConditional, RakuControl, RakuTopicalizer {
    String getLeadingStatementControl();
}
