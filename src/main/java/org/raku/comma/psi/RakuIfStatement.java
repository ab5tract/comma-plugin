package org.raku.comma.psi;

public interface RakuIfStatement extends RakuExtractable, RakuConditional, RakuControl, RakuTopicalizer {
    String getLeadingStatementControl();
}
