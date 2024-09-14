package org.raku.timeline;

import com.intellij.util.ui.JBUI;
import org.raku.timeline.model.Logged;
import org.raku.timeline.model.Task;
import org.raku.timeline.model.Event;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;
import java.util.Map;

public class TimelineTooltip extends JPanel {
    private static final Font defaultFont = JBUI.Fonts.label();
    private static final Font nameFont = defaultFont.deriveFont(Font.BOLD, (float)defaultFont.getSize() + 4);
    private static final Font pathFont = defaultFont.deriveFont((float)defaultFont.getSize() - 4);

    public TimelineTooltip(Logged logged) {
        super(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        GridBagConstraints cons = new GridBagConstraints();
        cons.insets = JBUI.insets(2, 4, 2, 4);
        int row = 0;

        // Describe the kind of event this is.
        cons.gridwidth = 2;
        cons.fill = GridBagConstraints.HORIZONTAL;
        JLabel path = new JLabel(logged.getModule() + " >> " + logged.getCategory());
        path.setFont(pathFont);
        cons.gridx = 0;
        cons.gridy = row++;
        add(path, cons);
        JLabel name = new JLabel(logged.getName());
        name.setFont(nameFont);
        cons.gridx = 0;
        cons.gridy = row++;
        add(name, cons);

        // Time information.
        cons.gridwidth = 1;
        if (logged instanceof Task task) {

            JLabel start = new JLabel("Start");
            start.setFont(defaultFont);
            cons.gridx = 0;
            cons.gridy = row++;
            add(start, cons);
            JLabel startValue = new JLabel(String.format("%.4f", task.getStart()) + "s");
            startValue.setFont(defaultFont);
            cons.gridx = 1;
            add(startValue, cons);

            JLabel duration = new JLabel("Duration");
            duration.setFont(defaultFont);
            cons.gridx = 0;
            cons.gridy = row++;
            add(duration, cons);
            double end = task.getEnd();
            JLabel durationValue = new JLabel(end == Double.POSITIVE_INFINITY
                    ? "Ongoing"
                    : String.format("%.4f", end - task.getStart()) + "s");
            durationValue.setFont(defaultFont);
            cons.gridx = 1;
            add(durationValue, cons);
        }
        else if (logged instanceof Event) {
            JLabel time = new JLabel("Time");
            time.setFont(defaultFont);
            cons.gridx = 0;
            cons.gridy = row++;
            add(time, cons);
            JLabel timeValue = new JLabel(String.format("%.4f", ((Event)logged).getWhen()) + "s");
            timeValue.setFont(defaultFont);
            cons.gridx = 1;
            add(timeValue, cons);
        }

        // Extra data.
        Map<String, Object> data = logged.getData();
        for (Map.Entry<String, Object> dataItem : data.entrySet()) {
            Object value = dataItem.getValue();
            if (value != null) {
                JLabel keyLabel = new JLabel(capitalize(dataItem.getKey()));
                keyLabel.setFont(defaultFont);
                cons.gridx = 0;
                cons.gridy = row++;
                add(keyLabel, cons);
                JLabel valueLabel = new JLabel(value.toString());
                valueLabel.setFont(defaultFont);
                cons.gridx = 1;
                add(valueLabel, cons);
            }
        }
    }

    private static String capitalize(String s) {
        return s.isEmpty() ? s : s.substring(0, 1).toUpperCase(Locale.ENGLISH) + s.substring(1);
    }
}
