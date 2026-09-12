package spiel1;

import java.awt.*;

public class RoundJoint extends Joint{
    private double size;
    public RoundJoint(double size) {
        super(0);
        this.size=size;
    }

    @Override
    protected void paintMe(Graphics2D g2d) {
        Vector2 jPos = getTransform().position.toJPanel();
        g2d.drawOval(
                (int) (jPos.x()- size/2),
                (int) (jPos.y() - size/2), // nach oben verschieben
                (int) size,
                (int) size
        );
        for(Joint joint : connectedJoints)joint.paintMe(g2d);//andere joints zeichnenx
    }
}
