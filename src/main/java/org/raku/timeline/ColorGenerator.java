package org.raku.timeline;

import java.awt.*;

public class ColorGenerator {
    private int nextHue = 0;

    public Color nextColor() {
        float hue = nextHue();
        return Color.getHSBColor(hue, 0.9f, 0.6f);
    }

    private float nextHue() {
        nextHue += 13;
        return (float)(nextHue % 100) / 100;
    }
}
