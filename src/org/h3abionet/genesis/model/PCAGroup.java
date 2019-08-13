package org.h3abionet.genesis.model;

import javafx.scene.paint.Color;

/**
 *
 * @author A Salmi
 */
public class PCAGroup {
    // Group color
    private Color color;
    // Group icon
    private Icons icon;

    public PCAGroup() {
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Icons getIcon() {
        return icon;
    }

    public void setIcon(Icons icon) {
        this.icon = icon;
    }
}
