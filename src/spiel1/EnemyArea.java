package spiel1;

import java.awt.*;

public class EnemyArea {
    Vector2 size;
    int xpos;
    int ypos;

    public EnemyArea(Vector2 size, int xpos, int ypos){
        this.size=size;
        this.xpos=xpos;
        this.ypos=ypos;
    }


    public int getXpos() {
        return xpos;
    }

    public void setXpos(int xpos) {
        this.xpos = xpos;
    }

    public int getYpos() {
        return ypos;
    }

    public void setYpos(int ypos) {
        this.ypos = ypos;
    }


}
