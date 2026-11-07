package spiel1;

import java.awt.*;
import java.util.ArrayList;

public class EnemyArea { // eine Klasse um gegner auf der Karte zu erzeugen

    int xpos;
    int ypos;
    ArrayList<Enemy> enemies = new ArrayList<>();

    public EnemyArea(int xpos){
        this.xpos=xpos;
    }

    public void generateEnemies(){//füllt den bereich mit gegnern
        double mindestAbstand;
        if(xpos>10){
            mindestAbstand=220*1.0*(xpos/10.0);//midestabstand zwischen gegner vergrößern wenn weiter rechts um schwieriger zu machen
        }else mindestAbstand= 150;
        for(Vector2 position : Bluenoise.generate(new Vector2(xpos*Spielfeld.AreaWidth,ypos*Spielfeld.AreaHeight),new Vector2((xpos+1)*Spielfeld.AreaWidth, (ypos+1)*Spielfeld.AreaWidth),mindestAbstand))
        {
           enemies.add(new Enemy(new Transform(position),17,17,5));
        }

    }

    public void update(){//aktualisiert positionen und lebensstatus der gegner
        for(Enemy enemy : enemies) enemy.moveGameObject(Spielfeld.getTimeMultiplyer());
        enemies.removeIf(Enemy::isDead);//https://stackoverflow.com/questions/13205773/how-to-destroy-an-object-in-java
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
