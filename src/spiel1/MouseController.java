package spiel1;

import java.awt.*;

public interface MouseController {
    void cursorPlayer();
    void cursorSniper(double sichtbarkeit);
    void cursorX();
    void paintCursor(Graphics2D g2d);
}
