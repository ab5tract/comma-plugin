package edument.perl6idea.profiler.ui;

import com.intellij.util.StringLenComparator;
import com.intellij.util.ui.JBUI;
import edument.perl6idea.profiler.model.Perl6ProfileCall;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

public class CallGraphTooltip extends JPanel {
    private final Perl6ProfileCallGraph.CallItem myCall;

    public CallGraphTooltip(Perl6ProfileCallGraph.CallItem call) {
        super(new BorderLayout());
        myCall = call;
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JLabel comp = new JLabel();
        comp.setFont(JBUI.Fonts.create(Font.MONOSPACED, comp.getFont().getSize()));
        String text = createText();
        comp.setText(text);
        add(comp, BorderLayout.CENTER);
    }

    private String createText() {
        StringJoiner joiner = new StringJoiner("\n");
        Perl6ProfileCall call = myCall.myCall;
        String name = call.name.isEmpty() ? "<anon>" : call.name;
        joiner.add(String.format("* %s (%s:%s)", name, call.filename, call.line));
        joiner.add(String.format("Total: %s μs", call.inclusiveTime));
        joiner.add(String.format("Entries: %s", call.entries));
        if (call.entries != 1)
            joiner.add(String.format("Per entry: %s μs", call.averageCallTime()));
        joiner.add(String.format("Inlined: %s%%", call.inlined()));
        joiner.add(String.format("Spesh: %s%%", call.spesh()));
        createCalleeTable(joiner, call);
        return String.format("<html>%s</html>",
                             processTags(joiner.toString()));
    }

    private static void createCalleeTable(StringJoiner joiner, Perl6ProfileCall call) {
        List<String> callees = call.calleesPercentageInfo();
        if (callees == null) {
            joiner.add("... expand node to see callees data...");
            return;
        }
        if (callees.size() == 0)
            return;
        Optional<String> maxString = callees.stream().max(StringLenComparator.getInstance());
        int horizontalBorderLength = maxString.get().length();
        joiner.add("Callee table:");
        joiner.add(" |" + StringUtils.repeat("=", horizontalBorderLength + 2) + "|");
        for (String callee : callees) {
            joiner.add(String.format(
                " | %s%s |", callee,
                StringUtils.repeat(" ", horizontalBorderLength - callee.length())));
        }
        joiner.add(" |" + StringUtils.repeat("=", horizontalBorderLength + 2) + "|");
    }

    private static String processTags(String string) {
        return string
            .replaceAll(" ", "&nbsp;")
            .replaceAll("<","&lt;")
            .replaceAll(">", "&gt;")
            .replaceAll("\n", "<br/>");
    }
}
