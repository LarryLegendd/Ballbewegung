package spiel1;

import java.awt.*;
import java.util.ArrayList;

public class EnemyArea {
    Vector2 size;
    int xpos;
    int ypos;
    ArrayList<Enemy> enemies = new ArrayList<>();
    ArrayList<Vector2> positions = new ArrayList<>();
    public EnemyArea(Vector2 size, int xpos, int ypos){
        this.size=size;
        this.xpos=xpos;
        this.ypos=ypos;
    }

    public void GenerateEnemies(){
        for(Vector2 position : Bluenoise.generate(new Vector2(xpos*10000,ypos*10000),new Vector2((xpos+1)*10000,(ypos+1)*10000)))
        {
           enemies.add(new Enemy(new Transform(position),10,10,5));
        }
    }

    public void update(){
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

    public int getYpos() {
        return ypos;
    }

    public void setYpos(int ypos) {
        this.ypos = ypos;
    }

    protected void paintMe(Graphics g) {//TODO bild für spieler
        for(Enemy enemy: enemies) {
            enemy.paintMe(g);
            enemy.getHitbox().paintMe(g);
        }
    }
}
