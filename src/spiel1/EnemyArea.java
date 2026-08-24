package spiel1;

import java.awt.*;
import java.util.ArrayList;

public class EnemyArea { // eine Klasse um gegner auf der Karte zu erzeugen
    //TODO size korrekt einfügen, um performance zu verbessern/testen was am besten ist

   // Vector2 size; //temp
    int xpos;
    int ypos;
    ArrayList<Enemy> enemies = new ArrayList<>();

    public EnemyArea(int xpos){
      //  this.size=size; //temp
        this.xpos=xpos;
    }

    public void GenerateEnemies(){//füllt den bereich mit gegnern
        for(Vector2 position : Bluenoise.generate(new Vector2(xpos*Spielfeld.AreaWidth,ypos*Spielfeld.AreaHeight),new Vector2((xpos+1)*Spielfeld.AreaWidth, (ypos+1)*Spielfeld.AreaWidth)))
        {
           enemies.add(new Enemy(new Transform(position),15,15,5));
        }
    }

    public void update(){//aktualisiert positionen und lebensstatus der gegner
        for(Enemy enemy : enemies) enemy.moveGameObject(Spielfeld.getTimeMultiplyer());
        enemies.removeIf(Enemy::isDead);
    }

    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }

    public void setEnemies(ArrayList<Enemy> enemies) {
        this.enemies = enemies;
    }

    public int getXpos() {
        return xpos;
    }

    public void setXpos(int xpos) {
        this.xpos = xpos;
    }

    protected void paintMe(Graphics2D g2d) { // zeichnet alle Gegner
        for(Enemy enemy: enemies) {
            enemy.paintMe(g2d);
            enemy.getHitbox().paintMe(g2d);
        }
    }
}
