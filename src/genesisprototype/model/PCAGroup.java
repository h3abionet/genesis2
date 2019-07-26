package genesisprototype.model;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * project genesis2
 * Created by ayyoub on 2019-07-25.
 */
public class PCAGroup {
    // Group color
    private Color color;
    // Group icon
    private Image icon;

    public PCAGroup() {
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Image getIcon() {
        return icon;
    }

    public void setIcon(Image icon) {
        this.icon = icon;
    }
}
